package com.raymondHariyono.playcut.presentation.screens.home

import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.HomeService
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto


data class HomePageUiState(
    val isLoading: Boolean = true,
    val userName: String = "",
    val promotions: List<Branch> = emptyList(),
    val homeServices: List<HomeService> = emptyList(),
    val inspirations: List<UnsplashPhoto> = emptyList(),
    val error: String? = null
)