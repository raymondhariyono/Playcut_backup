    package com.raymondHariyono.playcut.data.repository

    import android.util.Log
    import com.google.firebase.firestore.FirebaseFirestore
    import com.raymondHariyono.playcut.data.local.ReservationDao
    import com.raymondHariyono.playcut.data.local.ReservationEntity
    import com.raymondHariyono.playcut.domain.model.*
    import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
    import kotlinx.coroutines.flow.Flow
    import kotlinx.coroutines.flow.first
    import kotlinx.coroutines.flow.flow
    import kotlinx.coroutines.flow.map
    import kotlinx.coroutines.tasks.await

    class BarbershopRepositoryImpl(
        private val reservationDao: ReservationDao
    ) : BarbershopRepository {

        private val firestore = FirebaseFirestore.getInstance()
        private val TAG = "firestoreRepo"

        override fun getBranches(): Flow<List<Branch>> = flow {
            Log.d(TAG, "MEMULAI getBranches dengan mapping manual...")
            try {
                val snapshot = firestore.collection("branches").get().await()
                Log.d(TAG, "Berhasil mengambil ${snapshot.size()} dokumen.")

                val branches = snapshot.documents.mapNotNull { doc ->
                    try {
                        Log.d(TAG, "Mapping dokumen: ${doc.id}")

                        // Mapping untuk list barber yang ada di dalam branch
                        val barbersListMap = doc.get("barbers") as? List<Map<String, Any>> ?: emptyList()
                        val barbersList = barbersListMap.map { barberMap ->
                            Barber(
                                id = (barberMap["id"] as? Long)?.toInt() ?: 0,
                                name = barberMap["name"] as? String ?: "",
                                contact = barberMap["contact"] as? String ?: "",
                                imageRes = barberMap["imageRes"] as? String ?: "",
                                availableTimes = barberMap["availableTimes"] as? List<String> ?: emptyList()
                            ).also { Log.d(TAG, "  - Barber '${it.name}' OK") }
                        }

                        // Mapping untuk branch itu sendiri
                        Branch(
                            id = doc.getLong("id")?.toInt() ?: 0,
                            name = doc.getString("name") ?: "",
                            addressShort = doc.getString("addressShort") ?: "",
                            addressFull = doc.getString("addressFull") ?: "",
                            operationalHours = doc.getString("operationalHours") ?: "",
                            imageRes = doc.getString("imageRes") ?: "",
                            barbers = barbersList
                        ).also { Log.d(TAG, "SELESAI mapping dokumen: ${doc.id}") }

                    } catch (e: Exception) {
                        Log.e(TAG, "!!! GAGAL MAPPING DOKUMEN ${doc.id} !!!", e)
                        null // Lewati dokumen yang error
                    }
                }
                Log.d(TAG, "Semua dokumen berhasil di-mapping. Mengirim ${branches.size} cabang.")
                emit(branches)
            } catch (e: Exception) {
                Log.e(TAG, "!!! GAGAL MENGAMBIL KOLEKSI 'branches' !!!", e)
                emit(emptyList()) // Emit list kosong jika terjadi error besar
            }
        }

        override fun getBarberById(barberId: Int): Flow<Barber?> {
            return getBranches().map { allBranches ->
                val foundBarber = allBranches.flatMap { it.barbers }.find { it.id == barberId }
                Log.d(TAG, "Pencarian barber ID $barberId selesai. Ditemukan: ${foundBarber != null}")
                foundBarber
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
                emit(emptyList())
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
            return reservationDao.getAllReservations().map { entityList ->
                entityList.map { entity ->
                    Reservation(
                        id = entity.id,
                        bookingDate = entity.bookingDate,
                        bookingTime = entity.bookingTime,
                        service = entity.service,
                        branchName = entity.branchName,
                        barberName = entity.barberName,
                        customerName = entity.customerName,
                        status = entity.status
                    )
                }
            }
        }

        override suspend fun saveReservation(reservation: Reservation) {
            val reservationEntity = ReservationEntity(
                id = reservation.id,
                bookingDate = reservation.bookingDate,
                bookingTime = reservation.bookingTime,
                service = reservation.service,
                branchName = reservation.branchName,
                barberName = reservation.barberName,
                customerName = reservation.customerName,
                status = reservation.status
            )
            reservationDao.insertReservation(reservationEntity)
        }

        override fun getBranchById(branchId: Int): Flow<Branch?> = flow {
            val documentId = "branch-$branchId"
            Log.d(TAG, "Mencari cabang dengan ID dokumen: $documentId")

            val snapshot = firestore.collection("branches").document(documentId).get().await()
            if (snapshot.exists()) {
                val branch = snapshot.toObject(Branch::class.java)
                Log.d(TAG, "Cabang ditemukan: $branch?.name")
                emit(branch)
            } else {
                Log.d(TAG, "Cabang dengan ID $branchId tidak ditemukan.")
                emit(null)
            }
        }
    }