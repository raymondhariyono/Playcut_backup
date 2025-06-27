package com.raymondHariyono.playcut.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.LoginCredentials
import com.raymondHariyono.playcut.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val getUserProfileUseCase: GetUserProfileUseCase // Inject UseCase untuk profil
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, error = null) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Langkah 1: Jalankan UseCase untuk Login terlebih dahulu
            val loginResult = loginUseCase(
                LoginCredentials(
                    email = _uiState.value.email.trim(),
                    pass = _uiState.value.pass
                )
            )

            // Langkah 2: Periksa hasil login
            if (loginResult.isSuccess) {
                // Jika login berhasil, LANJUTKAN dengan mengambil profil
                val profileResult = getUserProfileUseCase()
                profileResult.onSuccess { profile ->
                    // Jika profil didapat, update state dengan sukses dan role
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userRole = profile?.role
                        )
                    }
                }.onFailure {e ->
                    // Jika GAGAL mengambil profil SETELAH login
                    _uiState.update { it.copy(isLoading = false, error = "Login berhasil, tapi gagal mengambil data profil: ${e.message}") }
                }
            } else {
                // Jika GAGAL login dari awal
                _uiState.update {
                    it.copy(isLoading = false, error = loginResult.exceptionOrNull()?.message)
                }
            }
        }
    }
}