package com.example.listxml

import androidx.lifecycle.ViewModel
import com.example.listxml.data.DataProvider
import com.example.listxml.data.Films
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

class FilmViewModel(private val userName: String) : ViewModel() {

    private val _filmList = MutableStateFlow(DataProvider.filmList)
    val filmList: StateFlow<List<Films>> = _filmList

    private val _selectedFilm = MutableStateFlow<Films?>(null)
    val selectedFilm: StateFlow<Films?> = _selectedFilm

    init {
        Timber.d("[$userName] FilmViewModel initialized with ${_filmList.value.size} items")
    }

    fun selectFilm(film: Films) {
        Timber.d("[$userName] Film selected: ${film.title}")
        _selectedFilm.value = film
    }

    fun logDetailClick(film: Films) {
        Timber.d("[$userName] Detail button clicked for: ${film.title}")
    }

    fun logImdbClick(film: Films) {
        Timber.d("[$userName] IMDB button clicked for: ${film.title}")
    }

    fun getFilmByTitle(title: String): Films? {
        return _filmList.value.find { it.title == title }
    }
}