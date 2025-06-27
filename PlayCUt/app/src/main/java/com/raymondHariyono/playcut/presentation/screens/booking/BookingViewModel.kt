    package com.raymondHariyono.playcut.presentation.screens.booking
    
    import androidx.lifecycle.SavedStateHandle
    import androidx.lifecycle.ViewModel
    import androidx.lifecycle.viewModelScope
    import com.raymondHariyono.playcut.domain.model.Barber
    import com.raymondHariyono.playcut.domain.model.Branch
    import com.raymondHariyono.playcut.domain.usecase.GetReservationByIdUseCase
    import com.raymondHariyono.playcut.domain.usecase.branch.BarberDetails
    import com.raymondHariyono.playcut.domain.usecase.reservation.BookingData
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
                // Jika ada reservationId, kita masuk mode EDIT
                loadReservationForEdit(reservationId)
            } else if (barberId != -1) {
                // Jika hanya ada barberId, kita masuk mode CREATE
                loadForCreateMode(barberId)
            } else {
                // Jika tidak ada keduanya, tampilkan error
                _uiState.update { it.copy(isLoading = false, error = "ID tidak valid.") }
            }
        }
    
        // --- Event Handlers dari UI ---
        fun onCustomerNameChange(name: String) {
            _uiState.update {
                it.copy(customerName = name)
                }
        }

        fun onMainServiceSelected(service: String) {
            _uiState.update {
                it.copy(selectedMainService = service)
            }
        }

        fun onOtherServicesSelected(services: List<String>) {
            _uiState.update {
                it.copy(selectedOtherServices = services)
            }
        }
        fun onTimeSelected(time: String) {
            _uiState.update {
                it.copy(selectedTime = time)
            }
        }

        fun bookingResultConsumed() {
            _uiState.update {
                it.copy(bookingResult = null, updateResult = null)
            }
        }
    
        private fun loadInitialData(barberId: Int) {
            viewModelScope.launch {
                combine(
                    getBarberDetailsUseCase(barberId),
                    getReservationsUseCase()
                ) { barberDetailsResult, reservations ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
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
    
        private fun onConfirmBooking() {
            viewModelScope.launch {
                val currentState = _uiState.value
                val barber = currentState.barberDetails?.barber ?: return@launch
                val branch = currentState.barberDetails.branch
    
                val allSelectedServices = mutableListOf<String>()
                if (currentState.selectedMainService.isNotBlank()) allSelectedServices.add(currentState.selectedMainService)
                allSelectedServices.addAll(currentState.selectedOtherServices)
    
                val result = createBookingUseCase(
                    BookingData(
                        barberName = barber.name,
                        branchName = branch.name,
                        barberId = barber.id,
                        customerName = currentState.customerName,
                        services = allSelectedServices,
                        time = currentState.selectedTime
                    )
                )
                _uiState.update { it.copy(bookingResult = result) }
            }
        }
    
        private fun loadReservationForEdit(id: String) {
            viewModelScope.launch {
                combine(
                    getReservationByIdUseCase(id),
                    getReservationsUseCase()
                ) { reservationToEdit, allReservations ->
                    if (reservationToEdit != null) {
                        // --- LOGIKA PERBAIKAN DI SINI ---
                        // 1. Pisahkan string layanan menjadi list
                        val services = reservationToEdit.service.split(",").map { it.trim() }
                        // 2. Asumsikan item pertama adalah layanan utama, sisanya layanan tambahan
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
                            isEditMode = false  ,
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
    
    
        private fun isTimeEditable(dateStr: String, timeStr: String): Boolean {
            return try {
                val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
                val bookingDateTime = sdf.parse("$dateStr $timeStr") ?: return false
                val oneHourInMillis = 3600000L
                (bookingDateTime.time - System.currentTimeMillis()) > oneHourInMillis
            } catch (e: Exception) { false }
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
    }