package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.schedule

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val TAG = "ManageBarbersVM"

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

    /**
     * Memuat detail cabang dan daftar barber dari sub-koleksi.
     */
    private fun loadBranchDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {

                db.collection("branches").document(branchDocumentId)
                    .addSnapshotListener { branchSnapshot, branchError ->
                        if (branchError != null) {
                            Log.e(TAG, "Error fetching branch details: ${branchError.localizedMessage}", branchError)
                            _uiState.update { it.copy(isLoading = false, error = branchError.localizedMessage) }
                            return@addSnapshotListener
                        }

                        if (branchSnapshot != null && branchSnapshot.exists()) {
                            val branch = branchSnapshot.toObject(Branch::class.java)
                            if (branch != null) {

                                db.collection("branches").document(branchDocumentId).collection("barbers")
                                    .addSnapshotListener { barbersSnapshot, barbersError ->
                                        if (barbersError != null) {
                                            Log.e(TAG, "Error fetching barbers subcollection: ${barbersError.localizedMessage}", barbersError)
                                            _uiState.update { it.copy(isLoading = false, error = barbersError.localizedMessage) }
                                            return@addSnapshotListener
                                        }

                                        if (barbersSnapshot != null) {
                                            val barbersList = barbersSnapshot.toObjects(Barber::class.java)

                                            val filteredBarbers = barbersList.filter { it.status == "active" || it.authUid != null }


                                            _uiState.update {
                                                it.copy(
                                                    isLoading = false,
                                                    branch = branch.copy(barbers = filteredBarbers),
                                                    error = null
                                                )
                                            }
                                            Log.d(TAG, "Branch details and barbers loaded successfully for branch: ${branch.name}")
                                        }
                                    }
                            } else {
                                Log.w(TAG, "Branch document exists but cannot be converted to Branch object.")
                                _uiState.update { it.copy(isLoading = false, error = "Cabang tidak ditemukan.") }
                            }
                        } else {
                            Log.w(TAG, "Branch document does not exist: $branchDocumentId")
                            _uiState.update { it.copy(isLoading = false, error = "Cabang tidak ditemukan.") }
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Unexpected error in loadBranchDetails: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }
        }
    }

    fun updateBarberSchedule(barberId: Int, newTimes: List<String>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, successMessage = null, error = null) }
            try {

                val barberDocRef = db.collection("branches").document(branchDocumentId)
                    .collection("barbers").document(barberId.toString())


                barberDocRef.update("availableTimes", newTimes).await()

                Log.d(TAG, "Jadwal barber ID $barberId berhasil diperbarui.")
                _uiState.update { it.copy(isLoading = false, successMessage = "Jadwal berhasil diperbarui!") }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal memperbarui jadwal untuk barber ID $barberId: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = "Gagal memperbarui jadwal: ${e.localizedMessage}") }
            }
        }
    }

    fun deleteBarber(barberId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, successMessage = null, error = null) }
            try {

                val barberDocRef = db.collection("branches").document(branchDocumentId)
                    .collection("barbers").document(barberId.toString())


                barberDocRef.delete().await()

                Log.d(TAG, "Barber ID $barberId berhasil dihapus.")
                _uiState.update { it.copy(isLoading = false, successMessage = "Barber berhasil dihapus.") }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal menghapus barber ID $barberId: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = "Gagal menghapus barber: ${e.localizedMessage}") }
            }
        }
    }

    fun setBarberStatus(barberId: Int, currentStatus: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, successMessage = null, error = null) }
            val newStatus = if (currentStatus == "active") "on_leave" else "active"
            val successText = if (newStatus == "on_leave") "diliburkan" else "diaktifkan"

            try {
                val barberDocRef = db.collection("branches").document(branchDocumentId)
                    .collection("barbers").document(barberId.toString())

                barberDocRef.update("status", newStatus).await()

                Log.d(TAG, "Status barber ID $barberId berhasil diubah menjadi $newStatus.")
                _uiState.update { it.copy(isLoading = false, successMessage = "Barber berhasil $successText.") }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengubah status barber ID $barberId: ${e.localizedMessage}", e)
                _uiState.update { it.copy(isLoading = false, error = "Gagal mengubah status: ${e.localizedMessage}") }
            }
        }
    }

}
