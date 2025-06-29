package com.raymondHariyono.playcut.presentation.screens.inspiration

import com.raymondHariyono.playcut.domain.model.UnsplashPhoto

data class InspirationUiState(
    val isLoading: Boolean = true,
    val photos: List<UnsplashPhoto> = emptyList(),
    val error: String? = null
)