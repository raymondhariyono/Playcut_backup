package com.raymondHariyono.playcut.presentation.screens.booking

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.usecase.branch.BarberDetails

data class BookingUiState(
    val isLoading: Boolean = true,
    val barberDetails: BarberDetails? = null,
    val existingReservations: List<Reservation> = emptyList(),

    // State untuk Mode Edit
    val isEditMode: Boolean = false,
    val canEditTime: Boolean = true,
    val reservationToEdit: Reservation? = null,

    // State untuk Input
    val customerName: String = "",
    val selectedMainService: String = "",
    val selectedOtherServices: List<String> = emptyList(),
    val selectedTime: String = "",

    // State untuk Hasil Aksi
    val bookingResult: Result<Unit>? = null,
    val updateResult: Result<Unit>? = null,

    // State untuk error handling
    val error: String? = null
)