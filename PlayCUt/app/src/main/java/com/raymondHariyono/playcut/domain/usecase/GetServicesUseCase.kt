package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.model.Service
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow

class GetServicesUseCase(private val repository: BarbershopRepository) {
    operator fun invoke(): Flow<List<Service>> {
        return repository.getServices()
    }
}