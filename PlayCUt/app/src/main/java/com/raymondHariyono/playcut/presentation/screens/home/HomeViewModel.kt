package com.raymondHariyono.playcut.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.usecase.GetHomePageDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomePageDataUseCase: GetHomePageDataUseCase
) : ViewModel(){
    private val _uiState = MutableStateFlow(HomePageUiState())
    val uiState: StateFlow<HomePageUiState> = _uiState.asStateFlow()

    init {
        loadHomePageData()
    }
        private fun loadHomePageData() {
        getHomePageDataUseCase().onEach { homeData ->
            _uiState.update {
                it.copy(
                    isLoading = false,
                    userName = homeData.userName,
                    promotions = homeData.promotions,
                    homeServices = homeData.homeServices,
                    inspirations = homeData.inspirations
                )
            }
        }
        .launchIn(viewModelScope)
    }
}