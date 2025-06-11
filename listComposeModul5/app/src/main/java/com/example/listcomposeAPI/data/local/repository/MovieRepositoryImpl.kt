package com.example.listxml.data.repository

import com.example.listxml.data.local.MovieDao
import com.example.listxml.data.mapper.toDomain
import com.example.listxml.data.mapper.toEntity
import com.example.listxml.data.remote.MovieService
import com.example.listxml.domain.model.Movie
import com.example.listxml.domain.repository.MovieRepository
import com.example.listxml.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.IOException
import retrofit2.HttpException

class MovieRepositoryImpl(
    private val movieService: MovieService,
    private val movieDao: MovieDao
) : MovieRepository {

    private val TEMP_API_KEY = "0f698f1405a96c1d4bc587f999fa4b0b"

    override fun getPopularMovies(): Flow<Result<List<Movie>>> = flow {
        emit(Result.Loading<List<Movie>>()) // Pastikan tipe generik eksplisit

        val localMoviesFlow: Flow<List<Movie>> = movieDao.getAllMovies()
            .map { movieEntities ->
                movieEntities.toDomain()
            }

        try {
            Timber.d("Fetching popular movies from network...")
            val remoteMovieResponse = movieService.getPopularMovies(apiKey = TEMP_API_KEY)
            // Mapping dari List<MovieDto> ke List<Movie> (domain model)
            val remoteMoviesDomain: List<Movie> = remoteMovieResponse.results.toDomain()


            movieDao.clearAllMovies()
            movieDao.insertMovies(remoteMoviesDomain.toEntity())
            Timber.d("Successfully fetched from network and updated cache.")

            emitAll(localMoviesFlow.map { movies -> Result.Success(movies) })

        } catch (e: HttpException) {
            Timber.e(e, "Network error (HTTP): ${e.code()} - ${e.message()}")
            emitAll(localMoviesFlow.map { cachedMovies ->
                Result.Error(
                    message = "Gagal mengambil data (HTTP ${e.code()}). Menampilkan data cache.",
                    data = cachedMovies
                )
            })
        } catch (e: IOException) {
            Timber.e(e, "Network error (IO): ${e.message}")
            emitAll(localMoviesFlow.map { cachedMovies ->
                Result.Error(
                    message = "Tidak ada koneksi internet. Menampilkan data cache.",
                    data = cachedMovies
                )
            })
        } catch (e: Exception) {
            Timber.e(e, "Unexpected error fetching popular movies: ${e.message}")
            emitAll(localMoviesFlow.map { cachedMovies ->
                Result.Error(
                    message = "Terjadi kesalahan. Menampilkan data cache. Error: ${e.localizedMessage}",
                    data = cachedMovies
                )
            })
        }
    }

    override fun getMovieById(movieId: Int): Flow<Result<Movie?>> {
        return movieDao.getMovieById(movieId)
            .map { movieEntity ->
                if (movieEntity != null) {
                    Result.Success(movieEntity.toDomain()) // Map Entity ke Domain Model
                } else {
                    Result.Error("Movie with id $movieId not found in cache.", data = null)
                }
            }
    }
}