package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
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
    val adminProfile = uiState.adminProfile as? UserProfile.Admin

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.snackbarMessageShown()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar("Error: $message") }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Cabang ${adminProfile?.branchName ?: "Memuat..."}") },
                actions = {
                    IconButton(onClick = viewModel::onLogoutClick) {
                        Icon(Icons.AutoMirrored.Outlined.ExitToApp, "Logout")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val branchId = adminProfile?.branchId
                    if (branchId != null && branchId != -1) {
                        navController.navigate("detailBranch/$branchId")
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = {
                    val branchId = adminProfile?.branchId
                    if (branchId != null && branchId != -1) {
                        navController.navigate("addBarber/$branchId")
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("ID Cabang tidak ditemukan.") }
                    }
                }) {
                    Icon(Icons.Outlined.Person, null, Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Tambah Barber")
                }

                Button(onClick = {
                    val branchId = adminProfile?.branchId
                    if (branchId != null && branchId != -1) {
                        navController.navigate("manageBarbers/$branchId")
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("ID Cabang tidak ditemukan.") }
                    }
                }) {
                    Icon(Icons.Default.Add, null, Modifier.size(ButtonDefaults.IconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Kelola Barber")
                }

            }
            Button(onClick = {
                val branchId = adminProfile?.branchId
                if (branchId != null && branchId != -1) {
                    navController.navigate("manageBarberAccounts/$branchId")
                } else {
                    scope.launch { snackbarHostState.showSnackbar("ID Cabang tidak ditemukan.") }
                }
            }) {
                Icon(Icons.Outlined.AccountCircle, null, Modifier.size(ButtonDefaults.IconSize)) // Contoh ikon
                Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                Text("Kelola Akun")
            }
            Spacer(Modifier.height(24.dp))
            Text("Daftar Reservasi di Cabang Anda:", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            } else if (uiState.reservations.isEmpty()) {
                Text("Belum ada reservasi untuk hari ini.")
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(uiState.reservations) { reservation ->
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
    }
}