package com.raymondHariyono.playcut.presentation.screens.inspiration

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.home.GetInspirationPhotosUseCase // Perhatikan package telah diubah dari 'inspiration' ke 'home'
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
        fetchInspirationPhotos() // Tidak perlu lagi parameter query karena use case tidak menggunakannya
    }

    // Ubah parameter agar tidak ada 'query' karena use case tidak menerimanya lagi
    fun fetchInspirationPhotos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                // Panggil metode execute() secara eksplisit
                val photos = getInspirationPhotosUseCase.execute()
                _uiState.update { it.copy(isLoading = false, photos = photos) }
            } catch (e: Exception) {
                // Tangani kesalahan di sini
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
}