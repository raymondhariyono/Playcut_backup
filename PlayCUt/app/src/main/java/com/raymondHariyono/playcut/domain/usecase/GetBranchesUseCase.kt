package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.model.Branch
import kotlinx.coroutines.flow.Flow

// Nantinya kita akan inject repository menggunakan Hilt
class GetBranchesUseCase(
    private val repository: BarbershopRepository
) {
    /**
     * Operator 'invoke' memungkinkan kita memanggil kelas ini seolah-olah sebuah fungsi.
     * Contoh: getBranchesUseCase()
     */
    operator fun invoke(): Flow<List<Branch>> {
        return repository.getBranches()
    }
}