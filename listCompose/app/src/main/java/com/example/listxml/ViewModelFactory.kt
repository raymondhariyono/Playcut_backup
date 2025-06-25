package com.example.listxml

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class FilmViewModelFactory(private val userName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilmViewModel(userName) as T
    }
}