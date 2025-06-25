package com.raymondHariyono.playcut.domain.model
data class Branch(
    val id: Int = 0,
    val name: String = "",
    val addressShort: String = "",
    val addressFull: String = "",
    val operationalHours: String = "",
    val imageRes: String = "",
    val barbers: List<Barber> = emptyList() // Beri list kosong sebagai default
)