package com.raymondHariyono.playcut.presentation.screens.auth.login

import com.raymondHariyono.playcut.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val userRole: UserRole? = null,
    val error: String? = null
)