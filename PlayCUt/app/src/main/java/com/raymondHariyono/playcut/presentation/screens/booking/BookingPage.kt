package com.raymondHariyono.playcut.presentation.screens.booking

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Reservation
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingPage(
    navController: NavController,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.bookingResult, uiState.updateResult) {
        val result = uiState.bookingResult ?: uiState.updateResult

        result?.let {
            val message = if (it.isSuccess) {
                if (uiState.isEditMode) "Perubahan berhasil disimpan!" else "Booking Berhasil!"
            } else {
                "Gagal: ${it.exceptionOrNull()?.message}"
            }

            scope.launch { snackbarHostState.showSnackbar(message) }

            viewModel.bookingResultConsumed()

            if (it.isSuccess) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(title = { Text(if (uiState.isEditMode) "Edit Reservasi" else "Buat Reservasi") })
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading && uiState.barberDetails == null -> CircularProgressIndicator()
                uiState.error != null -> Text(text = "Error: ${uiState.error}")
                uiState.barberDetails != null -> {
                    BookingForm(
                        uiState = uiState,
                        onCustomerNameChange = viewModel::onCustomerNameChange,
                        onMainServiceSelect = viewModel::onMainServiceSelected,
                        onOtherServicesSelect = viewModel::onOtherServicesSelected,
                        onTimeSelect = viewModel::onTimeSelected,
                        onConfirmClick = viewModel::onConfirmClick
                    )
                }
            }
        }
    }
}

@Composable
fun BookingForm(
    uiState: BookingUiState,
    onCustomerNameChange: (String) -> Unit,
    onMainServiceSelect: (String) -> Unit,
    onOtherServicesSelect: (List<String>) -> Unit,
    onTimeSelect: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    val barberDetails = uiState.barberDetails ?: return

    val isConfirmEnabled = uiState.customerName.isNotBlank() &&
            uiState.selectedTime.isNotBlank() &&
            uiState.selectedMainService.isNotBlank() &&
            !uiState.isLoading 

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text(text = "Pesan dengan", style = MaterialTheme.typography.titleLarge)
        Text(text = barberDetails.barber.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = "di ${barberDetails.branch.name}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.secondary)
        Spacer(Modifier.height(24.dp))

        SectionTitle("Nama Pelanggan")
        OutlinedTextField(
            value = uiState.customerName,
            onValueChange = onCustomerNameChange,
            label = { Text("Masukkan nama lengkap Anda") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(16.dp))

        SectionTitle("Pilih Layanan Utama")
        ServicesSelectionDropDown(
            selectedService = uiState.selectedMainService,
            onServiceSelected = onMainServiceSelect
        )
        Spacer(Modifier.height(16.dp))

        SectionTitle("Layanan Tambahan (Opsional)")
        OtherServicesGrid(
            selectedServices = uiState.selectedOtherServices,
            onServiceSelected = onOtherServicesSelect
        )
        Spacer(Modifier.height(16.dp))

        SectionTitle("Pilih Waktu Tersedia")
        TimeSelectionGrid(
            availableTimes = barberDetails.barber.availableTimes,
            selectedTime = uiState.selectedTime,
            reservations = uiState.existingReservations,
            barberName = barberDetails.barber.name,
            onTimeSelected = onTimeSelect,
            isEnabled = uiState.canEditTime
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onConfirmClick,
            enabled = isConfirmEnabled,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(if (uiState.isEditMode) "Simpan Perubahan" else "Konfirmasi Booking")
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesSelectionDropDown(selectedService: String, onServiceSelected: (String) -> Unit) {
    val dropdownServices = listOf("Haircut", "Haircut + Keramas", "Haircut + Keramas + Treatment")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedService,
            onValueChange = {},
            label = { Text("Pilih layanan utama...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            dropdownServices.forEach { service ->
                DropdownMenuItem(
                    text = { Text(service) },
                    onClick = {
                        onServiceSelected(service)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun OtherServicesGrid(
    selectedServices: List<String>,
    onServiceSelected: (List<String>) -> Unit
) {
    val otherServices = listOf("Coloring", "Shave", "Hair Spa", "Perming")

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        userScrollEnabled = false
    ) {
        items(otherServices) { service ->
            val isSelected = selectedServices.contains(service)
            FilterChip(
                selected = isSelected,
                onClick = {
                    val currentSelection = selectedServices.toMutableList()
                    if (isSelected) {
                        currentSelection.remove(service)
                    } else {
                        currentSelection.add(service)
                    }
                    onServiceSelected(currentSelection)
                },
                label = { Text(service) },
                leadingIcon = if (isSelected) { { Icon(Icons.Default.Check, contentDescription = "Selected") } } else null
            )
        }
    }
}

@Composable
fun TimeSelectionGrid(
    availableTimes: List<String>,
    selectedTime: String,
    reservations: List<Reservation>,
    barberName: String,
    onTimeSelected: (String) -> Unit,
    isEnabled: Boolean
) {
    val today = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(Date())
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(min = 60.dp, max = 200.dp)
    ) {
        items(availableTimes) { time ->
            val isTaken = reservations.any { it.barberName == barberName && it.bookingTime == time && it.bookingDate == today }
            val isSelected = selectedTime == time
            val finalIsEnabled = isEnabled && !isTaken

            val cardColors = when {
                !finalIsEnabled -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
                isSelected -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                else -> CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            }
            val textColor = when {
                !finalIsEnabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                isSelected -> MaterialTheme.colorScheme.onPrimaryContainer
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }

            Card(
                modifier = Modifier.clickable(enabled = finalIsEnabled) { onTimeSelected(time) },
                shape = RoundedCornerShape(8.dp),
                colors = cardColors
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = time, color = textColor, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}