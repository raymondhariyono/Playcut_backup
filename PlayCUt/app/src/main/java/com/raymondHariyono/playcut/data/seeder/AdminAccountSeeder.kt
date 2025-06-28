// File: app/src/main/java/com/raymondHariyono/playcut/data/seeder/AdminAccountSeeder.kt
package com.raymondHariyono.playcut.data.seeder

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.UserProfile
import kotlinx.coroutines.tasks.await

// Data class sederhana untuk menampung data admin yang akan kita buat
private data class AdminSeedData(
    val email: String,
    val pass: String,
    val name: String,
    val branchId: Int,
    val branchName: String
)

class AdminAccountSeeder(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    private val TAG = "AdminSeeder"

    private val adminsToSeed = listOf(
        AdminSeedData(
            "admin.lambung@playcut.com",
            "pass1234",
            "Admin Lambung Mangkurat",
            1,
            "Lambung Mangkurat"
        ),
        AdminSeedData(
            "admin.ayani@playcut.com",
            "pass1234",
            "Admin A. Yani KM 5",
            2,
            "A. Yani KM 5"
        ),
        AdminSeedData(
            "admin.sultan@playcut.com",
            "pass1234",
            "Admin Sultan Adam",
            3,
            "Sultan Adam"
        ),
        AdminSeedData(
            "admin.gatot@playcut.com",
            "pass1234",
            "Admin Gatot Subroto",
            4,
            "Gatot Subroto"
        ),
        AdminSeedData(
            "admin.banjarbaru@playcut.com",
            "pass1234",
            "Admin Banjarbaru",
            5,
            "Banjarbaru"
        )
    )

    // Fungsi utama untuk menjalankan proses seeding
    suspend fun seedAdminAccounts() {
        Log.d(TAG, "Memulai proses seeding akun admin...")
        var createdCount = 0

        for (adminData in adminsToSeed) {
            try {
                // Langkah 1: Buat pengguna di Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(adminData.email, adminData.pass).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    Log.d(TAG, "Berhasil membuat akun Auth untuk: ${adminData.email}")

                    // Langkah 2: Buat profil pengguna di Firestore
                    val userProfile = UserProfile(
                        name = adminData.name,
                        email = adminData.email,
                        role = "admin",
                        branchId = adminData.branchId,
                        branchName = adminData.branchName
                    )

                    // Gunakan UID dari Auth sebagai ID dokumen di Firestore
                    db.collection("users").document(firebaseUser.uid).set(userProfile).await()
                    Log.d(TAG, "Berhasil menyimpan profil Firestore untuk: ${adminData.name}")
                    createdCount++
                }
            } catch (e: com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                // Ini bukan error. Ini berarti emailnya sudah ada. Kita lewati saja.
                Log.w(TAG, "Akun untuk ${adminData.email} sudah ada. Melewati.")
                continue // Lanjut ke admin berikutnya
            } catch (e: Exception) {
                // Tangani error lain yang mungkin terjadi
                Log.e(TAG, "Gagal membuat admin ${adminData.email}", e)
            }
        }
        Log.d(TAG, "Proses seeding selesai. $createdCount akun admin baru dibuat.")
    }
}