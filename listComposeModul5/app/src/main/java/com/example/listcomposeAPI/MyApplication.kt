package com.example.listxml// Sesuaikan dengan namespace aplikasi Anda (dari build.gradle.kts)

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.listxml.data.local.AppDatabase
import com.example.listxml.data.local.MovieDao
import com.example.listxml.data.remote.ApiClient
import com.example.listxml.data.repository.MovieRepositoryImpl
import com.example.listxml.domain.repository.MovieRepository
import com.example.listxml.presentation.ui.FilmViewModelFactory
import timber.log.Timber

// Ini adalah ekstensi properti untuk DataStore, biarkan seperti ini
val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

class MyApplication : Application() {

    // --- PASTIKAN PROPERTI INI DIDEKLARASIKAN DI SINI ---
    // Database Room
    lateinit var database: AppDatabase
        private set // Membuat setter-nya private agar tidak bisa diubah dari luar

    // DAO untuk akses data film
    lateinit var movieDao: MovieDao
        private set

    // Implementasi repository film
    lateinit var movieRepository: MovieRepository
        private set

    // Factory untuk ViewModel
    lateinit var filmViewModelFactory: FilmViewModelFactory
        private set
    // ----------------------------------------------------

    override fun onCreate() {
        super.onCreate()

        // Inisialisasi Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "movie_database" // Nama database Anda
        ).build()

        // Inisialisasi DAO dari database
        movieDao = database.movieDao()

        // Inisialisasi MovieRepository
        movieRepository = MovieRepositoryImpl(
            movieService = ApiClient.movieService, // Ambil instance dari ApiClient
            movieDao = movieDao
        )

        // Inisialisasi ViewModel Factory
        filmViewModelFactory = FilmViewModelFactory(movieRepository)
    }
}