package com.raymondHariyono.playcut.presentation.screens.admin.edit

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.Service
import java.util.Date

data class EditReservationUiState(
    val isLoading: Boolean = true,
    val reservation: Reservation? = null, // Reservasi yang sedang diedit
    val availableServices: List<Service> = emptyList(), // Daftar layanan yang tersedia
    val availableBarbers: List<Barber> = emptyList(), // Daftar barber di cabang tersebut (untuk memilih ulang barber)
    val availableTimes: List<String> = emptyList(), // Waktu yang tersedia untuk barber terpilih

    // Input fields untuk diedit
    val customerNameInput: String = "",
    val selectedService: String = "",
    val selectedBarberId: Int = 0,
    val selectedDate: String = "", // Format tanggal yang sama dengan bookingDate di Reservation
    val selectedTime: String = "",

    val updateResult: Result<Unit>? = null, // Hasil operasi update
    val errorMessage: String? = null,
    val isUpdated: Boolean = false // Flag untuk memicu navigasi setelah update berhasil
)