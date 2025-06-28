package com.raymondHariyono.playcut.presentation.screens.admin.edit

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.GetReservationByIdUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchDetailsUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.GetServicesUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.UpdateReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReservationViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getReservationByIdUseCase: GetReservationByIdUseCase,
    private val updateReservationUseCase: UpdateReservationUseCase,
    private val getServicesUseCase: GetServicesUseCase, // Untuk daftar layanan
    private val getBranchDetailsUseCase: GetBranchDetailsUseCase // Untuk daftar barber di cabang
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditReservationUiState())
    val uiState: StateFlow<EditReservationUiState> = _uiState.asStateFlow()

    init {
        val reservationId: String? = savedStateHandle["reservationId"]
        if (reservationId != null) {
            loadReservationData(reservationId)
        } else {
            _uiState.update { it.copy(isLoading = false, errorMessage = "ID Reservasi tidak valid.") }
        }
    }
    private fun loadReservationData(reservationId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val reservation = try {
                    getReservationByIdUseCase(reservationId).first()
                } catch (e: Exception) {
                    Log.e("EditReservationVM", "Gagal mengambil reservasi: ${e.message}", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal mengambil data reservasi.") }
                    return@launch
                }

                if (reservation == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Reservasi tidak ditemukan.") }
                    return@launch
                }

                // Ambil daftar layanan
                val services = try {
                    getServicesUseCase().first()
                } catch (e: Exception) {
                    Log.e("EditReservationVM", "Gagal mengambil layanan: ${e.message}", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal mengambil layanan.") }
                    return@launch
                }

                // Ambil detail cabang untuk mendapatkan daftar barber
                val branchId = getBranchIdFromName(reservation.branchName)
                val branch = try {
                    getBranchDetailsUseCase(branchId).first()
                } catch (e: Exception) {
                    Log.e("EditReservationVM", "Gagal mengambil cabang: ${e.message}", e)
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal mengambil data cabang.") }
                    return@launch
                }

                val barbersInBranch = branch?.barbers ?: emptyList()
                val selectedBarber = barbersInBranch.find { it.id == reservation.barberId }

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        reservation = reservation,
                        availableServices = services,
                        availableBarbers = barbersInBranch,
                        customerNameInput = reservation.customerName,
                        selectedService = reservation.service,
                        selectedBarberId = reservation.barberId,
                        selectedDate = reservation.bookingDate,
                        selectedTime = reservation.bookingTime,
                        availableTimes = selectedBarber?.availableTimes ?: emptyList()
                    )
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal memuat reservasi.") }
                Log.e("EditReservationVM", "Error loading reservation: ${e.message}", e)
            }
        }
    }

    // Helper untuk mendapatkan branchId dari branchName (perlu disesuaikan jika tidak ada mapping langsung)
    private fun getBranchIdFromName(branchName: String): Int {
        return branchName.replace(" ", "").replace(".", "").filter { it.isDigit() }.toIntOrNull() ?: -1
        // Atau Anda bisa melakukan query ke koleksi 'branches' untuk mencari ID berdasarkan nama
    }

    // --- Event Handlers untuk perubahan input ---
    fun onCustomerNameChange(name: String) {
        _uiState.update { it.copy(customerNameInput = name, errorMessage = null, updateResult = null) }
    }

    fun onServiceSelected(service: String) {
        _uiState.update { it.copy(selectedService = service, errorMessage = null, updateResult = null) }
    }

    fun onBarberSelected(barberId: Int) {
        viewModelScope.launch {
            val selectedBarber = _uiState.value.availableBarbers.find { it.id == barberId }
            _uiState.update {
                it.copy(
                    selectedBarberId = barberId,
                    availableTimes = selectedBarber?.availableTimes ?: emptyList(),
                    selectedTime = "", // Reset waktu jika barber berubah
                    errorMessage = null,
                    updateResult = null
                )
            }
        }
    }

    fun onDateSelected(date: String) { // Jika Anda ingin mengimplementasikan pemilih tanggal
        _uiState.update { it.copy(selectedDate = date, errorMessage = null, updateResult = null) }
    }

    fun onTimeSelected(time: String) {
        _uiState.update { it.copy(selectedTime = time, errorMessage = null, updateResult = null) }
    }

    fun onConfirmUpdateClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, updateResult = null) }

            val currentState = _uiState.value
            val originalReservation = currentState.reservation ?: run {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Reservasi tidak valid untuk diperbarui.") }
                return@launch
            }

            // Dapatkan nama barber yang dipilih
            val selectedBarber = currentState.availableBarbers.find { it.id == currentState.selectedBarberId }
            val newBarberName = selectedBarber?.name ?: originalReservation.barberName

            val updatedReservation = originalReservation.copy(
                customerName = currentState.customerNameInput,
                service = currentState.selectedService,
                barberId = currentState.selectedBarberId,
                barberName = newBarberName,
                bookingDate = currentState.selectedDate,
                bookingTime = currentState.selectedTime,
                // userId, branchName, status harusnya tetap sama
            )

            val result = updateReservationUseCase(updatedReservation)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, updateResult = result, isUpdated = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal memperbarui reservasi.") }
                Log.e("EditReservationVM", "Error updating reservation: ${e.message}", e)
            }
        }
    }

    fun updateResultConsumed() {
        _uiState.update { it.copy(updateResult = null) }
    }

    fun navigatedAfterUpdate() {
        _uiState.update { it.copy(isUpdated = false) }
    }
}