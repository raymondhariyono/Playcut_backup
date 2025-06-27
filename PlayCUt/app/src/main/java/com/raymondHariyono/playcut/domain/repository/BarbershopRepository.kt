package com.raymondHariyono.playcut.domain.repository

import com.raymondHariyono.playcut.domain.model.*
import com.raymondHariyono.playcut.domain.usecase.branch.BarberDetails
import kotlinx.coroutines.flow.Flow

interface BarbershopRepository {

    // --- Data Remote (Firestore) ---
    fun getBranches(): Flow<List<Branch>>
    fun getBranchById(branchId: Int): Flow<Branch?>
    fun getBarberById(barberId: Int): Flow<BarberDetails?>
    fun getServices(): Flow<List<Service>>
    fun getHomeServices(): Flow<List<HomeService>>
    fun getInspirations(): Flow<List<Inspiration>>
    suspend fun addBarberToBranch(branchId: Int, barber: Barber): Result<Unit>

    // --- Data Lokal (Room) ---
    fun getReservations(): Flow<List<Reservation>>
    fun getReservationById(reservationId: String): Flow<Reservation?>
    suspend fun saveReservation(reservation: Reservation)
    suspend fun updateReservation(reservation: Reservation)
    suspend fun deleteReservation(reservationId: String)

    fun getReservationsByBranch(branchName: String): Flow<List<Reservation>>
}