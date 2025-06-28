package com.raymondHariyono.playcut.domain.model

import com.google.firebase.Timestamp

data class Reservation(
    val id: String = "",
    val barberId: Int = 0,
    val bookingDate: String = "",
    val bookingTime: String = "",
    val service: String = "",
    val branchName: String = "",
    val barberName: String = "",
    val customerName: String = "",
    val status: String = "",
    val userId: String = "",
    val dateTime: Timestamp = Timestamp.now()
)
