package com.raymondHariyono.playcut.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {
    @Query("SELECT * FROM reservations ORDER BY bookingDate DESC")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Query("DELETE FROM reservations WHERE id = :reservationId")
    suspend fun deleteReservationById(reservationId: String)

    @Query("SELECT * FROM reservations WHERE id = :reservationId")
    fun getReservationById(reservationId: String): Flow<ReservationEntity?>

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

    @Query("SELECT * FROM reservations WHERE branchName = :branchName ORDER BY bookingDate DESC")
    fun getReservationsByBranch(branchName: String): Flow<List<ReservationEntity>>
}