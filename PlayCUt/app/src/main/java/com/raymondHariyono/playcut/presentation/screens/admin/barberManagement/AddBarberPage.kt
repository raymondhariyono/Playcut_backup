package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBarberPage(
    navController: NavController,
    viewModel: AddBarberViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val branchId: Int? = navController.currentBackStackEntry?.arguments?.getInt("branchId")


    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.messageShown()
        }
        uiState.errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.messageShown()
        }
    }

    // LaunchedEffect untuk navigasi setelah sukses
    LaunchedEffect(uiState.isBarberAdded) {
        if (uiState.isBarberAdded) {
            // Kembali ke AdminDashboardPage atau tujuan lain
            navController.popBackStack()
            viewModel.barberAddedNavigated() // Reset flag
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { TopAppBar(title = { Text("Tambah Barber Baru") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = uiState.barberNameInput,
                onValueChange = viewModel::onBarberNameChange,
                label = { Text("Nama Barber") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.barberContactInput,
                onValueChange = viewModel::onBarberContactChange,
                label = { Text("Kontak Barber (Opsional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (branchId != null && branchId != -1) {
                        viewModel.onAddBarberClick(branchId)
                    } else {
                        scope.launch { snackbarHostState.showSnackbar("ID Cabang tidak valid.") }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Tambah Barber")
                }
            }
        }
    }
}