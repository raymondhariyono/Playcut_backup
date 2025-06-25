package com.raymondHariyono.playcut.presentation.screens.reservation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.data.repository.BarbershopRepositoryImpl
import com.raymondHariyono.playcut.domain.usecase.GetReservationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class YourReservationViewModel @Inject constructor(
    private val getReservationsUseCase: GetReservationsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(YourReservationUiState())
    val uiState: StateFlow<YourReservationUiState> = _uiState.asStateFlow()

    init {
        fetchReservations()
    }

    private fun fetchReservations() {
        getReservationsUseCase().onEach { reservationList ->
            _uiState.update {
                it.copy(isLoading = false, reservations = reservationList)
            }
        }.catch { e ->
            _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
        }.launchIn(viewModelScope)
    }
}