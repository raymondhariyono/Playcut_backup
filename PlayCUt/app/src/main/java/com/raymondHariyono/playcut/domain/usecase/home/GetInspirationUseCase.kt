package com.raymondHariyono.playcut.domain.usecase.home

import com.raymondHariyono.playcut.data.remote.UnsplashApiService
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto
import javax.inject.Inject

class GetInspirationPhotosUseCase @Inject constructor(
    private val api: UnsplashApiService
) {
    suspend fun execute(): List<UnsplashPhoto> {
        return api.getCollectionPhotos(
            collectionId = "4503965",
            apiKey = "9NhhX0p2EBTiBPSy2nlfF051q8C9YXIC9TlhxrFX4Ao"
        )
    }
}
