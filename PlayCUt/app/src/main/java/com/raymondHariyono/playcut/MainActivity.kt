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



