package com.example.listxml.data.mapper

import com.example.listxml.data.local.entity.MovieEntity
import com.example.listxml.data.remote.dto.MovieDto
import com.example.listxml.domain.model.Movie
import kotlin.jvm.JvmName // Impor JvmName

private const val TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/"
private const val DEFAULT_IMAGE_SIZE = "w500"

// Mapper DTO ke Domain
fun MovieDto.toDomain(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = this.posterPath?.let { path ->
            "$TMDB_IMAGE_BASE_URL$DEFAULT_IMAGE_SIZE$path"
        }
    )
}

@JvmName("movieDtoListToDomain") // Tambahkan @JvmName
fun List<MovieDto>.toDomain(): List<Movie> {
    return this.map { it.toDomain() }
}

// Mapper Domain ke Entity
fun Movie.toEntity(): MovieEntity {
    return MovieEntity(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = this.posterUrl
    )
}

@JvmName("movieDomainListToEntity") // Tambahkan @JvmName (opsional tapi konsisten)
fun List<Movie>.toEntity(): List<MovieEntity> {
    return this.map { it.toEntity() }
}

// Mapper Entity ke Domain
fun MovieEntity.toDomain(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        overview = this.overview,
        posterUrl = this.posterUrl
    )
}

@JvmName("movieEntityListToDomain") // Tambahkan @JvmName
fun List<MovieEntity>.toDomain(): List<Movie> {
    return this.map { it.toDomain() }
}
