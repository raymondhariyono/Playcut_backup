package com.raymondHariyono.playcut.domain.usecase.admin

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LinkBarberAccountUseCase @Inject constructor(
    private val db: FirebaseFirestore
) {
    suspend operator fun invoke(
        branchId: Int,
        barberProfileId: String,
        authUid: String
    ): Result<Unit> {
        return try {
            val barberDocRef = db
                .collection("branches").document("branch-$branchId")
                .collection("barbers").document(barberProfileId)

            barberDocRef.update("authUid", authUid).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("Gagal menautkan akun Auth ke profil barber: ${e.message}"))
        }
    }
}