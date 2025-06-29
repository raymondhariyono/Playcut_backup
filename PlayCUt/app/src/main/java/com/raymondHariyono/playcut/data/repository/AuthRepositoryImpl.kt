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
            val uid = firebaseAuth.currentUser?.uid
            Log.d("LoginDebug", "Login berhasil. UID: $uid")
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

            // --- PERBAIKAN DI SINI ---
            // Buat objek User yang lengkap dengan nama
            val newUser = com.raymondHariyono.playcut.domain.model.User(
                name = credentials.name, // <-- TAMBAHKAN BARIS INI
                email = credentials.email,
                phoneNumber = "" // Inisialisasi kosong, bisa diisi nanti di edit profil
            )

            // Simpan objek newUser yang sudah lengkap ke Firestore
            firestore.collection("users").document(firebaseUser.uid).set(newUser).await()
            Log.d("AuthRepository", "Profil user baru berhasil disimpan ke Firestore.")

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("AuthRepository", "Gagal mendaftarkan pengguna: ${e.message}", e)
            val errorMessage = when (e) {
                is FirebaseAuthUserCollisionException -> "Email yang Anda masukkan sudah terdaftar."
                else -> "Registrasi gagal: ${e.localizedMessage}"
            }
            Result.failure(Exception(errorMessage))
        }

    }

    override suspend fun getCurrentUserProfile(): UserProfile? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        val uid = firebaseUser.uid

        return try {
            // Prioritas 1: Cek Admin
            val adminDoc = firestore.collection("admins").document(uid).get().await()
            if (adminDoc.exists()) {
                return UserProfile.Admin(
                    docPath = adminDoc.reference.path,
                    name = adminDoc.getString("name") ?: "",
                    branchId = adminDoc.getLong("branchId")?.toInt() ?: -1,
                    branchName = adminDoc.getString("branchName") ?: ""
                )
            }

            val barberDoc = firestore.collectionGroup("barbers").whereEqualTo("authUid", uid).limit(1).get().await().firstOrNull()
            if (barberDoc != null) {
                return UserProfile.Barber(
                    docPath = barberDoc.reference.path,
                    name = barberDoc.getString("name") ?: "",
                    contact = barberDoc.getString("contact") ?: "",
                    imageRes = barberDoc.getString("imageRes") ?: "",
                    id = barberDoc.getLong("id")?.toInt() ?: 0,
                    authUid = barberDoc.getString("authUid")
                )
            }

            // Prioritas 3: Cek Customer secara eksplisit
            val userDoc = firestore.collection("users").document(uid).get().await()
            if (userDoc.exists()) {
                return UserProfile.Customer(
                    docPath = userDoc.reference.path,
                    name = userDoc.getString("name") ?: "",
                    phoneNumber = userDoc.getString("phoneNumber") ?: ""
                )
            }
            Log.w("AuthRepository", "Pengguna dengan UID $uid berhasil diautentikasi, tetapi tidak memiliki dokumen profil di Firestore (admins, barbers, atau users).")
            null

        } catch (e: Exception) {
            Log.e("AuthRepository", "Gagal mengambil profil dari Firestore: ${e.message}", e)
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