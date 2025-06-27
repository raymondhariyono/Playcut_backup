// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/admin/AddBarberViewModel.kt
package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.usecase.admin.AddBarberUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBarberViewModel @Inject constructor(
    private val addBarberUseCase: AddBarberUseCase
) : ViewModel() {

    fun onAddBarber(branchId: Int, barberName: String, barberContact: String) {
        viewModelScope.launch {
            val newBarber = Barber(
                id = (System.currentTimeMillis() % 1000).toInt(),
                name = barberName,
                contact = barberContact,
                imageRes = "placeholder_barber",
                availableTimes = listOf("09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00") // Default
            )
            val result = addBarberUseCase(branchId, newBarber)
            // TODO: Update UI State dengan hasil (sukses/gagal) untuk ditampilkan di UI
        }
    }
}