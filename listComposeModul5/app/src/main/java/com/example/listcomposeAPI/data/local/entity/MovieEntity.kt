package com.example.listxml.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterUrl: String?
    // val releaseYear: String? // Dihilangkan sementara karena belum ada sumber data
)