package com.raymondHariyono.playcut.domain.usecase.reservation

import com.raymondHariyono.playcut.domain.repository.BarbershopRepository

class DeleteReservationUseCase(
    private val repository: BarbershopRepository
) {
    suspend operator fun invoke(reservationId: String): Result<Unit> {
        return try {
            if (reservationId.isBlank()) {
                throw IllegalArgumentException("Reservation ID tidak boleh kosong.")
            }
            repository.deleteReservation(reservationId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}