package com.raymondHariyono.playcut.domain.usecase.admin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RegisterBarberAccountUseCase @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend operator fun invoke(email: String): Result<String> {
        val defaultPassword = "playcut123"

        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, defaultPassword).await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal mendapatkan UID dari akun yang dibuat.")
            Result.success(uid)
        } catch (e: Exception) {
            if (e is FirebaseAuthUserCollisionException) {
                Result.failure(Exception("Email ini sudah terdaftar sebagai akun Firebase Auth."))
            } else {
                Result.failure(Exception("Gagal membuat akun Auth: ${e.message}"))
            }
        }
    }
}