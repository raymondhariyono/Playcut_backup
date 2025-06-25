package com.raymondHariyono.playcut.presentation.screens.reservation

import com.raymondHariyono.playcut.domain.model.Reservation

data class YourReservationUiState(
    val isLoading: Boolean = true,
    val reservations: List<Reservation> = emptyList(),
    val error: String? = null
)