package com.raymondHariyono.playcut.domain.usecase.branch

import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.model.Branch
import kotlinx.coroutines.flow.Flow

// Nantinya kita akan inject repository menggunakan Hilt
class GetBranchesUseCase(
    private val repository: BarbershopRepository
) {
    operator fun invoke(): Flow<List<Branch>> {
        return repository.getBranches()
    }
}