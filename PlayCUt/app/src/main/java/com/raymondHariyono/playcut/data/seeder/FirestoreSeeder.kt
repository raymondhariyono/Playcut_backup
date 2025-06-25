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

    // 2. DATA CABANG DIPERBANYAK MENJADI 5
    private val branchesToSeed = listOf(
        // Cabang 1 (Lama)
        Branch(
            id = 1, name = "Lambung Mangkurat", addressShort = "Jl. Lambung Mangkurat No. 12",
            addressFull = "Jl. Lambung Mangkurat No. 12, Ruko Biru Sebelah Indomaret, Banjarmasin Tengah",
            operationalHours = "09:00 - 21:00", imageRes = "placeholder_branch",
            barbers = listOf(
                Barber(1, "Budi", "081234567801", "placeholder_barber", listOf("09:00", "10:30")),
                Barber(2, "Agus", "081234567802", "placeholder_barber", listOf("09:15", "10:45")),
            )
        ),
        // Cabang 2 (Lama)
        Branch(
            id = 2, name = "A. Yani KM 5", addressShort = "Jl. A. Yani KM 5 No. 45",
            addressFull = "Jl. A. Yani KM 5 No. 45, Ruko Merah Dekat McDonald's, Banjarmasin Timur",
            operationalHours = "09:00 - 21:00", imageRes = "placeholder_branch",
            barbers = listOf(
                Barber(3, "Candra", "081234567806", "placeholder_barber", listOf("09:00", "10:00")),
                Barber(4, "Dedi", "081234567807", "placeholder_barber", listOf("09:45", "11:15")),
            )
        ),
        // Cabang 3 (Baru)
        Branch(
            id = 3, name = "Sultan Adam", addressShort = "Jl. Sultan Adam No. 33",
            addressFull = "Jl. Sultan Adam No. 33, Komplek Mandiri, Banjarmasin Utara",
            operationalHours = "10:00 - 22:00", imageRes = "placeholder_branch",
            barbers = listOf(
                Barber(5, "Eko", "081234567810", "placeholder_barber", listOf("10:00", "11:00")),
            )
        ),
        // Cabang 4 (Baru)
        Branch(
            id = 4, name = "Gatot Subroto", addressShort = "Jl. Gatot Subroto No. 101",
            addressFull = "Jl. Gatot Subroto No. 101, Seberang Hotel Rattan Inn, Banjarmasin Timur",
            operationalHours = "09:00 - 21:00", imageRes = "placeholder_branch",
            barbers = listOf(
                Barber(6, "Fajar", "081234567812", "placeholder_barber", listOf("09:30", "10:30")),
                Barber(7, "Giri", "081234567813", "placeholder_barber", listOf("13:00", "14:00")),
            )
        ),
        // Cabang 5 (Baru)
        Branch(
            id = 5, name = "Banjarbaru", addressShort = "Jl. Panglima Batur, Banjarbaru",
            addressFull = "Jl. Panglima Batur, Ruko No. 5, Dekat Lapangan Murjani, Banjarbaru",
            operationalHours = "10:00 - 22:00", imageRes = "placeholder_branch",
            barbers = listOf(
                Barber(8, "Hadi", "081234567815", "placeholder_barber", listOf("10:00", "11:00", "12:00")),
            )
        )
    )

    // Logika seeder tidak perlu diubah, sudah benar
    suspend fun seedDataIfNeeded() {
        Log.d("FirestoreSeeder", "Checking if seeding is needed...")
        // Seed services
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

        // Seed branches
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