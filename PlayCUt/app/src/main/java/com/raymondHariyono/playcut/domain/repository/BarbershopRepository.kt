package com.raymondHariyono.playcut.domain.repository

import com.raymondHariyono.playcut.domain.model.Barber
import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.HomeService
import com.raymondHariyono.playcut.domain.model.Inspiration
import com.raymondHariyono.playcut.domain.model.Reservation
import com.raymondHariyono.playcut.domain.model.Service
import kotlinx.coroutines.flow.Flow

interface BarbershopRepository {

    fun getBranches(): Flow<List<Branch>>

    fun getBranchById(branchId: Int): Flow<Branch?>

    fun getBarberById(barberId: Int): Flow<Barber?>

    fun getServices(): Flow<List<Service>>

    fun getReservations(): Flow<List<Reservation>>

    fun getHomeServices(): Flow<List<HomeService>>

    fun getInspirations(): Flow<List<Inspiration>>

    suspend fun saveReservation(reservation: Reservation)

}