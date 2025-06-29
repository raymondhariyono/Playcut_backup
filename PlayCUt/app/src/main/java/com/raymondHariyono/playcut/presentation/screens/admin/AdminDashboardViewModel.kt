package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase,
    private val barbershopRepository: BarbershopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    private val _reservationToDeleteId = MutableStateFlow<String?>(null)
    val reservationToDeleteId: StateFlow<String?> = _reservationToDeleteId.asStateFlow()


    init {
        loadAdminProfileAndReservations()
    }

    private fun loadAdminProfileAndReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val profile = authRepository.getCurrentUserProfile()

            if (profile is UserProfile.Admin) {
                _uiState.update { it.copy(adminProfile = profile) }
                // Panggil fungsi untuk memuat data
                loadReservationsForBranch(profile.branchName)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat profil admin.") }
            }
        }
    }

    private fun loadReservationsForBranch(branchName: String) {
        if (branchName.isBlank()) {
            _uiState.update { it.copy(isLoading = false, reservations = emptyList(), errorMessage = "Nama cabang tidak valid.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Mulai loading
            barbershopRepository.getReservationsByBranch(branchName)
                .catch { e ->
                    // Tangani error jika terjadi
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.localizedMessage}") }
                }
                .collect { reservationsFromRepo ->
                    // Update UI dengan data yang didapat dan hentikan loading
                    _uiState.update { it.copy(isLoading = false, reservations = reservationsFromRepo) }
                }
        }
    }

    fun onCancelReservationClick(id: String) {
        _reservationToDeleteId.value = id
    }

    fun dismissDeleteConfirmation() {
        _reservationToDeleteId.value = null
    }

    fun onConfirmDeletion() {
        viewModelScope.launch {
            val id = _reservationToDeleteId.value ?: return@launch
            try {
                // Proses hapus bisa langsung ke firestore untuk sementara
                Firebase.firestore.collection("reservations").document(id).delete().await()

                dismissDeleteConfirmation()
                _uiState.update { it.copy(snackbarMessage = "Reservasi berhasil dibatalkan.") }

            } catch (e: Exception) {
                dismissDeleteConfirmation()
                _uiState.update { it.copy(errorMessage = "Gagal membatalkan reservasi: ${e.message}") }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null, errorMessage = null) }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}