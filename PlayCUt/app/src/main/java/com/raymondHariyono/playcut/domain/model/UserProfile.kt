package com.raymondHariyono.playcut.domain.model

data class UserProfile(
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val photoUrl: String = "",
    val role: String = "customer",
    val branchId: Int = 0,
    val branchName: String = ""
)