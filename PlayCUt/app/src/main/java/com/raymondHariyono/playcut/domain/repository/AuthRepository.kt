package com.raymondHariyono.playcut.domain.repository

import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.usecase.auth.LoginCredentials
import com.raymondHariyono.playcut.domain.usecase.auth.RegisterCredentials

interface AuthRepository {

    suspend fun loginUser(
        credentials: LoginCredentials
    ): Result<Unit>

    suspend fun registerUser(
        credentials: RegisterCredentials
    ): Result<Unit>

    suspend fun getCurrentUserProfile(): UserProfile?

    suspend fun logoutUser(): Result<Unit>
}