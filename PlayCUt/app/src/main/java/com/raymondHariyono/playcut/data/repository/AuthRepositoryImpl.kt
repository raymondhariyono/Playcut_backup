package com.raymondHariyono.playcut.data.repository

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.usecase.auth.LoginCredentials
import com.raymondHariyono.playcut.domain.usecase.auth.RegisterCredentials
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
                is FirebaseAuthInvalidCredentialsException -> "Password yang Anda masukkan salah."
                is FirebaseAuthInvalidUserException -> "Email tidak terdaftar."
                else -> "Login gagal: ${e.localizedMessage}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun registerUser(credentials: RegisterCredentials): Result<Unit> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(credentials.email, credentials.pass).await()
            val firebaseUser = authResult.user ?: throw Exception("Gagal membuat user di Firebase Auth.")

            Log.d(TAG, "Auth user berhasil dibuat dengan UID: ${firebaseUser.uid}")

            val userProfile = UserProfile(
                name = credentials.name,
                email = credentials.email,
                role = "customer",
                branchId = 0,
                branchName = "",
                phoneNumber = "", // Disesuaikan dengan UserProfile, diinisialisasi kosong
                photoUrl = ""
            )

            firestore.collection("users").document(firebaseUser.uid).set(userProfile).await()
            Log.d(TAG, "Profil user berhasil disimpan ke Firestore.")

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("FirebaseRegisterError", "Gagal mendaftarkan pengguna: ${e.message}", e)
            val errorMessage = when (e) {
                is FirebaseAuthUserCollisionException -> "Email yang Anda masukkan sudah terdaftar."
                else -> "Registrasi gagal: ${e.localizedMessage}"
            }
            Result.failure(Exception(errorMessage))
        }
    }

    override suspend fun getCurrentUserProfile(): UserProfile? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return try {
            val documentSnapshot = firestore.collection("users").document(firebaseUser.uid).get().await()
            documentSnapshot.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal mengambil profil dari Firestore: ${e.message}", e)
            null
        }
    }

    override suspend fun logoutUser(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}