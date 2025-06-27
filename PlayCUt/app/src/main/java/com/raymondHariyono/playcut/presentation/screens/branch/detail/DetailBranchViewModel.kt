package com.raymondHariyono.playcut.presentation.screens.branch.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBranchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBranchDetailsUseCase: GetBranchDetailsUseCase,
    private val getServicesUseCase: GetServicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailBranchUiState())
    val uiState: StateFlow<DetailBranchUiState> = _uiState.asStateFlow()

    init {
        val branchId: Int = savedStateHandle.get<Int>("branchId") ?: -1
        if (branchId != -1) {
            fetchBranchDetails(branchId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID Cabang tidak valid.") }
        }
    }

    private fun fetchBranchDetails(branchId: Int) {
        viewModelScope. launch {
            combine(
                getBranchDetailsUseCase(branchId),
                getServicesUseCase()
            ) { branchDetails, serviceList ->
                // Update state dengan hasil gabungan
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        branch = branchDetails,
                        services = serviceList,
                        error = if (branchDetails == null) "Cabang tidak ditemukan" else null
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }.launchIn(this)
        }
    }
}

class GetServicesUseCase(private val repository: BarbershopRepository) {
    operator fun invoke() = repository.getServices()
}