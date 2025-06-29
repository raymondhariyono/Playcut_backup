package com.raymondHariyono.playcut.presentation.screens.branch.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.admin.GetReservationsByBranchUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchDetailsUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.GetServicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailBranchViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getBranchDetailsUseCase: GetBranchDetailsUseCase,
    private val getServicesUseCase: GetServicesUseCase,
    private val getReservationsByBranchUseCase: GetReservationsByBranchUseCase
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


            getBranchDetailsUseCase(branchId).flatMapLatest { branchDetails ->
                if (branchDetails == null) {

                    flowOf(
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Cabang tidak ditemukan"
                            )
                        }
                    )
                    return@flatMapLatest emptyFlow()
                }

                combine(
                    getServicesUseCase(),
                    getReservationsByBranchUseCase(branchDetails.name)
                ) { serviceList, reservationList ->
                    val filteredBranch = branchDetails.copy(
                        barbers = branchDetails.barbers.filter { barber ->
                            barber.status == "active"
                        }
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            branch = filteredBranch,
                            services = serviceList,
                            reservations = reservationList,
                            error = null
                        )
                    }
                }
            }.catch { e ->

                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }.collect()
        }
    }
}