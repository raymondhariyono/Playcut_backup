package com.raymondHariyono.playcut.domain.usecase.admin

import android.util.Log // Import Log
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.Barber
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AddBarberUseCase @Inject constructor(
    // private val auth: FirebaseAuth, // Tidak lagi digunakan untuk membuat Auth di sini
    private val db: FirebaseFirestore
) {
    private val TAG = "AddBarberUseCase"

    suspend operator fun invoke(
        branchId: Int,
        name: String,
        contact: String
    ): Result<Unit> {
        if (branchId == -1) {
            Log.e(TAG, "ID Cabang tidak valid: $branchId")
            return Result.failure(Exception("ID Cabang tidak valid."))
        }
        return try {
            val newBarberId = (System.currentTimeMillis() / 1000).toInt()
            val newBarber = Barber(
                id = newBarberId,
                name = name,
                contact = contact,
                imageRes = "",
                status = "active",
                authUid = null
            )

            val branchDocRef = db.collection("branches").document("branch-$branchId")
            val barbersSubcollectionRef = branchDocRef.collection("barbers")

            val barberDocPath = barbersSubcollectionRef.document(newBarber.id.toString()).path
            Log.d(TAG, "Mencoba menyimpan barber: $newBarber ke path Firestore: $barberDocPath")

            barbersSubcollectionRef.document(newBarber.id.toString()).set(newBarber).await()

            Log.d(TAG, "Barber berhasil disimpan ke Firestore: $newBarberId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan barber ke Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }
}