package com.raymondHariyono.playcut.presentation.screens.admin

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.UserProfile

data class AdminDashboardUiState(
    val isLoading: Boolean = true,
    val adminProfile: UserProfile? = null,
    val reservations: List<Reservation> = emptyList(),
    val isLoggedOut: Boolean = false,
    val snackbarMessage: String? = null,
    val errorMessage: String? = null
)