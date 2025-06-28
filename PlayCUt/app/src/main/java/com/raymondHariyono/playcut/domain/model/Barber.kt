package com.raymondHariyono.playcut.domain.model

data class  Barber(
    val id: Int = 0,
    val name: String = "",
    val contact: String = "",
    val imageRes: String = "",
    val availableTimes: List<String> = emptyList(),
    val userId: String? = null
)