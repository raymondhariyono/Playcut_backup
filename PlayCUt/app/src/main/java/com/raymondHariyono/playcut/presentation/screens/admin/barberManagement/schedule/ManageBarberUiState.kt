package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.schedule

import com.raymondHariyono.playcut.domain.model.Branch

data class ManageBarbersUiState(
    val isLoading: Boolean = true,
    val branch: Branch? = null,
    val error: String? = null,
    val successMessage: String? = null
)