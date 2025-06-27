package com.raymondHariyono.playcut.presentation.screens.reservation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.reservation.DeleteReservationUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.GetReservationsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class YourReservationViewModel @Inject constructor(
    private val getReservationsUseCase: GetReservationsUseCase,
    private val deleteReservationUseCase: DeleteReservationUseCase
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

    fun onCancelReservation(reservationId: String) {
        viewModelScope.launch {
            // Panggil instance 'deleteReservationUseCase' yang sudah di-inject
            deleteReservationUseCase(reservationId)
                .onSuccess {
                    // Logika jika berhasil (tidak perlu melakukan apa-apa karena Flow akan otomatis update)
                    Log.d("ReservationVM", "Reservasi ID $reservationId berhasil dihapus.")
                }
                .onFailure { error ->
                    // Logika jika gagal
                    Log.e("ReservationVM", "Gagal menghapus reservasi", error)
                    // Anda bisa menambahkan _uiState.update untuk menampilkan error di Snackbar jika perlu

                }
        }
    }

    fun onCancelReservationClick(reservationId: String) {
        // Kita hanya set ID nya, untuk memicu dialog di UI
        _uiState.update { it.copy(reservationToDeleteId = reservationId) }
    }

    fun onConfirmDeletion() {
        _uiState.value.reservationToDeleteId?.let { idToDelete ->
            viewModelScope.launch {
                deleteReservationUseCase(idToDelete)
                    .onSuccess { Log.d("ReservationVM", "Reservasi ID $idToDelete berhasil dihapus.") }
                    .onFailure { error -> Log.e("ReservationVM", "Gagal menghapus reservasi", error) }

                // Setelah selesai, sembunyikan dialog
                onDismissDialog()
            }
        }
    }

    // 3. Dipanggil saat dialog ditutup (cancel atau klik di luar)
    fun onDismissDialog() {
        _uiState.update { it.copy(reservationToDeleteId = null) }
    }
}
