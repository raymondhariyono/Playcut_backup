// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/admin/AdminDashboardPage.kt
package com.raymondHariyono.playcut.presentation.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.presentation.components.ReservationItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardPage(
    navController: NavController,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard - Cabang ${uiState.adminProfile?.branchName ?: "..."}") },
                actions = {
                    // TODO: Tambahkan tombol logout untuk admin
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigasi ke halaman tambah reservasi walk-in */ }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Reservasi Pelanggan")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // Tombol untuk ke halaman "Tambah Barber"
            Button(onClick = {
                // Ambil branchId dari profil admin dan kirimkan saat navigasi
                val branchId = uiState.adminProfile?.branchId ?: -1
                if (branchId != -1) {
                    navController.navigate("addBarber/$branchId")
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
            } else if (uiState.reservationsForBranch.isEmpty()) {
                Text("Belum ada reservasi untuk hari ini.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.reservationsForBranch) { reservation ->
                        ReservationItem(
                            reservation = reservation,
                            onCancelClick = { /* panggil vm.delete... */ },
                            onEditClick = { /* navigasi ke halaman edit */ }
                        )
                    }
                }
            }
        }
    }
}