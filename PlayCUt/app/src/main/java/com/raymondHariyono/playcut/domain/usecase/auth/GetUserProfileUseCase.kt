package com.raymondHariyono.playcut.domain.usecase.auth

import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository

class GetUserProfileUseCase(private val repository: AuthRepository) {
    // Use case ini sekarang menjadi sangat sederhana
    suspend operator fun invoke(): UserProfile? {
        return repository.getCurrentUserProfile()
    }
}