package com.raymondHariyono.playcut.presentation.screens.branch.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SearchBranchViewModel @Inject constructor(
    private val getBranchesUseCase: GetBranchesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchBranchUiState())
    val uiState: StateFlow<SearchBranchUiState> = _uiState.asStateFlow()

    init {
        fetchBranches()
    }

    private fun fetchBranches() {
        _uiState.update { it.copy(isLoading = true) }

        getBranchesUseCase().onEach { branchList ->
            _uiState.update { currentState ->
                currentState.copy(
                    isLoading = false,
                    branches = branchList
                )
            }
        }.catch { e ->
            _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
        }.launchIn(viewModelScope)
    }
}