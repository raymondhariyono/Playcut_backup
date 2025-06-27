package com.raymondHariyono.playcut.domain.usecase.admin

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import javax.inject.Inject

class AddBarberUseCase @Inject constructor(
    private val repository: BarbershopRepository
) {
    suspend operator fun invoke(branchId: Int, barber: Barber): Result<Unit> {
        // Lakukan validasi di sini jika perlu
        if (barber.name.isBlank()) {
            return Result.failure(IllegalArgumentException("Nama barber tidak boleh kosong."))
        }
        return repository.addBarberToBranch(branchId, barber)
    }
}