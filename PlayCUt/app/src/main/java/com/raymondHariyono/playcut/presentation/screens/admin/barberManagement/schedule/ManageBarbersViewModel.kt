package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.schedule

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.google.firebase.auth.ktx.auth
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ManageBarbersViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val db = Firebase.firestore
    private val _uiState = MutableStateFlow(ManageBarbersUiState())
    val uiState = _uiState.asStateFlow()

    private val branchId: Int = savedStateHandle.get<Int>("branchId") ?: -1
    private val branchDocumentId = "branch-$branchId"

    init {
        if (branchId != -1) {
            loadBranchDetails()
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID Cabang tidak valid.") }
        }
    }

    fun onMessageShown() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }

    private fun loadBranchDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                db.collection("branches").document(branchDocumentId)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            _uiState.update { it.copy(isLoading = false, error = error.localizedMessage) }
                            return@addSnapshotListener
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val branch = snapshot.toObject(Branch::class.java)
                            _uiState.update { it.copy(isLoading = false, branch = branch, error = null) }
                        } else {
                            _uiState.update { it.copy(isLoading = false, error = "Cabang tidak ditemukan.") }
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun updateBarberSchedule(barberId: Int, newTimes: List<String>) {
        viewModelScope.launch {
            val currentBranch = _uiState.value.branch ?: run {
                _uiState.update { it.copy(error = "Data cabang tidak ditemukan untuk update.") }
                return@launch
            }

            val updatedBarbers = currentBranch.barbers.map { barber ->
                if (barber.id == barberId) {
                    barber.copy(availableTimes = newTimes)
                } else {
                    barber
                }
            }

            try {
                db.collection("branches").document(branchDocumentId)
                    .update("barbers", updatedBarbers)
                    .await()
                // Kirim pesan sukses ke UI
                _uiState.update { it.copy(successMessage = "Jadwal berhasil diperbarui!") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal memperbarui jadwal: ${e.localizedMessage}") }
            }


            try {
                db.collection("branches").document(branchDocumentId)
                    .update("barbers", updatedBarbers)
                    .await()
                _uiState.update { it.copy(isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Gagal memperbarui jadwal: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteBarber(barberId: Int) {
        viewModelScope.launch {
            val currentBranch = _uiState.value.branch ?: return@launch
            val updatedBarbers = currentBranch.barbers.filterNot { it.id == barberId }

            try {
                db.collection("branches").document(branchDocumentId)
                    .update("barbers", updatedBarbers)
                    .await()
                _uiState.update { it.copy(successMessage = "Barber berhasil dihapus.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal menghapus barber: ${e.localizedMessage}") }
            }
        }
    }

    fun setBarberStatus(barberId: Int, currentStatus: String) {
        viewModelScope.launch {
            val currentBranch = _uiState.value.branch ?: return@launch
            val newStatus = if (currentStatus == "active") "on_leave" else "active"
            val successText = if (newStatus == "on_leave") "diliburkan" else "diaktifkan"

            val updatedBarbers = currentBranch.barbers.map {
                if (it.id == barberId) it.copy(status = newStatus) else it
            }

            try {
                db.collection("branches").document(branchDocumentId)
                    .update("barbers", updatedBarbers)
                    .await()
                _uiState.update { it.copy(successMessage = "Barber berhasil $successText.") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal mengubah status: ${e.localizedMessage}") }
            }
        }
    }

    private val auth = Firebase.auth
    fun generateBarberAccount(barber: Barber) {
        viewModelScope.launch {
            val currentBranch = _uiState.value.branch ?: return@launch
            val email = "${barber.name.lowercase().replace(" ", "")}.${barber.id}@playcut.barber"
            val defaultPassword = "playcut123"

            try {
                val authResult = auth.createUserWithEmailAndPassword(email, defaultPassword).await()
                val uid = authResult.user?.uid

                if (uid != null) {
                    val updatedBarbers = currentBranch.barbers.map {
                        if (it.id == barber.id) it.copy(authUid = uid) else it
                    }
                    db.collection("branches").document(branchDocumentId)
                        .update("barbers", updatedBarbers)
                        .await()
                    _uiState.update { it.copy(successMessage = "Akun untuk ${barber.name} berhasil dibuat.") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = "Gagal buat akun: ${e.message}") }
            }
        }
    }
}