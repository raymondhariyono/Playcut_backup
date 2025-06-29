package com.raymondHariyono.playcut.data.remote

import com.raymondHariyono.playcut.domain.model.UnsplashPhoto
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface UnsplashApiService {

    @Headers("Accept-Version: v1")
    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") collectionId: String,
        @Query("client_id") apiKey: String,
        @Query("per_page") perPage: Int = 30
    ): List<UnsplashPhoto>
}
