package com.raymondHariyono.playcut.presentation.screens.auth.register

data class RegisterUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val pass: String = "",
    val isLoading: Boolean = false,
    val registerSuccess: Boolean = false,
    val error: String? = null
)