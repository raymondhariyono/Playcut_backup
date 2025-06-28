package com.raymondHariyono.playcut.presentation.screens.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.model.UserRole
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
    private val getUserProfileUseCase: GetUserProfileUseCase
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

            val loginResult = loginUseCase(
                LoginCredentials(
                    email = _uiState.value.email.trim(),
                    pass = _uiState.value.pass
                )
            )

            if (loginResult.isSuccess) {
                val profile = getUserProfileUseCase()

                val role = when (profile) {
                    is UserProfile.Admin -> UserRole.ADMIN
                    is UserProfile.Barber -> UserRole.BARBER
                    is UserProfile.Customer -> UserRole.CUSTOMER
                    else -> null
                }

                if (role != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            loginSuccess = true,
                            userRole = role
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Login berhasil, tapi data profil tidak ditemukan."
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, error = loginResult.exceptionOrNull()?.message)
                }
            }
        }
    }
}