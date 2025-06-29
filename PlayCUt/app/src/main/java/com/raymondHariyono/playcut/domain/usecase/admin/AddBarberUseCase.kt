package com.raymondHariyono.playcut.domain.usecase.admin

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.Barber
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddBarberUseCase @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(
        branchId: Int,
        name: String,
        contact: String,
        emailForAuth: String
    ): Result<Unit> {
        if (branchId == -1) {
            return Result.failure(Exception("ID Cabang tidak valid."))
        }
        return try {
            val authResult = auth.createUserWithEmailAndPassword(emailForAuth, "playcut123").await()
            val uid = authResult.user?.uid ?: throw Exception("Gagal mendapatkan UID dari Auth.")

            val newBarber = Barber(
                id = (System.currentTimeMillis() / 1000).toInt(),
                name = name,
                contact = contact,
                imageRes = "",
                status = "active",
                authUid = uid
            )

            val branchDocRef = db.collection("branches").document("branch-$branchId")
            branchDocRef.update("barbers", FieldValue.arrayUnion(newBarber)).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}