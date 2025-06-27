package com.raymondHariyono.playcut.domain.usecase.reservation

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository

class UpdateReservationUseCase(
    private val repository: BarbershopRepository
) {
    suspend operator fun invoke(reservation: Reservation): Result<Unit> {
        return try {
            if (reservation.customerName.isBlank()) {
                throw IllegalArgumentException("Nama pelanggan wajib diisi.")
            }
            repository.updateReservation(reservation)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}