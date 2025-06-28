package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp // Import ini
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Warning // Import ini untuk dialog
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.presentation.components.ReservationItem // Pastikan ini diimport
import kotlinx.coroutines.delay // Import ini untuk delay
import androidx.compose.runtime.LaunchedEffect // Pastikan ini diimport
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope // Pastikan ini diimport
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardPage(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val reservationToDeleteId by viewModel.reservationToDeleteId.collectAsState() // Untuk dialog konfirmasi delete
    val snackbarHostState = remember { SnackbarHostState() } // Untuk menampilkan pesan Snackbar
    val scope = rememberCoroutineScope() // Untuk menjalankan coroutine di LaunchedEffect

    // Efek untuk navigasi logout
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            delay(100) // Penundaan kecil untuk transisi yang lebih mulus
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true } // Bersihkan back stack
            }
        }
    }

    // Efek untuk menampilkan pesan umum (misal: sukses delete)
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.snackbarMessageShown() // Memberi tahu ViewModel bahwa pesan sudah ditampilkan
        }
    }

    // Efek untuk menampilkan pesan error
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar("Error: $message") }
            // Pesan error biasanya akan hilang jika ada operasi baru atau state direset secara manual
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }, // Pasang SnackbarHost ke Scaffold
        topBar = {
            TopAppBar(
                title = { Text("Dashboard - Cabang ${uiState.adminProfile?.branchName ?: "..."}") },
                actions = {
                    // Tombol untuk navigasi ke halaman tambah barber
                    IconButton(onClick = {
                        val branchId = uiState.adminProfile?.branchId ?: -1
                        if (branchId != -1) {
                            navController.navigate("addBarber/$branchId")
                        } else {
                            scope.launch { snackbarHostState.showSnackbar("ID Cabang admin tidak ditemukan.") }
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Tambah Barber")
                    }
                    // Tombol Logout
                    IconButton(onClick = { viewModel.onLogoutClick() }) { // Memanggil fungsi logout di ViewModel
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            // Jika ada fitur tambahan walk-in reservation, FAB bisa diletakkan di sini
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Tombol untuk ke halaman "Kelola Barber"
            Button(onClick = {
                val branchId = uiState.adminProfile?.branchId ?: -1
                if (branchId != -1) {
                    navController.navigate("addBarber/$branchId")
                } else {
                    scope.launch { snackbarHostState.showSnackbar("ID Cabang admin tidak ditemukan.") }
                }
            }){
                Icon(Icons.Outlined.Person , contentDescription = null, modifier = Modifier.size(ButtonDefaults.IconSize))
                Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
                Text("Kelola Barber")
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Daftar Reservasi di Cabang Anda:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.reservations.isEmpty()) { // Menggunakan uiState.reservations
                Text("Belum ada reservasi untuk hari ini.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.reservations) { reservation -> // Menggunakan uiState.reservations
                        ReservationItem(
                            reservation = reservation,
                            onCancelClick = { reservationId ->
                                viewModel.onCancelReservationClick(reservationId) // Memicu dialog konfirmasi hapus
                            },
                            onEditClick = { reservationId ->
                                navController.navigate("editReservation/$reservationId") // Navigasi ke halaman edit
                            }
                        )
                    }
                }
            }

            // Dialog konfirmasi penghapusan (akan tampil jika reservationToDeleteId tidak null)
            if (reservationToDeleteId != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.dismissDeleteConfirmation() }, // Tutup dialog jika diklik di luar atau tombol dismiss
                    icon = { Icon(Icons.Outlined.Warning, contentDescription = "Warning") },
                    title = { Text(text = "Konfirmasi Pembatalan") },
                    text = { Text("Apakah Anda yakin ingin membatalkan reservasi ini? Aksi ini tidak dapat dikembalikan.") },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.onConfirmDeletion() }, // Memicu proses delete
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Ya, Batalkan")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.dismissDeleteConfirmation() }) {
                            Text("Tidak")
                        }
                    }
                )
            }
        }
    }
}