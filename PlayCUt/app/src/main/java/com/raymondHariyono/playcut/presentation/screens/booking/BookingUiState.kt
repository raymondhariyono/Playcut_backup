package com.raymondHariyono.playcut.presentation.screens.booking

import com.raymondHariyono.playcut.domain.model.Barber

data class BookingUiState(
    val isLoading: Boolean = true,
    val barber: Barber? = null,
    val selectedTime: String = "",
    val selectedService: String = "",
    val bookingResult: Result<Unit>? = null,
    val error: String? = null
)