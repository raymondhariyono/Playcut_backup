    package com.raymondHariyono.playcut.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class ReservationEntity(
    @PrimaryKey val id: String,
    val bookingDate: String,
    val bookingTime: String,
    val service: String,
    val branchName: String,
    val barberName: String,
    val customerName: String,
    val status: String
)