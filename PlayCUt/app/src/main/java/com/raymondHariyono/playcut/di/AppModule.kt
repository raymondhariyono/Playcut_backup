package com.raymondHariyono.playcut.di

import android.app.Application
import androidx.room.Room
import com.raymondHariyono.playcut.data.local.AppDatabase
import com.raymondHariyono.playcut.data.local.ReservationDao
import com.raymondHariyono.playcut.data.repository.AuthRepositoryImpl
import com.raymondHariyono.playcut.data.repository.BarbershopRepositoryImpl
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.*
import com.raymondHariyono.playcut.presentation.screens.branch.detail.GetServicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Database & DAO Providers ---

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "playcut_database" // Anda bisa mengganti nama ini jika mau
        ).build()
    }

    @Provides
    @Singleton
    fun provideReservationDao(db: AppDatabase): ReservationDao {
        return db.reservationDao()
    }

    // --- Repository Providers ---

    @Provides
    @Singleton
    fun provideAuthRepository(): AuthRepository {
        // Menyediakan implementasi untuk AuthRepository
        return AuthRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideBarbershopRepository(dao: ReservationDao): BarbershopRepository {
        // Menyediakan implementasi untuk BarbershopRepository.
        // Hilt secara otomatis akan memberikan 'dao' dari fungsi provideReservationDao di atas.
        return BarbershopRepositoryImpl(dao)
    }

    // --- Use Case Providers (BAGIAN PALING PENTING) ---
    // Hilt perlu tahu cara membuat SETIAP UseCase yang akan di-inject ke ViewModel.

    @Provides
    @Singleton
    fun provideLoginUseCase(repo: AuthRepository): LoginUseCase {
        return LoginUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideRegisterUseCase(repo: AuthRepository): RegisterUseCase {
        return RegisterUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetHomePageDataUseCase(
        barbershopRepo: BarbershopRepository,
        authRepo: AuthRepository
    ): GetHomePageDataUseCase {
        return GetHomePageDataUseCase(barbershopRepo, authRepo)
    }

    @Provides
    @Singleton
    fun provideGetBranchesUseCase(repo: BarbershopRepository): GetBranchesUseCase {
        return GetBranchesUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetBranchDetailsUseCase(repo: BarbershopRepository): GetBranchDetailsUseCase {
        return GetBranchDetailsUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetServicesUseCase(repo: BarbershopRepository): GetServicesUseCase {
        return GetServicesUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideCreateBookingUseCase(repo: BarbershopRepository): CreateBookingUseCase {
        return CreateBookingUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetBarberDetailsUseCase(repo: BarbershopRepository): GetBarberDetailsUseCase {
        return GetBarberDetailsUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetReservationsUseCase(repo: BarbershopRepository): GetReservationsUseCase {
        return GetReservationsUseCase(repo)
    }
}