// File: app/src/main/java/com/raymondHariyono/playcut/domain/usecase/GetHomePageDataUseCase.kt
package com.raymondHariyono.playcut.domain.usecase.home

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
    /**
     * Use case untuk mengambil semua data yang dibutuhkan oleh Halaman Utama (Home).
     * Ini menggabungkan data profil pengguna dengan data barbershop.
     */
    operator fun invoke(): Flow<HomePageData> = flow {
        // Langkah 1: Panggil suspend function untuk mendapatkan profil pengguna saat ini.
        val userProfile = authRepository.getCurrentUserProfile()

        val dynamicUserName = when (userProfile) {
            is UserProfile.Admin -> userProfile.name
            is UserProfile.Barber -> userProfile.name
            is UserProfile.Customer -> userProfile.name
            else -> "Pengguna"
        }

        combine(
            barbershopRepository.getBranches(),
            barbershopRepository.getHomeServices(),
            barbershopRepository.getInspirations()
        ) { branches, services, inspirations ->
            // Langkah 4: Buat objek HomePageData dengan semua data yang sudah terkumpul.
            HomePageData(
                userName = dynamicUserName,
                promotions = branches.take(3), // Ambil 3 cabang teratas sebagai promosi
                homeServices = services,
                inspirations = inspirations
            )
        }.collect { combinedData ->
            // Langkah 5: Pancarkan (emit) data yang sudah jadi sebagai hasil akhir dari Flow ini.
            emit(combinedData)
        }
    }
}