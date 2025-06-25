package com.raymondHariyono.playcut.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.GetUserProfileUseCase
import com.raymondHariyono.playcut.domain.usecase.LogoutUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val getUserProfileUseCase = GetUserProfileUseCase()
    private val logoutUseCase = LogoutUseCase()

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        getUserProfileUseCase().onEach { result ->
            if (result.isSuccess) {
                _uiState.update { it.copy(isLoading = false, userProfile = result.getOrNull()) }
            } else {
                _uiState.update { it.copy(isLoading = false, error = result.exceptionOrNull()?.message) }
            }
        }.launchIn(viewModelScope)
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase().onSuccess {
                _uiState.update { it.copy(isLoggedOut = true) }
            }
        }
    }
}