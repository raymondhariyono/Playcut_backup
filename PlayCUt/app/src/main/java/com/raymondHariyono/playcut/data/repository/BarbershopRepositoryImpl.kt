package com.raymondHariyono.playcut.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.raymondHariyono.playcut.data.local.ReservationDao
import com.raymondHariyono.playcut.data.local.ReservationEntity
import com.raymondHariyono.playcut.domain.model.*
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.branch.BarberDetails
import kotlinx.coroutines.flow.Flow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class BarbershopRepositoryImpl(
    private val reservationDao: ReservationDao,
    firebaseAuth: FirebaseAuth
) : BarbershopRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val TAG = "FirestoreRepo"


    override fun getBranches(): Flow<List<Branch>> = flow {
        try {
            val branchesSnapshot = firestore.collection("branches").get().await()
            val branchesWithBarbers = branchesSnapshot.documents.mapNotNull { branchDoc ->
                val branch = branchDoc.toObject(Branch::class.java)
                if (branch != null) {
                    val barbersSnapshot = branchDoc.reference.collection("barbers").get().await()
                    val barbersList = barbersSnapshot.toObjects(Barber::class.java)

                    val filteredBarbers = barbersList.filter { it.status == "active" || it.authUid != null }

                    branch.copy(barbers = filteredBarbers)
                } else {
                    null
                }
            }
            emit(branchesWithBarbers)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting branches with barbers", e)
            emit(emptyList()) // Emit daftar kosong jika terjadi kesalahan
        }
    }

    override fun getBranchById(branchId: Int): Flow<Branch?> = flow {
        try {
            val documentId = "branch-$branchId"
            val snapshot = firestore.collection("branches").document(documentId).get().await()
            emit(snapshot.toObject(Branch::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Error getting branch by ID", e)
            emit(null)
        }
    }

    override fun getBarberById(barberId: Int): Flow<BarberDetails?> {
        return flow {
            try {
                // Mengambil semua dokumen cabang
                val branchesSnapshot = firestore.collection("branches").get().await()

                var foundBarber: Barber? = null
                var foundBranch: Branch? = null

                // Iterasi melalui setiap dokumen cabang
                for (branchDoc in branchesSnapshot.documents) {
                    val branch = branchDoc.toObject(Branch::class.java)
                    if (branch != null) {
                        // Kueri sub-koleksi 'barbers' di cabang ini untuk mencari barber berdasarkan ID
                        val barberQuerySnapshot = branchDoc.reference.collection("barbers")
                            .whereEqualTo("id", barberId)
                            .limit(1)
                            .get()
                            .await()

                        val barberDoc = barberQuerySnapshot.documents.firstOrNull()
                        if (barberDoc != null) {
                            foundBarber = barberDoc.toObject(Barber::class.java)
                            foundBranch = branch
                            break
                        }
                    }
                }

                if (foundBarber != null && foundBranch != null) {
                    emit(BarberDetails(barber = foundBarber, branch = foundBranch))
                } else {
                    Log.d(TAG, "Barber ID $barberId tidak ditemukan di cabang mana pun.")
                    emit(null)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gagal menemukan barber by ID", e)
                emit(null)
            }
        }
    }

    override fun getServices(): Flow<List<Service>> = flow {
        try {
            val snapshot = firestore.collection("services").get().await()
            val services = snapshot.toObjects(Service::class.java)
            emit(services)
            Log.d(TAG, "Successfully fetched ${services.size} services.")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting services", e)
            throw e //biarkan exception dilempar ke ViewModel
        }
    }


    override fun getHomeServices(): Flow<List<HomeService>> = flow {
        try {
            val snapshot = firestore.collection("services").limit(6).get().await()
            val homeServices = snapshot.toObjects(Service::class.java).map {
                HomeService(label = it.name, iconName = "ic_${it.name.lowercase()}")
            }
            emit(homeServices)
            Log.d(TAG, "Successfully fetched ${homeServices.size} home services.")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting home services", e)
            emit(emptyList())
        }
    }

    override fun getInspirations(): Flow<List<Inspiration>> = flow {
        try {
            //masih hardcode belum memakai API
            val inspirationsData = listOf(
                Inspiration("style1", "haircut_references"),
                Inspiration("style2", "haircut_references")
            )
            emit(inspirationsData)
            Log.d(TAG, "Successfully provided ${inspirationsData.size} inspirations.")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting inspirations", e)
            emit(emptyList())
        }
    }


    override fun getReservations(): Flow<List<Reservation>> {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return reservationDao.getAllReservations().map { entityList ->
            entityList
                .filter { it.userId == currentUserId }
                .map { it.toDomainModel() }
        }
    }

    override fun getReservationById(reservationId: String): Flow<Reservation?> {
        return reservationDao.getReservationById(reservationId).map { entity ->
            entity?.toDomainModel()
        }
    }

    override suspend fun saveReservation(reservation: Reservation) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: throw Exception("User belum login.")
        val reservationWithUser = reservation.copy(userId = currentUserId)
        try {
            firestore.collection("reservations").document(reservation.id).set(reservationWithUser).await()

            Log.d(TAG, "Reservasi berhasil disimpan ke Firestore dengan ID: ${reservation.id}")
            reservationDao.insertReservation(reservationWithUser.toEntity())
            Log.d(TAG, "Reservasi juga berhasil disimpan ke database lokal Room.")

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menyimpan reservasi: ${e.message}", e)
            throw e
        }
    }


    override suspend fun updateReservation(reservation: Reservation) {
        try {
            firestore.collection("reservations").document(reservation.id)
                .set(reservation, SetOptions.merge()).await()
            Log.d(TAG, "Reservasi berhasil di-update di Firestore dengan ID: ${reservation.id}")

            reservationDao.updateReservation(reservation.toEntity())
            Log.d(TAG, "Reservasi juga berhasil di-update di database lokal Room.")

        } catch (e: Exception) {
            Log.e(TAG, "Gagal meng-update reservasi: ${e.message}", e)
            throw e
        }
    }

    override suspend fun deleteReservation(reservationId: String) {
        try {
            firestore.collection("reservations").document(reservationId).delete().await()
            Log.d(TAG, "Reservasi berhasil dihapus dari Firestore dengan ID: $reservationId")

            reservationDao.deleteReservationById(reservationId)
            Log.d(TAG, "Reservasi juga berhasil dihapus dari database lokal Room.")

        } catch (e: Exception) {
            Log.e(TAG, "Gagal menghapus reservasi: ${e.message}", e)
            throw e
        }
    }


    override fun getReservationsByBranch(branchName: String): Flow<List<Reservation>> {
        // Mengembalikan Flow yang langsung "mendengarkan" perubahan dari Firestore
        return flow {
            if (branchName.isBlank()) {
                emit(emptyList()) // Jika nama cabang kosong, langsung kirim list kosong
                return@flow
            }

            try {
                val snapshot = firestore.collection("reservations")
                    .whereEqualTo("branchName", branchName)
                    .orderBy("dateTime", Query.Direction.DESCENDING)
                    .get()
                    .await()

                val reservations = snapshot.toObjects(Reservation::class.java)
                emit(reservations) // Kirim hasilnya
            } catch (e: Exception) {
                Log.e(TAG, "Gagal mengambil reservasi untuk cabang $branchName", e)
                throw e
            }
        }
    }

}

private fun ReservationEntity.toDomainModel(): Reservation {
    return Reservation(
        id = this.id,
        barberId = this.barberId,
        bookingDate = this.bookingDate,
        bookingTime = this.bookingTime,
        service = this.service,
        branchName = this.branchName,
        barberName = this.barberName,
        customerName = this.customerName,
        status = this.status,
        userId = this.userId
    )
}

private fun Reservation.toEntity(): ReservationEntity {
    return ReservationEntity(
        id = this.id,
        barberId = this.barberId,
        bookingDate = this.bookingDate,
        bookingTime = this.bookingTime,
        service = this.service,
        branchName = this.branchName,
        barberName = this.barberName,
        customerName = this.customerName,
        status = this.status,
        userId = this.userId
    )
}