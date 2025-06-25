// File: app/src/main/java/com/raymondHariyono/playcut/presentation/navigation/AppNavigation.kt
package com.raymondHariyono.playcut.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raymondHariyono.playcut.presentation.screens.booking.BookingPage
import com.raymondHariyono.playcut.presentation.screens.branch.detail.DetailBranchPage
import com.raymondHariyono.playcut.presentation.screens.branch.search.SearchBranchPage
import com.raymondHariyono.playcut.presentation.screens.home.HomePage
import com.raymondHariyono.playcut.presentation.screens.onboarding.OnBoardingPage
import com.raymondHariyono.playcut.presentation.screens.auth.login.LoginPage
import com.raymondHariyono.playcut.presentation.screens.user.RegisterPage
import com.raymondHariyono.playcut.presentation.screens.reservation.YourReservationPage

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onBoarding" // Memulai dari halaman OnBoarding
    ) {
        composable("onBoarding") {
            OnBoardingPage(navController = navController)
        }
        composable("login") {
            LoginPage(navController = navController)
        }
        composable("register") {
            RegisterPage(navController = navController)
        }
        composable("home") {
            HomePage(navController = navController)
        }
        // Ini adalah nama route untuk halaman daftar cabang
        composable("branch") {
            SearchBranchPage(navController = navController)
        }
        // Route untuk halaman detail, menerima argumen branchId
        composable(
            route = "DetailBranch/{branchId}",
            arguments = listOf(navArgument("branchId") { type = NavType.IntType })
        ) {
            DetailBranchPage(navController = navController)
        }

        composable(
            route = "booking/{barberId}",
            arguments = listOf(navArgument("barberId") { type = NavType.IntType })
        ) { backStackEntry ->
            val barberId = backStackEntry.arguments?.getInt("barberId") ?: -1
            BookingPage(navController = navController, barberId = barberId)
        }

        // Route untuk halaman reservasi Anda
        composable("yourReservation") {
            YourReservationPage(navController = navController)
        }
    }
}