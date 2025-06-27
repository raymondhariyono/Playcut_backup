package com.raymondHariyono.playcut.domain.usecase.admin

import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow

class GetReservationsByBranchUseCase(
    private val repository: BarbershopRepository
) {
    operator fun invoke(branchName: String): Flow<List<Reservation>> {
        return repository.getReservationsByBranch(branchName)
    }
}