package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Barber
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBarberAccountsPage(
    navController: NavController,
    viewModel: ManageBarberAccountsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.snackbarMessageShown()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Kelola Akun Barber") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
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
                    Text(text = "Error: ${uiState.error}", modifier = Modifier.align(Alignment.Center).padding(16.dp))
                }
                uiState.barbers.isEmpty() -> {
                    Text("Tidak ada barber untuk dikelola.", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Barber di Cabang ${uiState.adminProfile?.branchName ?: "Anda"}:",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                        items(uiState.barbers, key = { it.id }) { barber ->
                            BarberAccountItem(
                                barber = barber,
                                onRegisterAccountClick = {
                                    viewModel.onCreateBarberAccountClick(it)
                                },
                                isRegistering = uiState.isRegisteringAccount
                            )
                        }
                    }
                }
            }

            if (uiState.showAccountCredentialsDialog) {
                AccountCredentialsDialog(
                    barberName = uiState.barberSelectedForAccount?.name ?: "Barber",
                    email = uiState.generatedAccountEmail ?: "",
                    password = uiState.generatedAccountPassword ?: "playcut123",
                    onDismiss = viewModel::onDismissAccountCredentialsDialog
                )
            }
        }
    }
}

@Composable
fun BarberAccountItem(
    barber: Barber,
    onRegisterAccountClick: (Barber) -> Unit,
    isRegistering: Boolean
) {
    Card(elevation = CardDefaults.cardElevation(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = barber.name, style = MaterialTheme.typography.titleMedium)
                Text(text = barber.contact, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.width(16.dp))

            if (barber.authUid == null) {

                Button(
                    onClick = { onRegisterAccountClick(barber) },
                    enabled = !isRegistering
                ) {
                    if (isRegistering) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Buat Akun")
                    }
                }
            } else {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = "Akun Sudah Ada")
                    Spacer(Modifier.width(4.dp))
                    Text("Akun Terdaftar", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun AccountCredentialsDialog(
    barberName: String,
    email: String,
    password: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Akun Barber Dibuat!") },
        text = {
            Column {
                Text("Akun untuk $barberName berhasil dibuat.", style = MaterialTheme.typography.bodyLarge)
                Spacer(Modifier.height(8.dp))
                Text("Informasikan kredensial berikut kepada barber:", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
                Text("Email: $email", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text("Kata Sandi: $password", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Oke")
            }
        }
    )
}