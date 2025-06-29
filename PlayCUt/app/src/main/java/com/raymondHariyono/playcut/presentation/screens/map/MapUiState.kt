package com.raymondHariyono.playcut.presentation.screens.map

import com.raymondHariyono.playcut.domain.model.Branch

data class MapUiState(
    val branches: List<Branch> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)