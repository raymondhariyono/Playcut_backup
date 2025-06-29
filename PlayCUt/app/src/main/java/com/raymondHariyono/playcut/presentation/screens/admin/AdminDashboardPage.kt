package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.presentation.components.ReservationItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardPage(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservationToDeleteId by viewModel.reservationToDeleteId.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    // Ambil profil admin dari state dengan aman
    val adminProfile = uiState.adminProfile as? UserProfile.Admin

    // Efek untuk menangani pesan snackbar
    LaunchedEffect(uiState.snackbarMessage, uiState.errorMessage) {
        uiState.snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.snackbarMessageShown()
            }
        }
        uiState.errorMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.snackbarMessageShown()
            }
        }
    }

    // Efek untuk menangani navigasi setelah logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cabang ${adminProfile?.branchName ?: "Memuat..."}") },
                actions = {
                    IconButton(onClick = viewModel::onLogoutClick) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val branchId = adminProfile?.branchId
                    if (branchId != null && branchId != -1) {
                        navController.navigate("branch")
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("Profil admin tidak valid.") }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.AddCircle, "Walk-in Reservation", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(16.dp))

            // Tombol-tombol aksi admin
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AdminActionButton("Tambah Barber") {
                    adminProfile?.branchId?.takeIf { it != -1 }?.let {
                        navController.navigate("addBarber/$it")
                    }
                }
                AdminActionButton("Kelola Barber") {
                    adminProfile?.branchId?.takeIf { it != -1 }?.let {
                        navController.navigate("manageBarbers/$it")
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            AdminActionButton("Kelola Akun Barber") {
                adminProfile?.branchId?.takeIf { it != -1 }?.let {
                    navController.navigate("manageBarberAccounts/$it")
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("Daftar Reservasi di Cabang Anda:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            // Tampilan daftar reservasi
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.reservations.isEmpty()) {
                Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text("Belum ada reservasi untuk hari ini.")
                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(uiState.reservations, key = { it.id }) { reservation ->
                        ReservationItem(
                            reservation = reservation,
                            onCancelClick = viewModel::onCancelReservationClick,
                            onEditClick = { reservationId ->
                                navController.navigate("booking?reservationId=$reservationId")
                            }
                        )
                    }
                }
            }
        }
    }

    if (reservationToDeleteId != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeleteConfirmation,
            icon = { Icon(Icons.Outlined.Warning, "Warning") },
            title = { Text("Konfirmasi Pembatalan") },
            text = { Text("Apakah Anda yakin ingin membatalkan reservasi ini?") },
            confirmButton = {
                Button(
                    onClick = viewModel::onConfirmDeletion,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Ya, Batalkan") }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeleteConfirmation) { Text("Tidak") }
            }
        )
    }
}

@Composable
private fun RowScope.AdminActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}

@Composable
private fun AdminActionButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
    ) {
        Text(text)
    }
}