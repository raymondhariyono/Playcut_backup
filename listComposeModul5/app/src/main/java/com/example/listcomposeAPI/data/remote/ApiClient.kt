package com.example.listxml.data.remote // Pastikan package sudah benar

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object ApiClient {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    // Konfigurasi Json untuk KotlinX Serialization
    private val json = Json {
        ignoreUnknownKeys = true // Mengabaikan field JSON yang tidak ada di data class
        coerceInputValues = true // Memaksa nilai default jika ada null untuk non-nullable (hati-hati)
    }

    @OptIn(ExperimentalSerializationApi::class)
    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val movieService: MovieService by lazy {
        retrofit.create(MovieService::class.java)
    }
}