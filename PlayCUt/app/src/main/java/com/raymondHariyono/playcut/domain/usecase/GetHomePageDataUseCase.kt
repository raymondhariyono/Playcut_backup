// File: app/src/main/java/com/raymondHariyono/playcut/domain/usecase/GetHomePageDataUseCase.kt
package com.raymondHariyono.playcut.domain.usecase

import com.raymondHariyono.playcut.domain.model.Branch
import com.raymondHariyono.playcut.domain.model.HomeService
import com.raymondHariyono.playcut.domain.model.Inspiration
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow

data class HomePageData(
    val userName: String,
    val promotions: List<Branch>,
    val homeServices: List<HomeService>,
    val inspirations: List<Inspiration>
)

class GetHomePageDataUseCase(
    private val barbershopRepository: BarbershopRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<HomePageData> = flow {
        val userProfile: UserProfile? = authRepository.getCurrentUserProfile()
        val dynamicUserName = userProfile?.name ?: "Pengguna"

        combine(
            barbershopRepository.getBranches(),
            barbershopRepository.getHomeServices(),
            barbershopRepository.getInspirations()
        ) { branches, services, inspirations ->
            HomePageData(
                userName = dynamicUserName,
                promotions = branches.take(3),
                homeServices = services,
                inspirations = inspirations
            )
        }.collect { combinedData ->
            emit(combinedData)
        }
    }
}