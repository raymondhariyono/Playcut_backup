// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/user/RegisterPage.kt
package com.raymondHariyono.playcut.presentation.screens.user

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.presentation.screens.auth.register.RegisterViewModel

@Composable
fun RegisterPage(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = uiState.registerSuccess) {
        if (uiState.registerSuccess) {
            Toast.makeText(context, "Registrasi Berhasil! Silakan Login.", Toast.LENGTH_LONG).show()
            navController.navigate("login") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.barbershop_logo))
        LottieAnimation(
            composition = composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(150.dp)
        )

        Text("Daftar Akun Baru", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        // 3. Tampilkan pesan error jika ada
        if (uiState.error != null) {
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // 4. Hubungkan semua TextField dengan ViewModel
        OutlinedTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = { Text("Nama Lengkap") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.username, onValueChange = viewModel::onUsernameChange, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.email, onValueChange = viewModel::onEmailChange, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = uiState.pass, onValueChange = viewModel::onPasswordChange, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                focusManager.clearFocus()
                viewModel.onRegisterClick()
            },
            // 5. Status tombol diatur oleh state
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
            } else {
                Text("Daftar", style = MaterialTheme.typography.labelLarge)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Sudah punya akun?")
            TextButton(onClick = { if (!uiState.isLoading) navController.navigate("login") }) {
                Text("Masuk di sini", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}