package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class BarberDashboardViewmodel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

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
                    // Akses properti 'name' dan 'authUid' langsung dari objek 'profile'
                    Log.d(TAG, "Profil Barber dimuat: ${profile.name}, AuthUID: ${profile.authUid}") // INI SEHARUSNYA TIDAK ERROR LAGI

                    // Akses properti 'id' dan 'authUid' langsung dari objek 'profile'
                    profile.id.let { barberIdInteger -> // Menggunakan profile.id (integer)
                        if (barberIdInteger != 0) {
                            reservationsListener?.remove()

                            reservationsListener = firestore.collection("reservations")
                                .whereEqualTo("barberId", barberIdInteger) // Menggunakan barberId (integer)
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
                            Log.e(TAG, "Profil barber tidak memiliki ID valid: $barberIdInteger")
                        }
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
}