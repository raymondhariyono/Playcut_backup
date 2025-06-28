package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Reservation

data class BarberDashboardUiState(
    val isLoading: Boolean = true,
    val barberProfile: Barber? = null,
    val reservations: List<Reservation> = emptyList(),
    val error: String? = null
)