package com.raymondHariyono.playcut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import com.raymondHariyono.playcut.data.seeder.FirestoreSeeder
import com.raymondHariyono.playcut.presentation.navigation.AppNavigator
import com.raymondHariyono.playcut.ui.theme.PlayCUtTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        runAllSeeders()
        enableEdgeToEdge()

        setContent {
            PlayCUtTheme {
                AppNavigator()
            }
        }
    }


    private fun runAllSeeders() {
        // Gunakan lifecycleScope untuk menjalankan coroutine yang aman
        lifecycleScope.launch {
            // Siapkan semua koneksi yang dibutuhkan
            val firestore = FirebaseFirestore.getInstance()
            val firebaseAuth = FirebaseAuth.getInstance()

            // Buat instance dari kedua seeder
            val dataSeeder = FirestoreSeeder(firestore)
            val adminSeeder = AdminAccountSeeder(firebaseAuth, firestore)

            // Jalankan kedua seeder secara berurutan
            dataSeeder.seedDataIfNeeded()
            adminSeeder.seedAdminAccounts()
        }
    }
}



