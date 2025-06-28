package com.raymondHariyono.playcut.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val uid = auth.currentUser?.uid
            if (uid == null) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi tidak valid.") }
                return@launch
            }

            try {
                val adminDoc = db.collection("admins").document(uid).get().await()
                if (adminDoc.exists()) {
                    val profile = UserProfile.Admin(
                        docPath = adminDoc.reference.path,
                        name = adminDoc.getString("name") ?: "Admin",
                        branchId = adminDoc.getLong("branchId")?.toInt() ?: -1,
                        branchName = adminDoc.getString("branchName") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                    return@launch
                }

                val barberDoc = db.collectionGroup("barbers").whereEqualTo("authUid", uid).limit(1).get().await().firstOrNull()
                if (barberDoc != null) {
                    val profile = UserProfile.Barber(
                        docPath = barberDoc.reference.path,
                        name = barberDoc.getString("name") ?: "",
                        contact = barberDoc.getString("contact") ?: "",
                        imageRes = barberDoc.getString("imageRes") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                    return@launch
                }

                val userDoc = db.collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val profile = UserProfile.Customer(
                        docPath = userDoc.reference.path,
                        name = userDoc.getString("name") ?: "",
                        phoneNumber = userDoc.getString("phoneNumber") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile) }
                    return@launch
                }

                _uiState.update { it.copy(isLoading = false, error = "Profil tidak ditemukan.") }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}