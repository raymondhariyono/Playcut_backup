package com.raymondHariyono.playcut.domain.usecase

import android.util.Log
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.model.Reservation
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.Date
import java.util.UUID

    
data class BookingData(
    val barberName: String,
    val service: String,
    val time: String,

)

class CreateBookingUseCase(
    // Tambahkan repository sebagai dependensi
    private val repository: BarbershopRepository
) { suspend operator fun invoke(bookingData: BookingData): Result<Unit> {
        return try {
            if (bookingData.service.isBlank() || bookingData.time.isBlank()) {
                throw IllegalArgumentException("Layanan dan waktu harus dipilih.")
            }

            val newReservation = Reservation(
                id = UUID.randomUUID().toString(),
                bookingDate = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date()),
                bookingTime = bookingData.time,
                service = bookingData.service,
                barberName = bookingData.barberName,
                customerName = "Pengguna", // Placeholder
                branchName = "Cabang Pusat", // Placeholder
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