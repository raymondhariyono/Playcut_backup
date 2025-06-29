package com.raymondHariyono.playcut.presentation.screens.reservation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.presentation.components.ButtonNavBar
import com.raymondHariyono.playcut.presentation.components.ReservationItem

@Composable
fun YourReservationPage(
    navController: NavController,
    viewModel: YourReservationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            // Judul halaman reservasi
            Text(
                text = stringResource(R.string.your_reservation_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        },
        bottomBar = { ButtonNavBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.reservations.isEmpty()) {
                EmptyState(navController)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(uiState.reservations, key = { it.id }) { reservation ->
                        ReservationItem(
                            reservation = reservation,
                            onCancelClick = { reservationId ->
                                viewModel.onCancelReservationClick(reservationId)
                            },
                            onEditClick = { reservationId ->
                                navController.navigate("booking?reservationId=$reservationId")
                            }
                        )
                    }
                }
            }
            if (uiState.reservationToDeleteId != null) {
                ConfirmationDialog(
                    onDismiss = { viewModel.onDismissDialog() },
                    onConfirm = { viewModel.onConfirmDeletion() }
                )
            }
        }
    }
}

@Composable
fun EmptyState(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Teks saat tidak ada reservasi
        Text(
            text = stringResource(R.string.no_reservations_title),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.no_reservations_message),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { navController.navigate("branch") }) {
            Text(stringResource(R.string.find_branch_now))
        }
    }
}

@Composable
fun ConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Outlined.Warning, contentDescription = stringResource(R.string.warning_icon_desc)) },

        // Judul dan isi dialog konfirmasi
        title = { Text(text = stringResource(R.string.cancel_confirmation_title)) },
        text = { Text(stringResource(R.string.cancel_confirmation_message)) },

        // Tombol konfirmasi
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(R.string.confirm_cancel))
            }
        },

        // Tombol batal
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.dismiss_cancel))
            }
        }
    )
}
