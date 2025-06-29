package com.raymondHariyono.playcut.presentation.screens.profile.editprofile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase // Pastikan path LogoutUseCase benar
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private val storage = Firebase.storage

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val uid = auth.currentUser?.uid

            // Jika tidak ada pengguna yang login, hentikan proses.
            if (uid == null) {
                _uiState.update { it.copy(isLoading = false, error = "Sesi tidak valid. Silakan login kembali.") }
                return@launch
            }

            try {
                // Prioritas 1: Cek apakah pengguna adalah Admin.
                val adminDoc = db.collection("admins").document(uid).get().await()
                if (adminDoc.exists()) {
                    val profile = UserProfile.Admin(
                        docPath = adminDoc.reference.path,
                        name = adminDoc.getString("name") ?: "Admin",
                        branchId = adminDoc.getLong("branchId")?.toInt() ?: -1,
                        branchName = adminDoc.getString("branchName") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile, error = null) }
                    return@launch
                }

                // Prioritas 2: Cek apakah pengguna adalah Barber.
                val barberDoc = db.collectionGroup("barbers").whereEqualTo("authUid", uid).limit(1).get().await().firstOrNull()
                if (barberDoc != null) {
                    val profile = UserProfile.Barber(
                        docPath = barberDoc.reference.path,
                        name = barberDoc.getString("name") ?: "Barber",
                        contact = barberDoc.getString("contact") ?: "",
                        imageRes = barberDoc.getString("imageRes") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile, error = null) }
                    return@launch
                }

                // Prioritas 3: Asumsi pengguna adalah Customer.
                // Ganti "users" dengan nama koleksi pelanggan Anda jika berbeda.
                val userDoc = db.collection("users").document(uid).get().await()
                if (userDoc.exists()) {
                    val profile = UserProfile.Customer(
                        docPath = userDoc.reference.path,
                        name = userDoc.getString("name") ?: "Pengguna",
                        phoneNumber = userDoc.getString("phoneNumber") ?: ""
                    )
                    _uiState.update { it.copy(isLoading = false, userProfile = profile, error = null) }
                    return@launch
                }

                // Jika tidak ditemukan di mana pun.
                _uiState.update { it.copy(isLoading = false, error = "Profil pengguna tidak dapat ditemukan.") }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Terjadi kesalahan: ${e.localizedMessage}") }
            }
        }
    }

    fun updateProfile(profile: UserProfile) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                when (profile) {
                    is UserProfile.Barber -> {
                        db.document(profile.docPath).update("contact", profile.contact).await()
                    }
                    is UserProfile.Customer -> {
                        db.document(profile.docPath).update(mapOf(
                            "name" to profile.name, "phoneNumber" to profile.phoneNumber
                        )).await()
                    }
                    is UserProfile.Admin -> {
                        db.document(profile.docPath).update("name", profile.name).await()
                    }
                    else -> {}
                }
                _uiState.update { it.copy(isLoading = false, successMessage = "Profil berhasil diperbarui.") }
                loadUserProfile()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun updateBarberProfilePicture(imageUri: Uri) {
        viewModelScope.launch {
            val profile = _uiState.value.userProfile
            if (profile !is UserProfile.Barber) return@launch

            _uiState.update { it.copy(isUploading = true) }
            try {
                val uid = auth.currentUser?.uid ?: return@launch
                val storageRef = storage.reference.child("profile_pictures/$uid.jpg")
                val downloadUrl = storageRef.putFile(imageUri).await().storage.downloadUrl.await().toString()

                db.document(profile.docPath).update("imageRes", downloadUrl).await()

                _uiState.update { it.copy(isUploading = false, successMessage = "Foto berhasil diubah.") }
                loadUserProfile()
            } catch (e: Exception) {
                _uiState.update { it.copy(isUploading = false, error = "Gagal upload: ${e.message}") }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _uiState.update { it.copy(navigateToLogin = true) }
        }
    }

    fun onNavigationComplete() {
        _uiState.update { it.copy(navigateToLogin = false) }
    }
}