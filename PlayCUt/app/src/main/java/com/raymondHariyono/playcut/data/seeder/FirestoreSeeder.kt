package com.raymondHariyono.playcut.data.seeder

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.Service
import kotlinx.coroutines.tasks.await

class FirestoreSeeder(private val db: FirebaseFirestore) {

    private val servicesToSeed = listOf(
        Service("Haircut", "Rp 50.000", "Potong Rambut"),
        Service("Coloring", "Rp 150.000", "Pewarnaan"),
        Service("Shaving", "Rp 35.000", "Cukur & Perawatan"),
        Service("Spa", "Rp 100.000", "Perawatan Rambut"),
        Service("Perming", "Rp 250.000", "Pengeritingan"),
        Service("Braids", "Rp 80.000", "Gaya Rambut")
    )

    // ... (di dalam kelas FirestoreSeeder)

    private val branchesToSeed = listOf(
        Branch(
            id = 1,
            name = "Playcuts Lambung Mangkurat",
            addressFull = "Jl. Lambung Mangkurat No.22, Tlk. Dalam, Kec. Banjarmasin Tengah, Kota Banjarmasin",
            imageRes = "https://lh5.googleusercontent.com/p/AF1QipMh-e-6_v9Lg5qYjX1n3g9R2bX8c9J6H4v-d_Xw=w408-h306-k-no",
            latitude = -3.32849, // <-- DATA BARU
            longitude = 114.59011  // <-- DATA BARU
        ),
        Branch(
            id = 2,
            name = "Playcuts A. Yani KM 5",
            addressFull = "Jl. A. Yani No.5, Pemurus Dalam, Kec. Banjarmasin Sel., Kota Banjarmasin",
            imageRes = "https://lh5.googleusercontent.com/p/AF1QipM2z-O9o-4G6_y-I6h7_X8a7tP6l5_kC-y8n-Zk=w408-h544-k-no",
            latitude = -3.34320, // <-- DATA BARU
            longitude = 114.62020  // <-- DATA BARU
        ),
        Branch(
            id = 3,
            name = "Playcuts Sultan Adam",
            addressFull = "Jl. Sultan Adam, Surgi Mufti, Kec. Banjarmasin Utara, Kota Banjarmasin",
            imageRes = "https://lh5.googleusercontent.com/p/AF1QipP_rY2X-YJ5_g9K-Z0_wE9R-T8I_O6m4k-hN-fX=w408-h306-k-no",
            latitude = -3.30836, // <-- DATA BARU
            longitude = 114.59977  // <-- DATA BARU
        ),
        Branch(
            id = 4,
            name = "Playcuts Gatot Subroto",
            addressFull = "Jl. Gatot Subroto No.7, Kuripan, Kec. Banjarmasin Tim., Kota Banjarmasin",
            imageRes = "https://lh5.googleusercontent.com/p/AF1QipP_rY2X-YJ5_g9K-Z0_wE9R-T8I_O6m4k-hN-fX=w408-h306-k-no",
            latitude = -3.32263,
            longitude = 114.60531
        ),
        Branch(
            id = 5,
            name = "Playcuts Banjarbaru",
            addressFull = "Jl. Panglima Batur, Loktabat Utara, Kec. Banjarbaru Utara, Kota Banjarbaru",
            imageRes = "https://lh5.googleusercontent.com/p/AF1QipP_rY2X-YJ5_g9K-Z0_wE9R-T8I_O6m4k-hN-fX=w408-h306-k-no",
            latitude = -3.43300,
            longitude = 114.81512
        )
    )

    suspend fun seedDataIfNeeded() {
        Log.d("FirestoreSeeder", "Checking if seeding is needed...")
        if (isCollectionEmpty("services")) {
            Log.d("FirestoreSeeder", "Seeding services...")
            val batch = db.batch()
            servicesToSeed.forEach { service ->
                val docRef = db.collection("services").document()
                batch.set(docRef, service)
            }
            batch.commit().await()
            Log.d("FirestoreSeeder", "Services seeded successfully.")
        } else {
            Log.d("FirestoreSeeder", "Services collection already has data. Skipping.")
        }

        if (isCollectionEmpty("branches")) {
            Log.d("FirestoreSeeder", "Seeding branches...")
            val batch = db.batch()
            branchesToSeed.forEach { branch ->
                val docRef = db.collection("branches").document("branch-${branch.id}")
                batch.set(docRef, branch)
            }
            batch.commit().await()
            Log.d("FirestoreSeeder", "Branches seeded successfully.")
        } else {
            Log.d("FirestoreSeeder", "Branches collection already has data. Skipping.")
        }
    }

    private suspend fun isCollectionEmpty(collectionPath: String): Boolean {
        return try {
            val snapshot = db.collection(collectionPath).limit(1).get().await()
            snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("FirestoreSeeder", "Error checking collection $collectionPath", e)
            true
        }
    }
}