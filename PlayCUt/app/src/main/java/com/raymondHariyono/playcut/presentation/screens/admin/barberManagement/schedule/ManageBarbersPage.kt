package com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.schedule

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Barber
import kotlinx.coroutines.launch

data class EditableTime(val time: String, val isMarkedForDeletion: Boolean = false)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageBarbersPage(
    navController: NavController,
    viewModel: ManageBarbersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showScheduleDialog by remember { mutableStateOf<Barber?>(null) }
    var showDeleteDialog by remember { mutableStateOf<Barber?>(null) }
    var showStatusDialog by remember { mutableStateOf<Barber?>(null) }

    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let {
            scope.launch { snackbarHostState.showSnackbar(it) }
            viewModel.onMessageShown()
        }
        uiState.error?.let {
            scope.launch { snackbarHostState.showSnackbar(it, withDismissAction = true) }
            viewModel.onMessageShown() // Reset pesan setelah ditampilkan
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Kelola Barber & Jadwal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading && uiState.branch == null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            uiState.branch == null -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text("Gagal memuat data barber. Error: ${uiState.error}")
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.padding(padding).fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.branch!!.barbers, key = { it.id }) { barber ->
                        AdminBarberItem(
                            barber = barber,
                            onEditScheduleClick = { showScheduleDialog = barber },
                            onSetStatusClick = { showStatusDialog = barber },
                            onDeleteClick = { showDeleteDialog = barber }
                        )
                    }
                }
            }
        }
    }

    // --- DIALOG-DIALOG ---

    // Dialog untuk Edit Jadwal
    if (showScheduleDialog != null) {
        ScheduleEditDialog(
            barber = showScheduleDialog!!,
            onDismiss = { showScheduleDialog = null },
            onConfirm = { updatedTimes ->
                viewModel.updateBarberSchedule(showScheduleDialog!!.id, updatedTimes)
                showScheduleDialog = null
            }
        )
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog != null) {
        ConfirmationDialog(
            title = "Hapus Barber",
            text = "Apakah Anda yakin ingin menghapus ${showDeleteDialog?.name}? Reservasi yang terhubung dengannya mungkin akan terpengaruh.",
            onConfirm = {
                showDeleteDialog?.let { viewModel.deleteBarber(it.id) }
                showDeleteDialog = null
            },
            onDismiss = { showDeleteDialog = null }
        )
    }

    // Dialog Konfirmasi Ubah Status
    if (showStatusDialog != null) {
        val newStatusText = if (showStatusDialog?.status == "active") "meliburkan" else "mengaktifkan kembali"
        ConfirmationDialog(
            title = "Ubah Status Barber",
            text = "Apakah Anda yakin ingin $newStatusText ${showStatusDialog?.name}?",
            onConfirm = {
                showStatusDialog?.let { viewModel.setBarberStatus(it.id, it.status) }
                showStatusDialog = null
            },
            onDismiss = { showStatusDialog = null }
        )
    }
}


@Composable
fun AdminBarberItem(
    barber: Barber,
    onEditScheduleClick: () -> Unit,
    onSetStatusClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val isOnLeave = barber.status == "on_leave"
    val cardColor = if (isOnLeave) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
    val statusText = if (isOnLeave) "Sedang Libur" else "Aktif"
    val statusButtonText = if (isOnLeave) "Aktifkan" else "Liburkan"

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = barber.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    color = if(isOnLeave) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.background(
                        color = if(isOnLeave) MaterialTheme.colorScheme.error.copy(alpha = 0.5f) else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(4.dp)
                    ).padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "Jadwal Tersedia:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                barber.availableTimes.sorted().forEach { time ->
                    SuggestionChip(onClick = {}, label = { Text(time) })
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = onEditScheduleClick, modifier = Modifier.weight(1f)) {
                    Text("Ubah Jadwal")
                }
                OutlinedButton(onClick = onSetStatusClick, modifier = Modifier.weight(1f)) {
                    Text(statusButtonText)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun ScheduleEditDialog(
    barber: Barber,
    onDismiss: () -> Unit,
    onConfirm: (List<String>) -> Unit
) {
    var editableTimes by remember {
        mutableStateOf(barber.availableTimes.map { EditableTime(it) })
    }
    var newTimeInput by remember { mutableStateOf("") }
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Ubah Jadwal untuk ${barber.name}", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    editableTimes.forEachIndexed { index, item ->
                        InputChip(
                            selected = false,
                            onClick = {
                                val updatedList = editableTimes.toMutableList()
                                updatedList[index] = item.copy(isMarkedForDeletion = !item.isMarkedForDeletion)
                                editableTimes = updatedList
                            },
                            label = {
                                Text(
                                    text = item.time,
                                    textDecoration = if (item.isMarkedForDeletion) TextDecoration.LineThrough else null
                                )
                            },
                            trailingIcon = { Icon(Icons.Default.Close, "Hapus") }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

                // --- FIX: Dibungkus dalam Row ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newTimeInput,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                newTimeInput = it
                            }
                        },
                        label = { Text("Waktu (HHMM)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = TimeVisualTransformation()
                    )
                    Button(onClick = {
                        if (newTimeInput.length == 4) {
                            val formattedTime = "${newTimeInput.substring(0, 2)}:${newTimeInput.substring(2, 4)}"
                            if (editableTimes.any { it.time == formattedTime && !it.isMarkedForDeletion }) {
                                Toast.makeText(context, "Jadwal sudah ada", Toast.LENGTH_SHORT).show()
                            } else {
                                editableTimes = editableTimes + EditableTime(formattedTime)
                                newTimeInput = ""
                            }
                        } else {
                            Toast.makeText(context, "Format waktu tidak valid", Toast.LENGTH_SHORT).show()
                        }
                    }) {
                        Text("Tambah")
                    }
                }
                // --- Akhir dari FIX ---

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Batal") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val finalTimes = editableTimes
                            .filter { !it.isMarkedForDeletion }
                            .map { it.time }
                            .distinct()
                            .sorted()
                        onConfirm(finalTimes)
                    }) {
                        Text("Simpan")
                    }
                }
            }
        }
    }
}

// Composable baru untuk dialog konfirmasi
@Composable
fun ConfirmationDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Ya, Lanjutkan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}

// Kelas untuk transformasi visual input waktu HH:MM
class TimeVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val trimmed = text.text.take(4)
        var out = ""
        for (i in trimmed.indices) {
            out += trimmed[i]
            if (i == 1) out += ":"
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset <= 1) return offset
                if (offset <= 4) return offset + 1
                return 5
            }
            override fun transformedToOriginal(offset: Int): Int {
                if (offset <= 2) return offset
                if (offset <= 5) return offset - 1
                return 4
            }
        }
        return TransformedText(AnnotatedString(out), offsetMapping)
    }
}