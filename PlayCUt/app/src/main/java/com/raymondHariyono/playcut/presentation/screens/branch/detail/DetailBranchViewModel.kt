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
            fetchBranchDetailsAndServices(branchId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID Cabang tidak valid.") }
        }
    }

    private fun fetchBranchDetailsAndServices(branchId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            combine(
                getBranchDetailsUseCase(branchId),
                getServicesUseCase()
            ) { branchDetails, serviceList ->
                val filteredBranch = branchDetails?.copy(
                    barbers = branchDetails.barbers.filter { barber ->
                        barber.status == "active"
                    }
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        branch = filteredBranch,
                        services = serviceList,
                        error = if (filteredBranch == null) "Cabang tidak ditemukan" else null
                    )
                }
            }.catch { e ->
                // Tangani error jika salah satu flow gagal
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }.collect() // Gunakan .collect() karena kita tidak me-return dari launchIn
        }
    }
}


class GetServicesUseCase(private val repository: BarbershopRepository) {
    operator fun invoke() = repository.getServices()
}