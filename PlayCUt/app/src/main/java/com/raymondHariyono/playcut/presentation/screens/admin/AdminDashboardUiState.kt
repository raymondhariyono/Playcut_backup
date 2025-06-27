package com.raymondHariyono.playcut.presentation.screens.admin

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.UserProfile

data class AdminDashboardUiState(
    val isLoading: Boolean = true,
    val adminProfile: UserProfile? = null,
    val reservationsForBranch: List<Reservation> = emptyList(),
    val error: String? = null
)