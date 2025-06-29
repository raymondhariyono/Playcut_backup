package com.raymondHariyono.playcut.data.seeder

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.AdminProfile
import kotlinx.coroutines.tasks.await

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
        AdminSeedData("admin.lambung@playcut.com", "pass1234", "Admin Lambung Mangkurat", 1, "Lambung Mangkurat"),
        AdminSeedData("admin.ayani@playcut.com", "pass1234", "Admin A. Yani KM 5", 2, "A. Yani KM 5"),
        AdminSeedData("admin.sultan@playcut.com", "pass1234", "Admin Sultan Adam", 3, "Sultan Adam"),
        AdminSeedData("admin.gatot@playcut.com", "pass1234", "Admin Gatot Subroto", 4, "Gatot Subroto"),
        AdminSeedData("admin.banjarbaru@playcut.com", "pass1234", "Admin Banjarbaru", 5, "Banjarbaru")
    )

    suspend fun seedAdminAccounts() {
        Log.d(TAG, "Memulai proses seeding lengkap (Auth & Firestore)...")
        var createdCount = 0

        for (adminData in adminsToSeed) {
            try {
                // Langkah 1: Buat pengguna di Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(adminData.email, adminData.pass).await()
                val firebaseUser = authResult.user

                if (firebaseUser != null) {
                    Log.d(TAG, "Berhasil membuat akun Auth untuk: ${adminData.email} dengan UID: ${firebaseUser.uid}")

                    // Langkah 2: Siapkan objek profil admin
                    val adminProfile = AdminProfile(
                        name = adminData.name,
                        email = adminData.email,
                        branchId = adminData.branchId,
                        branchName = adminData.branchName
                    )

                    db.collection("admins").document(firebaseUser.uid).set(adminProfile).await()

                    Log.d(TAG, "Berhasil menyimpan profil Firestore untuk: ${adminData.name} dengan Document ID: ${firebaseUser.uid}")
                    createdCount++
                }
            } catch (e: FirebaseAuthUserCollisionException) {
                Log.w(TAG, "Akun Auth untuk ${adminData.email} sudah ada. Melewati.")
                continue
            } catch (e: Exception) {
                Log.e(TAG, "Gagal membuat admin ${adminData.email}", e)
            }
        }
        Log.d(TAG, "Proses seeding selesai. $createdCount akun admin baru dibuat.")
    }
}
