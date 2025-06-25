// File: app/src/main/java/com/raymondHariyono/playcut/domain/usecase/GetUserProfileUseCase.kt
package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetUserProfileUseCase {
    operator fun invoke(): Flow<Result<UserProfile>> = flow {
        // Simulasi pengambilan data profil dari server/database
        delay(1000)

        // Data dummy untuk ditampilkan
        val dummyProfile = UserProfile(
            name = "Raymond Hariyono",
            email = "raymond@example.com",
            phoneNumber = "081234567890",
            photoUrl = "" // Kosongkan untuk placeholder
        )

        emit(Result.success(dummyProfile))
        // Untuk simulasi error, Anda bisa emit(Result.failure(Exception("...")))
    }
}