package com.raymondHariyono.playcut.presentation.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.inspiration.GetInspirationPhotosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InspirationViewModel @Inject constructor(
    private val getInspirationPhotosUseCase: GetInspirationPhotosUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InspirationUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchInspirationPhotos("men haircut style")
    }

    fun fetchInspirationPhotos(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = getInspirationPhotosUseCase(query)

            result.onSuccess { photos ->
                _uiState.update { it.copy(isLoading = false, photos = photos) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }
}