package com.raymondHariyono.playcut.domain.usecase.reservation

import android.util.Log
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject

data class BookingData(
    val barberId: Int,
    val barberName: String,
    val branchName: String,
    val customerName: String,
    val services: List<String>,
    val time: String,
)

class CreateBookingUseCase @Inject constructor(
    private val repository: BarbershopRepository
) {
    suspend operator fun invoke(bookingData: BookingData): Result<Unit> {
        return try {
            if (bookingData.customerName.isBlank()) {
                throw IllegalArgumentException("Nama pelanggan wajib diisi.")
            }
            if (bookingData.services.isEmpty()) {
                throw IllegalArgumentException("Minimal pilih satu layanan.")
            }
            if (bookingData.time.isBlank()) {
                throw IllegalArgumentException("Waktu harus dipilih.")
            }

            val newReservation = Reservation(
                id = UUID.randomUUID().toString(),
                barberId = bookingData.barberId,
                barberName = bookingData.barberName,
                branchName = bookingData.branchName,
                customerName = bookingData.customerName,
                service = bookingData.services.joinToString(", "),
                bookingTime = bookingData.time,
                status = "Confirmed"
            )

            repository.saveReservation(newReservation)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("CreateBookingUseCase", "Booking gagal: ${e.message}")
            Result.failure(e)
        }
    }
}