package com.raymondHariyono.playcut.domain.usecase.branch

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow

data class BarberDetails(
    val barber: Barber,
    val branch: Branch
)

class GetBarberDetailsUseCase(private val repository: BarbershopRepository) {
    operator fun invoke(barberId: Int): Flow<BarberDetails?> {
        return repository.getBarberById(barberId)
    }
}