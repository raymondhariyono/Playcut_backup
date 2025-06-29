package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarberDashboardViewmodel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(BarberDashboardUiState())
    val uiState = _uiState.asStateFlow()

    private var reservationsListener: ListenerRegistration? = null
    private val TAG = "BarberDashboardVM"

    init {
        loadBarberProfileAndReservations()
    }

    private fun loadBarberProfileAndReservations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val currentAuthUser = firebaseAuth.currentUser

            if (currentAuthUser == null) {
                _uiState.update { it.copy(isLoading = false, error = "Pengguna tidak terautentikasi.") }
                return@launch
            }

            try {
                val profile = authRepository.getCurrentUserProfile()

                if (profile is UserProfile.Barber) {
                    _uiState.update { it.copy(barberProfile = profile) }

                    Log.d(TAG, "Profil Barber dimuat: ${profile.name}, AuthUID: ${profile.authUid}")

                    if (profile.id != 0) {
                        reservationsListener?.remove()

                        reservationsListener = firestore.collection("reservations")
                            .whereEqualTo("barberAuthUid", profile.authUid)
                            .orderBy("bookingDate", Query.Direction.DESCENDING)
                            .orderBy("bookingTime", Query.Direction.DESCENDING)
                            .addSnapshotListener { snapshot, e ->
                                if (e != null) {
                                    Log.e(TAG, "Error listening for reservations: ${e.message}", e)
                                    _uiState.update { it.copy(isLoading = false, error = "Gagal memuat reservasi: ${e.localizedMessage}") }
                                    return@addSnapshotListener
                                }

                                if (snapshot != null) {
                                    val reservations = snapshot.toObjects(Reservation::class.java)
                                    _uiState.update { it.copy(isLoading = false, reservations = reservations, error = null) }
                                    Log.d(TAG, "Memuat ${reservations.size} reservasi untuk barber.")
                                } else {
                                    _uiState.update { it.copy(isLoading = false, reservations = emptyList(), error = null) }
                                    Log.d(TAG, "Snapshot reservasi kosong.")
                                }
                            }
                    } else {
                        _uiState.update { it.copy(isLoading = false, error = "Profil barber tidak memiliki ID valid.") }
                        Log.e(TAG, "Profil barber tidak memiliki ID valid: ${profile.id}")
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Profil pengguna bukan barber.") }
                    Log.e(TAG, "Profil pengguna bukan barber. Tipe: ${profile?.javaClass?.simpleName ?: "null"}")
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal memuat data: ${e.localizedMessage}") }
                Log.e(TAG, "Kesalahan umum saat memuat data barber: ${e.message}", e)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        reservationsListener?.remove()
    }
}