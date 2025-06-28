package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

data class AddBarberUiState(
    val isLoading: Boolean = false,
    val barberNameInput: String = "",
    val barberContactInput: String = "",
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isBarberAdded: Boolean = false // Untuk memicu navigasi setelah sukses
)