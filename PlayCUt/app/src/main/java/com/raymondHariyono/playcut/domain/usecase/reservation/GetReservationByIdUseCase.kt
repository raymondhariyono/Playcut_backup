package com.raymondHariyono.playcut.domain.usecase.reservation

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow

class GetReservationByIdUseCase(
    private val repository: BarbershopRepository
) {
    operator fun invoke(id: String): Flow<Reservation?> {
        return repository.getReservationById(id)
    }
}