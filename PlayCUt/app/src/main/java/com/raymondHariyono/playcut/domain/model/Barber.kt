package com.raymondHariyono.playcut.domain.model

data class Barber(
    val id: Int = 0,
    val name: String = "",
    val contact: String = "",
    var imageRes: String = "",
    val availableTimes: List<String> = emptyList(),
    val status: String = "active",
    val authUid: String? = null
)