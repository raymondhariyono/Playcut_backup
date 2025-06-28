package com.raymondHariyono.playcut.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import com.raymondHariyono.playcut.domain.model.UserProfile // Import sealed class
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val adminSeeder: AdminAccountSeeder
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        initializeApp()
        checkUserStatus()
    }

    private fun initializeApp() {
        viewModelScope.launch {
            // Langkah 1: Jalankan seeder dan TUNGGU hingga selesai
            adminSeeder.seedAdminAccounts()

            // Langkah 2: Lanjutkan dengan memeriksa status pengguna
            checkUserStatus()
        }
    }


    private fun checkUserStatus() {
        viewModelScope.launch {
            // Beri jeda agar animasi Lottie atau splash screen sempat terlihat
            delay(2000)

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                // KASUS 1: Tidak ada pengguna yang login, arahkan ke onboarding/login
                _uiState.update { it.copy(isLoading = false, navigateTo = "onBoarding") }
            } else {
                // KASUS 2: Ada pengguna yang login, kita perlu cek perannya
                val profile = getUserProfileUseCase() // Panggil use case

                // --- PERBAIKAN DI SINI ---
                // Gunakan 'when' untuk menentukan tujuan navigasi berdasarkan tipe profil
                val destination = when (profile) {
                    is UserProfile.Admin -> "adminDashboard"
                    is UserProfile.Barber -> "barberDashboard"
                    is UserProfile.Customer -> "home"
                    // Jika profil null atau tidak dikenal (error), arahkan ke login untuk keamanan
                    else -> "login"
                }

                _uiState.update { it.copy(isLoading = false, navigateTo = destination) }
            }
        }
    }
}