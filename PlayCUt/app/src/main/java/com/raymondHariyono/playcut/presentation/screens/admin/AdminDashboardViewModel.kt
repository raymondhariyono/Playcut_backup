package com.raymondHariyono.playcut.presentation.screens.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.admin.GetReservationsByBranchUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase // Pastikan ini diimport
import com.raymondHariyono.playcut.domain.usecase.reservation.DeleteReservationUseCase // Pastikan ini diimport
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase // Pastikan ini diimport
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getReservationsByBranchUseCase: GetReservationsByBranchUseCase,
    private val logoutUseCase: LogoutUseCase, // Inject LogoutUseCase
    private val deleteReservationUseCase: DeleteReservationUseCase // Inject DeleteReservationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    // State untuk dialog konfirmasi penghapusan
    private val _reservationToDeleteId = MutableStateFlow<String?>(null)
    val reservationToDeleteId: StateFlow<String?> = _reservationToDeleteId.asStateFlow()

    init {
        loadAdminData()
    }

    private fun loadAdminData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val profileResult = getUserProfileUseCase() // Dapatkan Result dari UseCase

            profileResult.onSuccess { userProfile ->
                if (userProfile?.role == "admin" && userProfile.branchName != null) {
                    _uiState.update { it.copy(adminProfile = userProfile, adminBranchName = userProfile.branchName) } // Perbarui adminBranchName

                    getReservationsByBranchUseCase(userProfile.branchName)
                        .catch { e ->
                            _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal memuat reservasi.") }
                            Log.e("AdminDashboardVM", "Error loading reservations: ${e.message}", e)
                        }
                        .collect { reservations ->
                            _uiState.update { it.copy(isLoading = false, reservations = reservations, errorMessage = null) } // Gunakan 'reservations'
                        }
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Akses ditolak: Bukan akun admin atau profil tidak ditemukan.") }
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal memuat data admin.") }
                Log.e("AdminDashboardVM", "Error loading admin data: ${e.message}", e)
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) } // Tampilkan loading saat logout
            logoutUseCase().onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoggedOut = true, errorMessage = null) } // Set isLoggedOut
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal logout.") }
                Log.e("AdminDashboardVM", "Logout failed: ${e.message}", e)
            }
        }
    }

    // Fungsi untuk memicu dialog konfirmasi penghapusan
    fun onCancelReservationClick(reservationId: String) {
        _reservationToDeleteId.value = reservationId
    }

    // Fungsi untuk menutup dialog konfirmasi
    fun dismissDeleteConfirmation() {
        _reservationToDeleteId.value = null
    }

    // Fungsi untuk mengeksekusi penghapusan setelah konfirmasi
    fun onConfirmDeletion() {
        val idToDelete = _reservationToDeleteId.value ?: return
        dismissDeleteConfirmation()

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, snackbarMessage = null) }
            val result = deleteReservationUseCase(idToDelete)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, snackbarMessage = "Reservasi berhasil dihapus.") }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Gagal menghapus reservasi.") }
                Log.e("AdminDashboardVM", "Error deleting reservation: ${e.message}", e)
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}