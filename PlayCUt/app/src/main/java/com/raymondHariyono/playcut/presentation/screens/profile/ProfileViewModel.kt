package com.raymondHariyono.playcut.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    // 1. Terima UseCase melalui constructor, jangan buat manual
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        // Logika baru yang lebih aman dan jelas
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Panggil UseCase yang mengembalikan Result
            val profileResult = getUserProfileUseCase()

            // Buka "kotak" Result dengan onSuccess dan onFailure
            profileResult.onSuccess { profile ->
                // Jika sukses, 'profile' di sini adalah objek UserProfile?
                if (profile != null) {
                    _uiState.update { it.copy(isLoading = false, userProfile = profile, error = null) }
                } else {
                    // Kasus di mana login berhasil tapi dokumen profil tidak ada
                    _uiState.update { it.copy(isLoading = false, error = "Gagal memuat detail profil.") }
                }
            }.onFailure { e ->
                // Jika gagal (misal: error jaringan), tampilkan pesan error dari Exception
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }


    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase().onSuccess {
                _uiState.update { it.copy(isLoggedOut = true) }
            }
        }
    }
}
