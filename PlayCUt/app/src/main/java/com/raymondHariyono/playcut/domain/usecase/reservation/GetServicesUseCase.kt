package com.raymondHariyono.playcut.domain.usecase.reservation // Pastikan package ini

import com.raymondHariyono.playcut.domain.model.Service
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject // <<< Tambahkan import ini jika belum ada

class GetServicesUseCase @Inject constructor( // <<< Tambahkan @Inject constructor
    private val repository: BarbershopRepository
) {
    operator fun invoke(): Flow<List<Service>> {
        return repository.getServices()
    }
}