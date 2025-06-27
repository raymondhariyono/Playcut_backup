package com.raymondHariyono.playcut.domain.usecase.auth

import android.util.Patterns
import com.raymondHariyono.playcut.domain.repository.AuthRepository

data class RegisterCredentials(
    val name: String,
    val username: String,
    val email: String,
    val pass: String
)

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: RegisterCredentials): Result<Unit> {
        if (credentials.name.isBlank() || credentials.username.isBlank()) {
            return Result.failure(Exception("Nama dan username wajib diisi."))
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()) {
            return Result.failure(Exception("Format email tidak valid."))
        }
        if (credentials.pass.length < 6) {
            return Result.failure(Exception("Password minimal 6 karakter."))
        }

        return authRepository.registerUser(credentials)
    }
}