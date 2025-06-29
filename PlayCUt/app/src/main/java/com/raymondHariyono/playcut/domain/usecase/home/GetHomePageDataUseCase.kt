package com.raymondHariyono.playcut.domain.usecase.home

import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.inspiration.GetInspirationPhotosUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

data class HomePageData(
    val userName: String,
    val promotions: List<Branch>,
    val inspirations: List<UnsplashPhoto>
)

class GetHomePageDataUseCase @Inject constructor(
    private val barbershopRepository: BarbershopRepository,
    private val authRepository: AuthRepository,
    private val getInspirationPhotosUseCase: GetInspirationPhotosUseCase
) {

    operator fun invoke(): Flow<HomePageData> = flow {
        val profile = authRepository.getCurrentUserProfile()
        val inspirationResult = getInspirationPhotosUseCase("men's fade haircut")

        val userName = when (profile) {
            is UserProfile.Admin -> profile.name
            is UserProfile.Barber -> profile.name
            is UserProfile.Customer -> profile.name
            else -> "Pengguna"
        }

        val photos = inspirationResult.getOrNull() ?: emptyList()

        val homeData = HomePageData(
            userName = userName,
            promotions = emptyList(),
            inspirations = photos
        )

        emit(homeData)
    }
}