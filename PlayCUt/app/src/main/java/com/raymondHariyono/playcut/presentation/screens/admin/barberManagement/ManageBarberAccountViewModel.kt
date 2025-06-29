package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.admin.LinkBarberAccountUseCase
import com.raymondHariyono.playcut.domain.usecase.admin.RegisterBarberAccountUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageBarberAccountsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val barbershopRepository: BarbershopRepository,
    private val registerBarberAccountUseCase: RegisterBarberAccountUseCase,
    private val linkBarberAccountUseCase: LinkBarberAccountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageBarberAccountsUiState())
    val uiState = _uiState.asStateFlow()

    private val defaultBarberPassword = "playcut123"

    init {
        loadAdminAndBarberData()
    }

    /**
     * Memuat profil admin dan daftar barber untuk cabang admin.
     */
    private fun loadAdminAndBarberData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val profile = authRepository.getCurrentUserProfile()
                if (profile is UserProfile.Admin) {
                    _uiState.update { it.copy(adminProfile = profile) }
                    barbershopRepository.getBranches()
                        .collect { branches ->
                            val currentBranch = branches.find { it.id == profile.branchId }
                            _uiState.update { currentState ->
                                currentState.copy(
                                    isLoading = false,
                                    barbers = currentBranch?.barbers ?: emptyList(),
                                    error = if (currentBranch == null) "Cabang admin tidak ditemukan." else null
                                )
                            }
                        }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Profil admin tidak ditemukan atau peran tidak valid.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal memuat data: ${e.localizedMessage}") }
            }
        }
    }

    /**
     * Memulai proses pembuatan akun Firebase Auth dan menautkannya ke profil barber.
     *
     * @param barber Profil barber yang akan dibuatkan akun.
     */
    fun onCreateBarberAccountClick(barber: Barber) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegisteringAccount = true, snackbarMessage = null) }

            val generatedEmail = generateBarberEmail(barber)


            val registerResult = registerBarberAccountUseCase(generatedEmail)

            if (registerResult.isSuccess) {
                val authUid = registerResult.getOrNull()!!


                val linkResult = linkBarberAccountUseCase(
                    branchId = _uiState.value.adminProfile?.branchId ?: -1,
                    barberProfileId = barber.id.toString(),
                    authUid = authUid
                )

                if (linkResult.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isRegisteringAccount = false,
                            snackbarMessage = "Akun untuk ${barber.name} berhasil dibuat dan ditautkan!",
                            showAccountCredentialsDialog = true,
                            barberSelectedForAccount = barber,
                            generatedAccountEmail = generatedEmail,
                            generatedAccountPassword = defaultBarberPassword
                        )
                    }
                    loadAdminAndBarberData()
                } else {
                    _uiState.update {
                        it.copy(
                            isRegisteringAccount = false,
                            snackbarMessage = "Gagal menautkan akun: ${linkResult.exceptionOrNull()?.message}",
                            error = linkResult.exceptionOrNull()?.message
                        )
                    }
                }
            } else {
                _uiState.update {
                    it.copy(
                        isRegisteringAccount = false,
                        snackbarMessage = "Gagal membuat akun Auth: ${registerResult.exceptionOrNull()?.message}",
                        error = registerResult.exceptionOrNull()?.message
                    )
                }
            }
        }
    }

    private fun generateBarberEmail(barber: Barber): String {
        val sanitizedName = barber.name.lowercase().replace(" ", "")
        val branchPrefix = _uiState.value.adminProfile?.branchId?.toString() ?: "unknown"
        return "${sanitizedName}_${barber.id}@barber.playcut.com"
    }

    fun snackbarMessageShown() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun onDismissAccountCredentialsDialog() {
        _uiState.update { it.copy(showAccountCredentialsDialog = false, barberSelectedForAccount = null, generatedAccountEmail = null, generatedAccountPassword = null) }
    }
}