package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.addBarber

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.admin.AddBarberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBarberViewModel @Inject constructor(
    private val addBarberUseCase: AddBarberUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddBarberUiState())
    val uiState = _uiState.asStateFlow()

    private val branchId: Int = savedStateHandle.get<Int>("branchId") ?: -1

    fun onNameChange(name: String) { _uiState.update { it.copy(nameInput = name, errorMessage = null) } }
    fun onContactChange(contact: String) { _uiState.update { it.copy(contactInput = contact, errorMessage = null) } }
    // Hapus onBarberAuthUidChange jika ada

    fun onAddBarberClick() {
        if (branchId == -1) {
            _uiState.update { it.copy(errorMessage = "Error: ID Cabang tidak ditemukan.") }
            return
        }

        viewModelScope.launch {
            val name = _uiState.value.nameInput.trim()
            val contact = _uiState.value.contactInput.trim()

            // Hapus logika pembuatan emailForAuth di sini

            // Validasi Sederhana
            if (name.isBlank() || contact.isBlank()) {
                _uiState.update { it.copy(errorMessage = "Nama dan Kontak wajib diisi.") }
                return@launch
            }
            if (!name.matches(Regex("^[a-zA-Z ]+\$"))) {
                _uiState.update { it.copy(errorMessage = "Nama hanya boleh berisi huruf dan spasi.") }
                return@launch
            }
            if (!contact.all { it.isDigit() }) {
                _uiState.update { it.copy(errorMessage = "Nomor kontak hanya boleh berisi angka.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = addBarberUseCase(
                branchId = branchId,
                name = name,
                contact = contact
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, addSuccess = true) }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    fun consumeResult() {
        _uiState.update { it.copy(addSuccess = false, errorMessage = null) }
    }
}