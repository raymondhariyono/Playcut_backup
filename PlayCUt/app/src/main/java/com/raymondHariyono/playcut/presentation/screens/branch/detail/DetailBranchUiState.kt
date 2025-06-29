package com.raymondHariyono.playcut.presentation.screens.branch.detail

import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.Service

data class DetailBranchUiState(
    val isLoading: Boolean = true,
    val branch: Branch? = null,
    val services: List<Service> = emptyList(),
    val reservations: List<Reservation> = emptyList(),
    val error: String? = null
)