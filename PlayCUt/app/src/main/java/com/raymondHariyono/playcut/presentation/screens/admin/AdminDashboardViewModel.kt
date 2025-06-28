package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    val reservationToDeleteId = MutableStateFlow<String?>(null)

    init {
        loadAdminProfileAndReservations()
    }

    private fun loadAdminProfileAndReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Pertama, dapatkan profil admin
            val profile = authRepository.getCurrentUserProfile()
            if (profile is UserProfile.Admin) {
                _uiState.update { it.copy(adminProfile = profile) }
                // Jika berhasil, gunakan branchId dari profil untuk memuat reservasi
                loadReservationsForBranch(profile.branchName)
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Gagal memuat profil admin.") }
            }
        }
    }


    fun onCancelReservationClick(id: String) {
        reservationToDeleteId.value = id
    }

    fun dismissDeleteConfirmation() {
        reservationToDeleteId.value = null
    }

    fun onConfirmDeletion() {
        viewModelScope.launch {
            val id = reservationToDeleteId.value ?: return@launch
            try {
                db.collection("reservations").document(id).delete().await()

                dismissDeleteConfirmation()
                _uiState.update { it.copy(snackbarMessage = "Reservasi berhasil dibatalkan.") }

                (_uiState.value.adminProfile as? UserProfile.Admin)?.branchName?.let {
                    loadReservationsForBranch(it)
                }
            } catch (e: Exception) {
                dismissDeleteConfirmation()
                _uiState.update { it.copy(errorMessage = "Gagal membatalkan reservasi.") }
            }
        }
    }

    private fun loadReservationsForBranch(branchName: String) {
        viewModelScope.launch {
            if (branchName.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, reservations = emptyList()) }
                return@launch
            }

            try {
                val reservationsSnapshot = db.collection("reservations")
                    .whereEqualTo("branchName", branchName)
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val reservations = reservationsSnapshot.toObjects(Reservation::class.java)
                _uiState.update { it.copy(isLoading = false, reservations = reservations) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error mengambil reservasi: ${e.localizedMessage}"
                    )
                }
            }
        }
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}