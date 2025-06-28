package com.raymondHariyono.playcut.presentation.screens.barber.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Reservation
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarberDashboardPage(
    navController: NavController,
    viewModel: BarberDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Halo, ${uiState.barberProfile?.name ?: "..."}") },
                actions = {
                    IconButton(onClick = { navController.navigate("editProfile") }) {
                        Icon(Icons.Default.Edit , contentDescription = "Edit Profil")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(text = uiState.error!!)
                    }
                }
                uiState.reservations.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Belum ada reservasi untuk Anda.")
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("Jadwal Reservasi Anda:", style = MaterialTheme.typography.titleMedium)
                        }
                        items(uiState.reservations) { reservation ->
                            ReservationItemCard(reservation = reservation)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ReservationItemCard(reservation: Reservation) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            val formattedDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))
                .format(reservation.dateTime.toDate())
            val formattedTime = SimpleDateFormat("HH:mm", Locale("id", "ID"))
                .format(reservation.dateTime.toDate())
            Text(
                text = reservation.customerName,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Layanan: ${reservation.service}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "$formattedDate - Pukul $formattedTime",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}