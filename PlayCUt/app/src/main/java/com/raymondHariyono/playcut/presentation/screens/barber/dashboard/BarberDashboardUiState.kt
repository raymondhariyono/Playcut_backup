package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.UserProfile

data class BarberDashboardUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val barberProfile: UserProfile.Barber? = null,
    val reservations: List<Reservation> = emptyList()
)