package com.example.listxml.data.remote // Pastikan package sudah benar

import com.example.listxml.data.remote.dto.MovieResponse // Import DTO
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("language") language: String = "en-US",
        @Query("page") page: Int = 1
    ): MovieResponse
}