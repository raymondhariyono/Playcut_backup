package com.raymondHariyono.playcut.presentation.screens.branch.search

import com.raymondHariyono.playcut.domain.model.Branch

data class SearchBranchUiState(
    val isLoading: Boolean = true,
    val branches: List<Branch> = emptyList(),
    val error: String? = null
)