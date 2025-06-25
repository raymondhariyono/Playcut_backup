package com.raymondHariyono.playcut.domain.usecase

import kotlinx.coroutines.delay

class LogoutUseCase {
    suspend operator fun invoke(): Result<Unit> {
        delay(500)
        return Result.success(Unit)
    }
}