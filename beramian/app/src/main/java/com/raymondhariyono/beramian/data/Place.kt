// com/raymondhariyono/beramian/data/Place.kt
package com.raymondhariyono.beramian.data

import androidx.compose.runtime.Immutable
import com.raymondhariyono.beramian.R

@Immutable
data class Place(
    val name: String,
    val location: String,
    val imageResId: Int,
    val rating: Float? = null,
    val reviewCount: Int? = null
)

val kalselPlaces = listOf(
    Place(
        name = "Pasar Terapung Lok Baintan",
        location = "Sungai Tabuk, Kab. Banjar",
        imageResId = R.drawable.kalsel,
        rating = 4.5f,
        reviewCount = 1200
    ),
    Place(
        name = "Bukit Matang Kaladan",
        location = "Tiwingan Lama, Kab. Banjar",
        imageResId = R.drawable.kalsel,
        rating = 4.8f,
        reviewCount = 980
    ),
    Place(
        name = "Pulau Kembang",
        location = "Alalak, Barito Kuala",
        imageResId = R.drawable.kalsel,
        rating = 4.3f,
        reviewCount = 750
    ),
    Place(
        name = "Air Terjun Kilat Api",
        location = "Loksado, Hulu Sungai Selatan",
        imageResId = R.drawable.kalsel,
        rating = 4.6f,
        reviewCount = 300
    ),
    Place(
        name = "Danau Seran",
        location = "Banjarbaru",
        imageResId = R.drawable.kalsel,
        rating = 4.2f,
        reviewCount = 500
    ),
    Place(
        name = "Pantai Angsana",
        location = "Angsana, Tanah Bumbu",
        imageResId = R.drawable.kalsel,
        rating = 4.5f,
        reviewCount = 1500
    ),
    Place(
        name = "Taman Labirin Pelaihari",
        location = "Tambang Ulang, Tanah Laut",
        imageResId = R.drawable.kalsel,
        rating = 4.0f,
        reviewCount = 450
    ),
    Place(
        name = "Pegunungan Meratus",
        location = "Kalsel (area luas)",
        imageResId = R.drawable.kalsel,
        rating = 4.7f, // Rating umum untuk area luas
        reviewCount = 100 // Contoh
    ),
    Place(
        name = "Kebun Raya Banua",
        location = "Banjarbaru",
        imageResId = R.drawable.kalsel,
        rating = 4.4f,
        reviewCount = 600
    ),
    Place(
        name = "Amanah Borneo Park",
        location = "Cindai Alus, Kab. Banjar",
        imageResId = R.drawable.kalsel,
        rating = 4.6f,
        reviewCount = 850
    ),
    Place(
        name = "Loksado (Arung Jeram)",
        location = "Hulu Sungai Selatan",
        imageResId = R.drawable.kalsel,
        rating = 4.9f, // Aktivitas populer
        reviewCount = 700
    ),
    Place(
        name = "Masjid Raya Sabilal Muhtadin",
        location = "Banjarmasin",
        imageResId = R.drawable.kalsel,
        rating = 4.8f,
        reviewCount = 2000
    ),
    Place(
        name = "Siring Sungai Martapura",
        location = "Banjarmasin",
        imageResId = R.drawable.kalsel,
        rating = 4.5f,
        reviewCount = 1300
    ),
    Place(
        name = "Jembatan Barito",
        location = "Barito Kuala",
        imageResId = R.drawable.kalsel,
        rating = 4.1f,
        reviewCount = 400
    ),
    Place(
        name = "Goa Batu Hapu",
        location = "Paringin, Balangan",
        imageResId = R.drawable.kalsel,
        rating = null,
        reviewCount = null
    )
)