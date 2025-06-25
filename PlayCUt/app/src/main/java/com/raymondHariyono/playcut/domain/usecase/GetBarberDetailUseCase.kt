package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class GetBarberDetailsUseCase(private val repository: BarbershopRepository) {
    operator fun invoke(barberId: Int): Flow<Barber?> {
        return repository.getBarberById(barberId)
    }
}