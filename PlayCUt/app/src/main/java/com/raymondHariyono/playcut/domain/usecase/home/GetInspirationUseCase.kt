package com.raymondHariyono.playcut.domain.usecase.inspiration

import com.raymondHariyono.playcut.data.remote.UnsplashApiService
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto
import javax.inject.Inject

class GetInspirationPhotosUseCase @Inject constructor(
    private val unsplashApi: UnsplashApiService
) {
    private val API_KEY = "9NhhX0p2EBTiBPSy2nlfF051q8C9YXIC9TlhxrFX4Ao"
    private val COLLECTION_ID = "4503965" // ID dari koleksi "men's hairstyle"

    suspend operator fun invoke(query: String): Result<List<UnsplashPhoto>> {
        if (API_KEY.startsWith("GANTI_DENGAN")) {
            return Result.failure(Exception("Kunci API Unsplash belum dimasukkan."))
        }
        return try {
            val photos = unsplashApi.getCollectionPhotos(COLLECTION_ID, API_KEY)
            Result.success(photos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}