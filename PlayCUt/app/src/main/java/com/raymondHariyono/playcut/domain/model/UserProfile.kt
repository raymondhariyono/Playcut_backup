package com.raymondHariyono.playcut.domain.model

sealed class UserProfile {

    data class Barber(
        val docPath: String, // Path dokumen untuk update
        val name: String,
        val contact: String,
        val imageRes: String
    ) : UserProfile()

    data class Customer(
        val docPath: String,
        val name: String,
        val phoneNumber: String
    ) : UserProfile()

    data class Admin(
        val docPath: String,
        val name: String,
        val branchId: Int,
        val branchName: String
    ) : UserProfile()

    object Unknown : UserProfile() // Untuk state default atau error
}