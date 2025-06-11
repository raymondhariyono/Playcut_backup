package com.example.listxml.domain.repository

import com.example.listxml.domain.model.Movie
import com.example.listxml.domain.util.Result
import kotlinx.coroutines.flow.Flow
interface MovieRepository {
    fun getPopularMovies(): Flow<Result<List<Movie>>>

    fun getMovieById(movieId: Int): Flow<Result<Movie?>>
}
