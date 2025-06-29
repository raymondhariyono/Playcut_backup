package com.raymondHariyono.playcut.domain.usecase.reservation // Pastikan package ini

import com.raymondHariyono.playcut.domain.model.Service
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetServicesUseCase @Inject constructor(
    private val repository: BarbershopRepository
) {
    operator fun invoke(): Flow<List<Service>> {
        return repository.getServices()
    }
}