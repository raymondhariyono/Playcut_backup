package com.raymondHariyono.playcut.presentation.screens.auth.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.data.repository.AuthRepositoryImpl
import com.raymondHariyono.playcut.domain.usecase.LoginCredentials
import com.raymondHariyono.playcut.domain.usecase.LoginUseCase
import com.raymondHariyono.playcut.utils.ConnectionUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val loginUseCase = LoginUseCase(AuthRepositoryImpl())

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(pass: String) {
        _uiState.update { it.copy(pass = pass, error = null) }
    }

    fun onLoginClick() {
        if (!ConnectionUtils.isOnline(getApplication())) {
            _uiState.update { it.copy(error = "Tidak ada koneksi internet.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentState = _uiState.value
            val result = loginUseCase(
                LoginCredentials(
                    email = currentState.email,
                    pass = currentState.pass
                )
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, loginSuccess = true) }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.exceptionOrNull()?.message ?: "Terjadi error"
                    )
                }
            }
        }
    }
}