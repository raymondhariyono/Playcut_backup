package com.raymondHariyono.playcut.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnsplashPhoto(
    val id: String,
    val description: String? = null,
    @SerialName("urls") val urls: UnsplashUrls,
    @SerialName("user") val user: UnsplashUser
)

@Serializable
data class UnsplashUrls(
    @SerialName("regular") val regular: String, // Kita akan pakai ukuran ini
    @SerialName("small") val small: String
)

@Serializable
data class UnsplashUser(
    @SerialName("name") val name: String
)