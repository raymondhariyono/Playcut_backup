package com.raymondHariyono.playcut.presentation.screens.auth.login

data class LoginUiState(
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val userRole: String? = null,
    val error: String? = null
)