package com.raymondHariyono.playcut.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.usecase.LoginCredentials
import com.raymondHariyono.playcut.domain.usecase.RegisterCredentials
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun loginUser(credentials: LoginCredentials): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(credentials.email, credentials.pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthInvalidCredentialsException -> "Password yang Anda masukkan salah."
                is com.google.firebase.auth.FirebaseAuthInvalidUserException -> "Email tidak terdaftar."
                else -> "Login gagal: ${e.localizedMessage}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun registerUser(credentials: RegisterCredentials): Result<Unit> {
        return try {
            firebaseAuth.createUserWithEmailAndPassword(credentials.email, credentials.pass).await()
            Result.success(Unit)
        } catch (e: Exception) {
            // TAMBAHKAN BARIS INI UNTUK MELIHAT ERROR DI LOGCAT
            Log.e("FirebaseRegisterError", "Gagal mendaftarkan pengguna: ${e.message}", e)

            // ... sisa kode
            val errorMessage = when (e) {
                is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email yang Anda masukkan sudah terdaftar."
                else -> "Registrasi gagal: ${e.localizedMessage}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun getCurrentUserProfile(): UserProfile? {
        // Dapatkan pengguna yang sedang login dari FirebaseAuth
        val firebaseUser = firebaseAuth.currentUser
        return if (firebaseUser != null) {
            try {
                // Ambil dokumen dari koleksi "users" berdasarkan UID pengguna saat ini
                val documentSnapshot =
                    firestore.collection("users").document(firebaseUser.uid).get().await()
                // Ubah dokumen Firestore menjadi objek UserProfile kita
                documentSnapshot.toObject(UserProfile::class.java)
            } catch (e: Exception) {
                Log.e("AuthRepoImpl", "Gagal mengambil profil dari Firestore: ${e.message}", e)
                null // Kembalikan null jika terjadi error
            }
        } else {
            null // Kembalikan null jika tidak ada pengguna yang login

        }
    }
}