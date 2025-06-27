package com.raymondHariyono.playcut.domain.usecase.auth

import android.util.Patterns
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import kotlinx.coroutines.delay

data class LoginCredentials(
    val email: String,
    val pass: String
)

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(credentials: LoginCredentials): Result<Unit> {
        if (!Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()) {
            return Result.failure(Exception("Format email tidak valid."))
        }
        if (credentials.pass.isBlank()) {
            return Result.failure(Exception("Password tidak boleh kosong."))
        }
        return authRepository.loginUser(credentials)
    }
}
