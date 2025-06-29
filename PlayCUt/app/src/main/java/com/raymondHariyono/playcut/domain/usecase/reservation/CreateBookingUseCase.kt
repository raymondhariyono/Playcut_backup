package com.raymondHariyono.playcut.domain.usecase.reservation

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.raymondHariyono.playcut.domain.model.Reservation
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class CreateBookingUseCase(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    suspend operator fun invoke(
        barberId: Int,
        barberName: String,
        branchName: String,
        service: String,
        bookingTime: String // Format "HH:mm"
    ): Result<Unit> {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid
        val customerName = currentUser?.displayName ?: "Pengguna Tamu"

        if (userId == null) {
            return Result.failure(Exception("Anda harus login untuk membuat reservasi."))
        }

        // --- VALIDASI 1: Apakah waktu booking sudah lewat? ---
        val bookingDateTime = createTimestampFromTime(bookingTime)
        if (bookingDateTime < Timestamp.now()) {
            return Result.failure(Exception("Waktu yang Anda pilih sudah lewat. Silakan pilih waktu lain."))
        }

        try {
            // --- VALIDASI 2: Apakah user ini sudah punya booking aktif di cabang lain? ---
            val existingBookingsSnapshot = db.collection("reservations")
                .whereEqualTo("userId", userId)
                .whereGreaterThan("dateTime", Timestamp.now()) // Hanya cek booking yang akan datang
                .limit(1)
                .get()
                .await()

            if (!existingBookingsSnapshot.isEmpty) {
                // User sudah punya booking, cek apakah di cabang yang sama atau berbeda
                val existingBranch = existingBookingsSnapshot.documents.first().getString("branchName")
                if (existingBranch != branchName) {
                    return Result.failure(Exception("Anda sudah memiliki reservasi aktif di cabang $existingBranch. Selesaikan dulu reservasi tersebut."))
                }
            }

            // --- VALIDASI 3: Apakah slot waktu & barber ini sudah dibooking orang lain? ---
            val slotTakenSnapshot = db.collection("reservations")
                .whereEqualTo("barberId", barberId)
                .whereEqualTo("dateTime", bookingDateTime) // Cek kombinasi barber dan waktu yang sama persis
                .limit(1)
                .get()
                .await()

            if (!slotTakenSnapshot.isEmpty) {
                return Result.failure(Exception("Jadwal ini baru saja dipesan oleh orang lain. Silakan pilih waktu atau barber lain."))
            }

            // --- Jika semua validasi lolos, lanjutkan membuat reservasi ---
            val newReservation = Reservation(
                id = db.collection("reservations").document().id, // Generate ID otomatis
                userId = userId,
                customerName = customerName,
                barberId = barberId,
                barberName = barberName,
                branchName = branchName,
                service = service,
                bookingDate = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID")).format(bookingDateTime.toDate()),
                bookingTime = bookingTime,
                dateTime = bookingDateTime,
                status = "Booked"
            )

            // Simpan ke Firestore
            db.collection("reservations").document(newReservation.id).set(newReservation).await()

            return Result.success(Unit)

        } catch (e: Exception) {
            return Result.failure(Exception("Gagal membuat reservasi: ${e.message}"))
        }
    }

    // Fungsi helper untuk membuat Timestamp dari string waktu "HH:mm" untuk hari ini
    private fun createTimestampFromTime(timeString: String): Timestamp {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val date = sdf.parse(timeString) ?: return Timestamp.now()

        val calendar = Calendar.getInstance()
        val bookingCalendar = Calendar.getInstance()
        bookingCalendar.time = date

        calendar.set(Calendar.HOUR_OF_DAY, bookingCalendar.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, bookingCalendar.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        return Timestamp(calendar.time)
    }
}