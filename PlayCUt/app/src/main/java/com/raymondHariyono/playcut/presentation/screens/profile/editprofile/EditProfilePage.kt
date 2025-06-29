package com.raymondHariyono.playcut.presentation.screens.profile.editprofile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.domain.model.UserProfile
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfilePage(
    navController: NavController,
    viewModel: EditProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // LaunchedEffect akan "mendengarkan" perubahan pada pesan sukses atau error
    LaunchedEffect(uiState.successMessage, uiState.error) {
        uiState.successMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.messageConsumed()
            }
        }
        uiState.error?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar("Error: $message")
                viewModel.messageConsumed()
            }
        }
    }
    LaunchedEffect(uiState.navigateToLogin) {
        if (uiState.navigateToLogin) {
            navController.navigate("login") {
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                launchSingleTop = true
            }
            viewModel.onNavigationComplete()
        }
    }

    Scaffold(
        // --- KODE BARU UNTUK MENAMPILKAN PESAN ---
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Edit Profil") },
                navigationIcon = {
                    // Tambahkan tombol kembali jika diperlukan
                    // IconButton(onClick = { navController.popBackStack() }) { ... }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier.padding(16.dp).fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    when (val profile = uiState.userProfile) {
                        is UserProfile.Barber -> BarberProfileFields(
                            profile = profile,
                            isUploading = uiState.isUploading,
                            onUpdate = { viewModel.updateProfile(it) },
                            onImageChange = { viewModel.updateBarberProfilePicture(it) }
                        )
                        is UserProfile.Customer -> CustomerProfileFields(
                            profile = profile,
                            onUpdate = { viewModel.updateProfile(it) }
                        )
                        is UserProfile.Admin -> AdminProfileFields(
                            profile = profile,
                            onUpdate = { viewModel.updateProfile(it) }
                        )
                        is UserProfile.Unknown -> Text("Gagal memuat profil atau peran tidak dikenal.")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    OutlinedButton(
                        onClick = { viewModel.logout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
                    ) {
                        Text("Logout")
                    }
                }
            }
        }
    }
}

// Tidak ada perubahan pada Composable di bawah ini
@Composable
private fun BarberProfileFields(
    profile: UserProfile.Barber,
    isUploading: Boolean,
    onUpdate: (UserProfile.Barber) -> Unit,
    onImageChange: (Uri) -> Unit
) {
    var contact by remember { mutableStateOf(profile.contact) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? -> uri?.let(onImageChange) }
    )

    AsyncImage(
        model = profile.imageRes.ifEmpty { R.drawable.ic_default_profile },
        contentDescription = "Foto Profil",
        placeholder = painterResource(id = R.drawable.ic_default_profile),
        error = painterResource(id = R.drawable.ic_default_profile),
        modifier = Modifier.size(120.dp).clip(CircleShape).clickable { imagePickerLauncher.launch("image/*") },
        contentScale = ContentScale.Crop
    )
    if (isUploading) {
        CircularProgressIndicator(modifier = Modifier.padding(top = 8.dp))
    }
    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = contact,
        onValueChange = { contact = it },
        label = { Text("Nomor Kontak") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = { onUpdate(profile.copy(contact = contact)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Simpan Perubahan")
    }
}

@Composable
private fun CustomerProfileFields(
    profile: UserProfile.Customer,
    onUpdate: (UserProfile.Customer) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var phoneNumber by remember { mutableStateOf(profile.phoneNumber) }

    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Icon(painterResource(id = R.drawable.ic_default_profile), contentDescription = "Profile", modifier = Modifier.fillMaxSize())
    }
    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nama Lengkap") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = phoneNumber,
        onValueChange = { phoneNumber = it },
        label = { Text("Nomor HP") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = { onUpdate(profile.copy(name = name, phoneNumber = phoneNumber)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Simpan Perubahan")
    }
}

@Composable
private fun AdminProfileFields(
    profile: UserProfile.Admin,
    onUpdate: (UserProfile.Admin) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }

    Box(modifier = Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        Icon(painterResource(id = R.drawable.ic_default_profile), contentDescription = "Profile", modifier = Modifier.fillMaxSize())
    }
    Spacer(Modifier.height(24.dp))

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Nama Admin") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(24.dp))
    Button(
        onClick = { onUpdate(profile.copy(name = name)) },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Simpan Perubahan")
    }
}