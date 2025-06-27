package com.raymondHariyono.playcut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import com.raymondHariyono.playcut.data.seeder.FirestoreSeeder
import com.raymondHariyono.playcut.presentation.navigation.AppNavigator
import com.raymondHariyono.playcut.ui.theme.PlayCUtTheme
import dagger.hilt.android.AndroidEntryPoint
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
        lifecycleScope.launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                val firebaseAuth = FirebaseAuth.getInstance()

                val dataSeeder = FirestoreSeeder(firestore)
                val adminSeeder = AdminAccountSeeder(firebaseAuth, firestore)

                dataSeeder.seedDataIfNeeded()
                adminSeeder.seedAdminAccounts()
            } catch (e: Exception) {
                e.printStackTrace() // Penting untuk logcat
            }
        }
    }
}



