// File: app/src/main/java/com/raymondHariyono/playcut/domain/usecase/auth/GetUserProfileUseCase.kt
package com.raymondHariyono.playcut.domain.usecase.auth

import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import javax.inject.Inject

// Hilt akan menyediakan AuthRepository ke UseCase ini
class GetUserProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    // Gunakan 'suspend operator' agar bisa dipanggil sebagai fungsi dari coroutine
    suspend operator fun invoke(): Result<UserProfile?> {
        return try {
            val profile = repository.getCurrentUserProfile()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}