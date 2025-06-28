package com.raymondHariyono.playcut.presentation.screens.admin.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Service
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReservationPage(
    navController: NavController,
    reservationId: String, // Diterima dari navigasi
    viewModel: EditReservationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.updateResult) {
        uiState.updateResult?.let { result ->
            val message = if (result.isSuccess) "Reservasi berhasil diperbarui!" else "Gagal memperbarui reservasi: ${result.exceptionOrNull()?.localizedMessage}"
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.updateResultConsumed() // Reset result
        }
    }

    LaunchedEffect(uiState.isUpdated) {
        if (uiState.isUpdated) {
            navController.popBackStack() // Kembali setelah berhasil
            viewModel.navigatedAfterUpdate() // Reset flag
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Edit Reservasi") }) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage != null -> Text(text = "Error: ${uiState.errorMessage}")
                uiState.reservation != null -> {
                    // Tampilkan formulir edit
                    EditReservationForm(
                        uiState = uiState,
                        onCustomerNameChange = viewModel::onCustomerNameChange,
                        onServiceSelected = viewModel::onServiceSelected,
                        onBarberSelected = viewModel::onBarberSelected,
                        onDateSelected = viewModel::onDateSelected,
                        onTimeSelected = viewModel::onTimeSelected,
                        onConfirmUpdateClick = viewModel::onConfirmUpdateClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReservationForm(
    uiState: EditReservationUiState,
    onCustomerNameChange: (String) -> Unit,
    onServiceSelected: (String) -> Unit,
    onBarberSelected: (Int) -> Unit,
    onDateSelected: (String) -> Unit, // Implementasi DatePicker di luar scope saat ini
    onTimeSelected: (String) -> Unit,
    onConfirmUpdateClick: () -> Unit
) {
    val originalReservation = uiState.reservation ?: return
    val selectedBarberName = uiState.availableBarbers.find { it.id == uiState.selectedBarberId }?.name ?: "Pilih Barber"
    val isConfirmEnabled = uiState.customerNameInput.isNotBlank() &&
            uiState.selectedService.isNotBlank() &&
            uiState.selectedBarberId != 0 &&
            uiState.selectedDate.isNotBlank() &&
            uiState.selectedTime.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))
        Text(text = "Edit Reservasi untuk ${originalReservation.customerName}", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(24.dp))

        // Input Nama Pelanggan
        Text(text = "Nama Pelanggan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        OutlinedTextField(
            value = uiState.customerNameInput,
            onValueChange = onCustomerNameChange,
            label = { Text("Nama lengkap pelanggan") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Pilih Layanan
        Text(text = "Layanan", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        ServiceSelectionDropdown(
            selectedService = uiState.selectedService,
            services = uiState.availableServices,
            onServiceSelected = onServiceSelected
        )
        Spacer(Modifier.height(16.dp))

        // Pilih Barber
        Text(text = "Barber", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        BarberSelectionDropdown(
            selectedBarberId = uiState.selectedBarberId,
            selectedBarberName = selectedBarberName,
            barbers = uiState.availableBarbers,
            onBarberSelected = onBarberSelected
        )
        Spacer(Modifier.height(16.dp))

        // Pilih Tanggal
        Text(text = "Tanggal Reservasi", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        // TODO: Implementasi DatePicker di sini. Untuk saat ini, bisa pakai TextField sederhana
        OutlinedTextField(
            value = uiState.selectedDate,
            onValueChange = onDateSelected,
            label = { Text("Tanggal (contoh: 28 Jun 2025)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))

        // Pilih Waktu Tersedia
        Text(text = "Waktu Tersedia", style = MaterialTheme.typography.titleMedium, modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
        TimeSelectionGridForEdit(
            availableTimes = uiState.availableTimes,
            selectedTime = uiState.selectedTime,
            onTimeSelected = onTimeSelected
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = onConfirmUpdateClick,
            enabled = isConfirmEnabled && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text("Simpan Perubahan")
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

// Reusable Composable untuk dropdown layanan
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceSelectionDropdown(
    selectedService: String,
    services: List<Service>,
    onServiceSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedService,
            onValueChange = {},
            label = { Text("Pilih layanan...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            services.forEach { service ->
                DropdownMenuItem(
                    text = { Text("${service.name} (${service.price})") },
                    onClick = {
                        onServiceSelected(service.name)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Reusable Composable untuk dropdown barber
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberSelectionDropdown(
    selectedBarberId: Int,
    selectedBarberName: String,
    barbers: List<Barber>,
    onBarberSelected: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = selectedBarberName,
            onValueChange = {},
            label = { Text("Pilih barber...") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            barbers.forEach { barber ->
                DropdownMenuItem(
                    text = { Text(barber.name) },
                    onClick = {
                        onBarberSelected(barber.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

// Time selection grid sederhana untuk edit
@Composable
fun TimeSelectionGridForEdit(
    availableTimes: List<String>,
    selectedTime: String,
    onTimeSelected: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 90.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.heightIn(min = 60.dp, max = 200.dp)
    ) {
        items(availableTimes) { time ->
            val isSelected = selectedTime == time
            Card(
                modifier = Modifier.clickable { onTimeSelected(time) },
                shape = RoundedCornerShape(8.dp),
                colors = if (isSelected) CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(text = time, color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}