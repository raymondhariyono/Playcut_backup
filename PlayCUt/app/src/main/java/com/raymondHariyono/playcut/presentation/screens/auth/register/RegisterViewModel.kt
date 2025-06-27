// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/user/RegisterViewModel.kt
package com.raymondHariyono.playcut.presentation.screens.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.data.repository.AuthRepositoryImpl
import com.raymondHariyono.playcut.domain.usecase.auth.RegisterCredentials
import com.raymondHariyono.playcut.domain.usecase.auth.RegisterUseCase
import com.raymondHariyono.playcut.utils.ConnectionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    application: Application
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update {
            it.copy(name = name, error = null)
        }
    }
    fun onUsernameChange(username: String) {
        _uiState.update {
            it.copy(username = username, error = null)
        }
    }
    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, error = null)
        }
    }
    fun onPasswordChange(pass: String) {
        _uiState.update {
            it.copy(pass = pass, error = null)
        }
    }

    fun onRegisterClick() {
        if (!ConnectionUtils.isOnline(getApplication())) {
            _uiState.update { it.copy(error = "Tidak ada koneksi internet untuk mendaftar.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = registerUseCase(
                RegisterCredentials(
                    name = _uiState.value.name.trim(),
                    username = _uiState.value.username.trim(),
                    email = _uiState.value.email.trim(),
                    pass = _uiState.value.pass
                )
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, registerSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Registrasi gagal."
                    )
                }
            }
        }
    }
}