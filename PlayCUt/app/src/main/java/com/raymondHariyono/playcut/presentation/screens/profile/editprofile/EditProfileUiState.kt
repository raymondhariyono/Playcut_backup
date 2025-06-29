package com.raymondHariyono.playcut.presentation.screens.profile.editprofile

import com.raymondHariyono.playcut.domain.model.UserProfile

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isUploading: Boolean = false,
    val userProfile: UserProfile = UserProfile.Unknown,
    val successMessage: String? = null,
    val error: String? = null,
    val navigateToLogin: Boolean = false
)