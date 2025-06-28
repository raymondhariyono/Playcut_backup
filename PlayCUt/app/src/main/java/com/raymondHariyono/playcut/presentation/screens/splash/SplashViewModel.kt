package com.raymondHariyono.playcut.presentation.screens.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase
import com.raymondHariyono.playcut.presentation.screens.splash.SplashUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState = _uiState.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            // Beri jeda agar animasi Lottie sempat terlihat
            delay(2000)

            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, navigateTo = "onBoarding") }
            } else {
                // KASUS 2: Ada pengguna yang login, kita perlu cek perannya
                val profileResult = getUserProfileUseCase()
                profileResult.onSuccess { profile ->

                    val destination = if (profile?.role == "admin") {
                        "adminDashboard"
                    } else {
                        "home"
                    }
                    _uiState.update { it.copy(isLoading = false, navigateTo = destination) }
                }.onFailure {
                    // Jika gagal ambil profil (misal: koneksi error), lempar ke halaman login
                    _uiState.update { it.copy(isLoading = false, navigateTo = "login") }
                }
            }
        }
    }
}