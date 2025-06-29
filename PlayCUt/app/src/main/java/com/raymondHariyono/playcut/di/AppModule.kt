package com.raymondHariyono.playcut.di

import android.app.Application
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.raymondHariyono.playcut.data.local.AppDatabase
import com.raymondHariyono.playcut.data.local.ReservationDao
import com.raymondHariyono.playcut.data.remote.UnsplashApiService
import com.raymondHariyono.playcut.data.repository.AuthRepositoryImpl
import com.raymondHariyono.playcut.data.repository.BarbershopRepositoryImpl
import com.raymondHariyono.playcut.data.seeder.AdminAccountSeeder
import com.raymondHariyono.playcut.data.seeder.FirestoreSeeder
import com.raymondHariyono.playcut.domain.repository.AuthRepository
import com.raymondHariyono.playcut.domain.repository.BarbershopRepository
import com.raymondHariyono.playcut.domain.usecase.GetReservationByIdUseCase
import com.raymondHariyono.playcut.domain.usecase.admin.AddBarberUseCase
import com.raymondHariyono.playcut.domain.usecase.admin.GetReservationsByBranchUseCase
import com.raymondHariyono.playcut.domain.usecase.admin.LinkBarberAccountUseCase
import com.raymondHariyono.playcut.domain.usecase.admin.RegisterBarberAccountUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.GetUserProfileUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.CreateBookingUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.DeleteReservationUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.GetReservationsUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.LoginUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.LogoutUseCase
import com.raymondHariyono.playcut.domain.usecase.auth.RegisterUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBarberDetailsUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchDetailsUseCase
import com.raymondHariyono.playcut.domain.usecase.branch.GetBranchesUseCase
import com.raymondHariyono.playcut.domain.usecase.home.GetHomePageDataUseCase
import com.raymondHariyono.playcut.domain.usecase.home.GetInspirationPhotosUseCase
import com.raymondHariyono.playcut.domain.usecase.reservation.UpdateReservationUseCase
import com.raymondHariyono.playcut.presentation.screens.branch.detail.GetServicesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app,
            AppDatabase::class.java,
            "playcut_database" // Anda bisa mengganti nama ini jika mau
        )
            .fallbackToDestructiveMigration()
            .build()
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
    fun provideBarbershopRepository(dao: ReservationDao, firebaseAuth: FirebaseAuth): BarbershopRepository {
        return BarbershopRepositoryImpl(dao, firebaseAuth)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()


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
        authRepo: AuthRepository,
        inspirationUseCase: GetInspirationPhotosUseCase
    ): GetHomePageDataUseCase {
        return GetHomePageDataUseCase(barbershopRepo, authRepo, inspirationUseCase)
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
    fun provideCreateBookingUseCase(
        db: FirebaseFirestore, auth: FirebaseAuth
    ): CreateBookingUseCase {
        return CreateBookingUseCase(db, auth)
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

    @Provides
    @Singleton
    fun provideDeleteReservationUseCase(repo: BarbershopRepository): DeleteReservationUseCase {
        return DeleteReservationUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetReservationByIdUseCase(repo: BarbershopRepository): GetReservationByIdUseCase {
        return GetReservationByIdUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideUpdateReservationUseCase(repo: BarbershopRepository): UpdateReservationUseCase {
        return UpdateReservationUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideGetUserProfileUseCase(repo: AuthRepository): GetUserProfileUseCase {
        return GetUserProfileUseCase(repo)
    }


    @Provides
    @Singleton
    fun provideGetReservationsByBranchUseCase(repo: BarbershopRepository): GetReservationsByBranchUseCase {
        return GetReservationsByBranchUseCase(repo)
    }

    @Provides
    @Singleton
    fun provideAddBarberUseCase(
        db: FirebaseFirestore // Hanya perlu parameter FirebaseFirestore
    ): AddBarberUseCase {
        return AddBarberUseCase(db)
    }

    @Provides
    @Singleton
    fun provideFirestoreSeeder(db: FirebaseFirestore): FirestoreSeeder {
        return FirestoreSeeder(db)
    }

    @Provides
    @Singleton
    fun provideAdminAccountSeeder(auth: FirebaseAuth, db: FirebaseFirestore): AdminAccountSeeder {
        return AdminAccountSeeder(auth, db)
    }

    @Provides
    @Singleton
    fun provideLogoutUseCase(repo: AuthRepository): LogoutUseCase = LogoutUseCase(repo)


    private const val UNSPLASH_BASE_URL = "https://api.unsplash.com/"
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val json = Json { ignoreUnknownKeys = true }
        return Retrofit.Builder()
            .baseUrl(UNSPLASH_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    @Provides
    @Singleton
    fun provideUnsplashApiService(retrofit: Retrofit): UnsplashApiService {
        return retrofit.create(UnsplashApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRegisterBarberAccountUseCase(auth: FirebaseAuth): RegisterBarberAccountUseCase {
        return RegisterBarberAccountUseCase(auth)
    }

    @Provides
    @Singleton
    fun provideLinkBarberAccountUseCase(db: FirebaseFirestore): LinkBarberAccountUseCase {
        return LinkBarberAccountUseCase(db)
    }


}