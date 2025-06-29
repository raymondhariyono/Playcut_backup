package com.raymondHariyono.playcut.presentation.screens.booking

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.usecase.branch.BarberDetails
import com.raymondHariyono.playcut.domain.usecase.reservation.GetReservationByIdUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.CreateBookingUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.GetReservationsUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBarberDetailsUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.UpdateReservationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getBarberDetailsUseCase: GetBarberDetailsUseCase,
    private val createBookingUseCase: CreateBookingUseCase,
    private val getReservationsUseCase: GetReservationsUseCase,
    private val getReservationByIdUseCase: GetReservationByIdUseCase,
    private val updateReservationUseCase: UpdateReservationUseCase

) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    init {
        val reservationId: String? = savedStateHandle["reservationId"]
        val barberId: Int = savedStateHandle.get<Int>("barberId") ?: -1

        if (reservationId != null) {
            loadReservationForEdit(reservationId)
        } else if (barberId != -1) {
            loadForCreateMode(barberId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "ID tidak valid.") }
        }
    }

    fun onCustomerNameChange(name: String) { _uiState.update { it.copy(customerName = name) } }
    fun onMainServiceSelected(service: String) { _uiState.update { it.copy(selectedMainService = service) } }
    fun onOtherServicesSelected(services: List<String>) { _uiState.update { it.copy(selectedOtherServices = services) } }
    fun onTimeSelected(time: String) { _uiState.update { it.copy(selectedTime = time) } }
    fun bookingResultConsumed() { _uiState.update { it.copy(bookingResult = null, updateResult = null) } }



    private fun loadReservationForEdit(id: String) {
        viewModelScope.launch {
            combine(
                getReservationByIdUseCase(id),
                getReservationsUseCase()
            ) { reservationToEdit, allReservations ->
                if (reservationToEdit != null) {

                    val services = reservationToEdit.service.split(",").map { it.trim() }
                    val mainService = services.firstOrNull() ?: ""
                    val otherServices = if (services.size > 1) services.drop(1) else emptyList()

                    val canEditTime = isTimeEditable(reservationToEdit.bookingDate, reservationToEdit.bookingTime)
                    val dummyBarberDetails = BarberDetails(
                        barber = Barber(name = reservationToEdit.barberName),
                        branch = Branch(name = reservationToEdit.branchName)
                    )

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isEditMode = true,
                            canEditTime = canEditTime,
                            reservationToEdit = reservationToEdit,
                            barberDetails = dummyBarberDetails,
                            existingReservations = allReservations.filter { it.id != id },
                            customerName = reservationToEdit.customerName,
                            selectedTime = reservationToEdit.bookingTime,
                            selectedMainService = mainService,
                            selectedOtherServices = otherServices
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, error = "Reservasi tidak ditemukan.") }
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }.launchIn(this)
        }
    }

    private fun loadForCreateMode(barberId: Int) {
        viewModelScope.launch {
            combine(
                getBarberDetailsUseCase(barberId),
                getReservationsUseCase()
            ) { barberDetailsResult, reservations ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isEditMode = false,
                        barberDetails = barberDetailsResult,
                        existingReservations = reservations,
                        error = if (barberDetailsResult == null) "Barber tidak ditemukan" else null
                    )
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.localizedMessage) }
            }.launchIn(this)
        }
    }



    fun onConfirmClick() {
        if (_uiState.value.isEditMode) {
            onConfirmChanges()
        } else {
            onConfirmBooking()
        }
    }

    private fun onConfirmChanges() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val reservationToUpdate = currentState.reservationToEdit?.copy(
                customerName = currentState.customerName,
                service = (listOf(currentState.selectedMainService) + currentState.selectedOtherServices).joinToString(", "),
                bookingTime = currentState.selectedTime
            )

            if (reservationToUpdate != null) {
                val result = updateReservationUseCase(reservationToUpdate)
                _uiState.update { it.copy(updateResult = result) }
            }
        }
    }

    private fun onConfirmBooking() {
        viewModelScope.launch {
            val currentState = _uiState.value
            val barber = currentState.barberDetails?.barber
            val branch = currentState.barberDetails?.branch

            // --- VALIDASI TAMBAHAN ---
            if (currentState.customerName.isBlank()) {
                _uiState.update { it.copy(bookingResult = Result.failure(Exception("Nama pemesan tidak boleh kosong."))) }
                return@launch
            }
            // --- AKHIR VALIDASI ---

            if (barber == null || branch == null || currentState.selectedTime.isBlank()) {
                _uiState.update { it.copy(bookingResult = Result.failure(Exception("Data barber, cabang, atau waktu tidak lengkap."))) }
                return@launch
            }

            val allSelectedServices = (listOf(currentState.selectedMainService) + currentState.selectedOtherServices).joinToString(", ")

            if (allSelectedServices.isBlank()) {
                _uiState.update { it.copy(bookingResult = Result.failure(Exception("Anda harus memilih setidaknya satu layanan."))) }
                return@launch
            }

            val result = createBookingUseCase(
                customerName = currentState.customerName,
                barberId = barber.id,
                barberName = barber.name,
                branchName = branch.name,
                service = allSelectedServices,
                bookingTime = currentState.selectedTime
            )

            _uiState.update { it.copy(bookingResult = result) }
        }
    }

    private fun isTimeEditable(dateStr: String, timeStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
            val bookingDateTime = sdf.parse("$dateStr $timeStr") ?: return false
            val oneHourInMillis = 3600000L
            (bookingDateTime.time - System.currentTimeMillis()) > oneHourInMillis
        } catch (e: Exception) { false }
    }
}