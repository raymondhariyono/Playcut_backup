package com.example.listxml.data

data class Films(
    val title: String,
    val year: String,
    val description: String,
    val image: Int = 0,
    val imdbUrl: String
)
