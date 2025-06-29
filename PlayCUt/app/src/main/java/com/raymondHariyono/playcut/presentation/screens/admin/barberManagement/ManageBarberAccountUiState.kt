package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.UserProfile

data class ManageBarberAccountsUiState(
    val isLoading: Boolean = true,
    val adminProfile: UserProfile.Admin? = null,
    val barbers: List<Barber> = emptyList(),
    val error: String? = null,
    val snackbarMessage: String? = null,
    val isRegisteringAccount: Boolean = false,

    val showAccountCredentialsDialog: Boolean = false,
    val barberSelectedForAccount: Barber? = null,
    val generatedAccountEmail: String? = null,
    val generatedAccountPassword: String? = null
)