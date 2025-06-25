package com.raymondHariyono.playcut.presentation.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.data.repository.BarbershopRepositoryImpl
import com.raymondHariyono.playcut.domain.usecase.BookingData
import com.raymondHariyono.playcut.domain.usecase.CreateBookingUseCase
import com.raymondHariyono.playcut.domain.usecase.GetBarberDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val getBarberDetailsUseCase: GetBarberDetailsUseCase,
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    fun loadBarber(barberId: Int) {
        if (barberId <= 0) {
            _uiState.update { it.copy(isLoading = false, error = "ID Barber tidak valid.") }
            return
        }

        getBarberDetailsUseCase(barberId).onEach { barber ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    barber = barber,
                    error = if (barber == null) "Barber tidak ditemukan." else null
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onServiceSelected(service: String) {
        _uiState.update { it.copy(selectedService = service) }
    }

    fun onTimeSelected(time: String) {
        _uiState.update { it.copy(selectedTime = time) }
    }

    fun onConfirmBooking() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val barberName = currentState.barber?.name ?: "Unknown Barber"

            val result = createBookingUseCase(
                BookingData(
                    barberName = barberName,
                    service = currentState.selectedService,
                    time = currentState.selectedTime
                )
            )
            _uiState.update { it.copy(bookingResult = result) }
        }
    }

    fun bookingResultConsumed() {
        _uiState.update { it.copy(bookingResult = null) }
    }
}
