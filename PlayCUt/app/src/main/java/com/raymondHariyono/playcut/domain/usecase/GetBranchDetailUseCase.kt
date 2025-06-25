package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.model.Branch
import kotlinx.coroutines.flow.Flow

class GetBranchDetailsUseCase(private val repository: BarbershopRepository) {
    operator fun invoke(branchId: Int): Flow<Branch?> {
        if (branchId == -1) {
            return kotlinx.coroutines.flow.flowOf(null)
        }
        return repository.getBranchById(branchId)
    }
}