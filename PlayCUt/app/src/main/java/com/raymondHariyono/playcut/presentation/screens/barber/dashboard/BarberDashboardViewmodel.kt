package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Reservation
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class BarberDashboardViewModel @Inject constructor() : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(BarberDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val uid = auth.currentUser?.uid
            if (uid == null) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi berakhir, silakan login kembali.") }
                return@launch
            }

            try {
                val barberDoc = db.collectionGroup("barbers").whereEqualTo("authUid", uid).limit(1).get().await().firstOrNull()
                val barber = barberDoc?.toObject(Barber::class.java)

                _uiState.update { it.copy(barberProfile = barber) }

                if (barber != null) {
                    val reservationsSnapshot = db.collection("reservations")
                        .whereEqualTo("barberId", barber.id)
                        .orderBy("dateTime", Query.Direction.DESCENDING) // Urutkan dari yang paling baru
                        .get().await()

                    val reservationList = reservationsSnapshot.toObjects(Reservation::class.java)
                    _uiState.update { it.copy(isLoading = false, reservations = reservationList, error = null) }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Profil barber tidak ditemukan atau belum tertaut.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal memuat data: ${e.message}") }
            }
        }
    }
}