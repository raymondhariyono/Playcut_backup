package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

// Mendefinisikan semua state yang dibutuhkan oleh AddBarberPage
data class AddBarberUiState(
    val nameInput: String = "",
    val contactInput: String = "",
    val emailInput: String = "", // Untuk akun login
    val isLoading: Boolean = false,
    val addSuccess: Boolean = false, // Sinyal untuk navigasi setelah sukses
    val errorMessage: String? = null
)