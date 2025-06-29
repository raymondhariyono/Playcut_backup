package com.raymondHariyono.playcut.domain.usecase.auth

import com.raymondHariyono.playcut.domain.repository.AuthRepository

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            repository.logoutUser() // Kita akan tambahkan ini di Repository
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}