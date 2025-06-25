package com.example.listxml

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeContent(navController: NavController, filmViewModel: FilmViewModel) {
    val films by filmViewModel.filmList.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        items(films) { film ->
            FilmListItems(films = film, navController = navController, viewModel = filmViewModel)
        }
    }
}
