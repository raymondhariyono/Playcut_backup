package com.raymondHariyono.playcut.data

import com.raymondHariyono.playcut.R

class Branch(
    val id: Int,
    val name: String,
    val addressShort: String,
    val addressFull: String,
    val operationalHours: String,
    val imageRes: Int,
    val barbers: List<Barber>
)

class Barber(
    val id: Int,
    val name: String,
    val contact: String,
    val imageRes: Int,
    val availableTimes: List<String>
)

val barberShopData = listOf(
    Branch(
        id = 1,
        name = "Lambung Mangkurat",
        addressShort = "Jl. Lambung Mangkurat No. 12",
        addressFull = "Jl. Lambung Mangkurat No. 12, Ruko Biru Sebelah Indomaret, Banjarmasin Tengah",
        operationalHours = "09:00 - 21:00",
        imageRes = R.drawable.placeholder_branch,
        barbers = listOf(
            Barber(1, "Barber A1", "081234567801", R.drawable.placeholder_barber, listOf("09:00", "10:30", "12:00", "13:30", "15:00", "17:00")),
            Barber(2, "Barber A2", "081234567802", R.drawable.placeholder_barber, listOf("09:15", "10:45", "12:15", "14:00", "15:30", "18:00")),
            Barber(3, "Barber A3", "081234567803", R.drawable.placeholder_barber, listOf("09:30", "11:00", "12:30", "14:30", "16:00", "19:00")),
            Barber(4, "Barber A4", "081234567804", R.drawable.placeholder_barber, listOf("10:00", "11:30", "13:00", "14:45", "16:15", "20:00")),
            Barber(5, "Barber A5", "081234567805", R.drawable.placeholder_barber, listOf("10:15", "11:45", "13:15", "15:00", "17:15", "20:30"))
        )
    ),
    Branch(
        id = 2,
        name = "KM 5",
        addressShort = "Jl. A. Yani KM 5 No. 45",
        addressFull = "Jl. A. Yani KM 5 No. 45, Ruko Merah Dekat McDonald's, Banjarmasin Timur",
        operationalHours = "09:00 - 21:00",
        imageRes = R.drawable.placeholder_branch,
        barbers = listOf(
            Barber(1, "Barber B1", "081234567806", R.drawable.placeholder_barber, listOf("09:00", "10:00", "11:00", "13:00", "15:00", "18:00")),
            Barber(2, "Barber B2", "081234567807", R.drawable.placeholder_barber, listOf("09:45", "11:15", "12:45", "14:15", "16:45", "20:00")),
            Barber(3, "Barber B3", "081234567808", R.drawable.placeholder_barber, listOf("10:30", "12:00", "13:30", "15:30", "17:00", "19:30")),
            Barber(4, "Barber B4", "081234567809", R.drawable.placeholder_barber, listOf("09:30", "11:00", "12:30", "14:00", "16:30", "18:30")),
            Barber(5, "Barber B5", "081234567810", R.drawable.placeholder_barber, listOf("10:15", "11:45", "13:15", "14:45", "17:15", "20:15"))
        )
    ),
    Branch(
        id = 3,
        name = "S. Parman",
        addressShort = "Jl. S. Parman No. 8",
        addressFull = "Jl. S. Parman No. 8, Sebelah Apotek Kimia Farma, Banjarmasin Barat",
        operationalHours = "09:00 - 21:00",
        imageRes = R.drawable.placeholder_branch,
        barbers = listOf(
            Barber(7, "Barber C1", "081234567811", R.drawable.placeholder_barber, listOf("09:30", "11:00", "12:30", "14:00", "16:00", "18:30")),
            Barber(8, "Barber C2", "081234567812", R.drawable.placeholder_barber, listOf("09:00", "10:45", "12:15", "13:45", "15:15", "17:45")),
            Barber(9, "Barber C3", "081234567813", R.drawable.placeholder_barber, listOf("10:00", "11:30", "13:00", "14:30", "16:30", "19:00")),
            Barber(10, "Barber C4", "081234567814", R.drawable.placeholder_barber, listOf("10:15", "11:45", "13:15", "15:00", "17:00", "20:00")),
            Barber(11, "Barber C5", "081234567815", R.drawable.placeholder_barber, listOf("09:45", "11:15", "12:45", "14:15", "16:15", "18:45"))
        )
    ),
    Branch(
        id = 4,
        name = "Gatot",
        addressShort = "Jl. Gatot Subroto No. 21",
        addressFull = "Jl. Gatot Subroto No. 21, Ruko Hijau 3 Lantai, Banjarmasin Selatan",
        operationalHours = "09:00 - 21:00",
        imageRes = R.drawable.placeholder_branch,
        barbers = listOf(
            Barber(12, "Barber D1", "081234567816", R.drawable.placeholder_barber, listOf("09:00", "10:30", "12:00", "13:30", "15:00", "17:30")),
            Barber(13, "Barber D2", "081234567817", R.drawable.placeholder_barber, listOf("09:45", "11:15", "12:45", "14:15", "16:45", "19:15")),
            Barber(14, "Barber D3", "081234567818", R.drawable.placeholder_barber, listOf("10:15", "11:45", "13:15", "14:45", "17:15", "20:30")),
            Barber(15, "Barber D4", "081234567819", R.drawable.placeholder_barber, listOf("09:30", "11:00", "12:30", "14:00", "16:00", "18:30")),
            Barber(16, "Barber D5", "081234567820", R.drawable.placeholder_barber, listOf("10:00", "11:30", "13:00", "15:00", "17:00", "19:30"))
        )
    ),
    Branch(
        id = 5,
        name = "Veteran",
        addressShort = "Jl. Veteran No. 66",
        addressFull = "Jl. Veteran No. 66, Ruko Abu-abu Sebelah Alfamidi, Banjarmasin Utara",
        operationalHours = "09:00 - 21:00",
        imageRes = R.drawable.placeholder_branch,
        barbers = listOf(
            Barber(17, "Barber E1", "081234567821", R.drawable.placeholder_barber, listOf("09:00", "10:00", "11:30", "13:00", "15:00", "17:00")),
            Barber(18, "Barber E2", "081234567822", R.drawable.placeholder_barber, listOf("09:30", "11:00", "12:30", "14:00", "16:30", "18:30")),
            Barber(19, "Barber E3", "081234567823", R.drawable.placeholder_barber, listOf("10:00", "11:30", "13:00", "15:00", "17:30", "20:00")),
            Barber(20, "Barber E4", "081234567824", R.drawable.placeholder_barber, listOf("10:15", "11:45", "13:15", "14:45", "17:15", "20:15")),
            Barber(21, "Barber E5", "081234567825", R.drawable.placeholder_barber, listOf("09:45", "11:15", "12:45", "14:15", "16:15", "18:45"))
        )
    )
)
