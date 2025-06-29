package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.addBarber

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
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

    // Efek untuk menampilkan pesan error dan meresetnya
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.consumeResult()
        }
    }

    // Efek untuk navigasi kembali setelah sukses
    LaunchedEffect(uiState.addSuccess) {
        if (uiState.addSuccess) {
            // Tampilkan pesan sukses sebentar sebelum kembali
            scope.launch {
                snackbarHostState.showSnackbar("Barber baru berhasil ditambahkan!")
            }
            viewModel.consumeResult()
            navController.popBackStack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tambah Barber Baru") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Detail Barber", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = uiState.nameInput,
                onValueChange = viewModel::onNameChange,
                label = { Text("Nama Lengkap Barber") },
                modifier = Modifier.fillMaxWidth(),
                isError = uiState.errorMessage != null
            )
            OutlinedTextField(
                value = uiState.contactInput,
                onValueChange = viewModel::onContactChange,
                label = { Text("Nomor Kontak (cth: 0812...)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                isError = uiState.errorMessage != null
            )


            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Akun Login Barber", style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(
                value = uiState.emailInput,
                onValueChange = viewModel::onEmailChange,
                label = { Text("Email untuk Login") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                supportingText = { Text("Password default akan dibuat: 'playcut123'") },
                isError = uiState.errorMessage != null
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::onAddBarberClick,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Tambah Barber")
                }
            }
        }
    }
}