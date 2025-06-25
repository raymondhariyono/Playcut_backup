
package com.raymondHariyono.playcut.presentation.screens.booking
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Barber
import kotlinx.coroutines.launch

@Composable
fun BookingPage(
    navController: NavController,
    barberId: Int,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Panggil loadBarber saat halaman dimuat pertama kali
    LaunchedEffect(Unit) {
        viewModel.loadBarber(barberId)
    }

    // Handle hasil booking
    LaunchedEffect(uiState.bookingResult) {
        uiState.bookingResult?.let { result ->
            val message = if (result.isSuccess) "Booking Berhasil!" else "Booking Gagal: ${result.exceptionOrNull()?.message}"
            scope.launch {
                snackbarHostState.showSnackbar(message)
            }
            viewModel.bookingResultConsumed()

            if (result.isSuccess) {
                navController.popBackStack()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }
                uiState.error != null -> {
                    Text("Error: ${uiState.error}")
                }
                uiState.barber != null -> {
                    BookingForm(
                        barber = uiState.barber!!,
                        uiState = uiState,
                        onServiceSelect = viewModel::onServiceSelected,
                        onTimeSelect = viewModel::onTimeSelected,
                        onConfirmClick = viewModel::onConfirmBooking
                    )
                }
            }
        }
    }
}

@Composable
fun BookingForm(
    barber: Barber,
    uiState: BookingUiState,
    onServiceSelect: (String) -> Unit,
    onTimeSelect: (String) -> Unit,
    onConfirmClick: () -> Unit
) {
    val mainServices = listOf("Haircut", "Haircut + Keramas", "Haircut + Keramas + Treatment")
    val isConfirmEnabled = uiState.selectedService.isNotBlank() && uiState.selectedTime.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        Text(text = "Pesan dengan", style = MaterialTheme.typography.titleLarge)
        Text(text = barber.name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(24.dp))

        SectionTitle("Pilih Layanan Utama")
        mainServices.forEach { service ->
            ServiceRadioButton(
                serviceName = service,
                isSelected = uiState.selectedService == service,
                onSelect = { onServiceSelect(service) }
            )
        }

        Spacer(Modifier.height(16.dp))
        SectionTitle("Pilih Waktu Tersedia")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 100.dp),
            modifier = Modifier.height(150.dp), // Tentukan tinggi agar tidak error di dalam Column
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(barber.availableTimes) { time ->
                SelectableItem(
                    text = time,
                    isSelected = uiState.selectedTime == time,
                    onClick = { onTimeSelect(time) }
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onConfirmClick,
            enabled = isConfirmEnabled, // Tombol aktif jika semua sudah dipilih
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Konfirmasi Booking", style = MaterialTheme.typography.titleMedium)
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

@Composable
fun ServiceRadioButton(serviceName: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = isSelected, onClick = onSelect)
        Spacer(Modifier.width(8.dp))
        Text(text = serviceName, style = MaterialTheme.typography.bodyLarge)
    }
}


@Composable
fun SelectableItem(text: String, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, textAlign = TextAlign.Center)
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(16.dp)
                )
            }
        }
    }
}