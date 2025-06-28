package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement // Pastikan package ini sesuai

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.usecase.admin.AddBarberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBarberViewModel @Inject constructor(
    private val addBarberUseCase: AddBarberUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBarberUiState())
    val uiState: StateFlow<AddBarberUiState> = _uiState.asStateFlow()

    fun onBarberNameChange(name: String) {
        _uiState.update { it.copy(barberNameInput = name, errorMessage = null, successMessage = null) }
    }

    fun onBarberContactChange(contact: String) {
        _uiState.update { it.copy(barberContactInput = contact, errorMessage = null, successMessage = null) }
    }


    fun onAddBarberClick(branchId: Int) { // Ubah nama fungsi agar lebih jelas
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

            val newBarber = Barber(
                id = (System.currentTimeMillis() % 1000).toInt(), // ID sederhana, bisa diubah jika ada kebutuhan ID unik yang lebih kuat
                name = _uiState.value.barberNameInput.trim(),
                contact = _uiState.value.barberContactInput.trim(),
                imageRes = "placeholder_barber",
                availableTimes = listOf("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00") // Default
            )

            val result = addBarberUseCase(branchId, newBarber)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Barber '${newBarber.name}' berhasil ditambahkan!",
                        isBarberAdded = true, // Set true untuk memicu navigasi
                        barberNameInput = "", // Bersihkan input
                        barberContactInput = "" // Bersihkan input
                    )
                }
            }.onFailure { e ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Terjadi kesalahan yang tidak diketahui."
                    )
                }
            }
        }
    }

    // Fungsi untuk mengonsumsi pesan agar tidak tampil berulang
    fun messageShown() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    // Fungsi untuk mereset flag navigasi
    fun barberAddedNavigated() {
        _uiState.update { it.copy(isBarberAdded = false) }
    }
}