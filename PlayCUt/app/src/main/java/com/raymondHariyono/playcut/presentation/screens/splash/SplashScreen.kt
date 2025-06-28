// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/splash/SplashScreen.kt
package com.raymondHariyono.playcut.presentation.screens.splash


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.raymondHariyono.playcut.R

const val SPLASH_ROUTE = "splash" // Definisikan nama rute untuk splash

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // LaunchedEffect akan berjalan satu kali saat uiState.navigateTo berubah dari null
    LaunchedEffect(key1 = uiState.navigateTo) {
        uiState.navigateTo?.let { destination ->
            // Lakukan navigasi ke tujuan yang sudah ditentukan oleh ViewModel
            navController.navigate(destination) {
                // Hapus splash screen dari back stack agar tidak bisa kembali ke sini
                popUpTo(SPLASH_ROUTE) {
                    inclusive = true
                }
            }
        }
    }

    // Tampilkan konten splash screen Anda
    SplashContent()
}

@Composable
fun SplashContent() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.barbershop_logo))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,

        ) {
        LottieAnimation(composition = composition, progress = { progress })
    }
}