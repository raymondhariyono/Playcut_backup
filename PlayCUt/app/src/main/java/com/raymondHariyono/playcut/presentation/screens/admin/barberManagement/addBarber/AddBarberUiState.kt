package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.addBarber

data class AddBarberUiState(
    val nameInput: String = "",
    val contactInput: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val addSuccess: Boolean = false
)