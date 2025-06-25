package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow

class GetReservationsUseCase(private val repository: BarbershopRepository) {
    operator fun invoke(): Flow<List<Reservation>> {
        return repository.getReservations()
    }
}