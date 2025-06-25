package com.raymondHariyono.playcut.presentation.screens.profile

import com.raymondHariyono.playcut.domain.model.UserProfile

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userProfile: UserProfile? = null,
    val isLoggedOut: Boolean = false,
    val error: String? = null
)