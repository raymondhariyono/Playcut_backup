package com.raymondHariyono.playcut.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.AddBarberPage
import com.raymondHariyono.playcut.presentation.screens.admin.AdminDashboardPage
import com.raymondHariyono.playcut.presentation.screens.admin.barberManagement.schedule.ManageBarbersPage
import com.raymondHariyono.playcut.presentation.screens.auth.login.LoginPage
import com.raymondHariyono.playcut.presentation.screens.booking.BookingPage
import com.raymondHariyono.playcut.presentation.screens.branch.detail.DetailBranchPage
import com.raymondHariyono.playcut.presentation.screens.branch.search.SearchBranchPage
import com.raymondHariyono.playcut.presentation.screens.home.HomePage
import com.raymondHariyono.playcut.presentation.screens.map.MapPage
import com.raymondHariyono.playcut.presentation.screens.onboarding.OnBoardingPage
import com.raymondHariyono.playcut.presentation.screens.profile.ProfilePage
import com.raymondHariyono.playcut.presentation.screens.reservation.YourReservationPage
import com.raymondHariyono.playcut.presentation.screens.splash.SPLASH_ROUTE
import com.raymondHariyono.playcut.presentation.screens.splash.SplashScreen
import com.raymondHariyono.playcut.presentation.screens.user.RegisterPage

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = SPLASH_ROUTE // Halaman pertama selalu splash
    ) {
        composable(SPLASH_ROUTE) {
            SplashScreen(navController = navController)
        }
        composable("onBoarding") {
            OnBoardingPage(navController = navController)
        }
        composable("login") {
            LoginPage(navController = navController, viewModel = hiltViewModel())
        }
        composable("register") {
            RegisterPage(navController = navController, viewModel = hiltViewModel())
        }
        composable("home") {
            HomePage(navController = navController, viewModel = hiltViewModel())
        }
        composable("profile") {
            ProfilePage(navController = navController, viewModel = hiltViewModel())
        }
        composable("branch") {
            SearchBranchPage(navController = navController, viewModel = hiltViewModel())
        }
        composable("yourReservation") {
            YourReservationPage(navController = navController, viewModel = hiltViewModel())
        }
        composable("adminDashboard") {
            AdminDashboardPage(navController = navController, viewModel = hiltViewModel())
        }
        composable("addBarber/{branchId}", arguments = listOf(navArgument("branchId") { type = NavType.IntType })) {
            AddBarberPage(navController = navController, viewModel = hiltViewModel())
        }

        composable(
            route = "DetailBranch/{branchId}",
            arguments = listOf(navArgument("branchId") { type = NavType.IntType })
        ) {
            DetailBranchPage(navController = navController, viewModel = hiltViewModel())
        }

        composable(
            route = "booking?barberId={barberId}&reservationId={reservationId}",
            arguments = listOf(
                navArgument("barberId") {
                    type = NavType.IntType
                    defaultValue = -1
                },
                navArgument("reservationId") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) {
            BookingPage(navController = navController, viewModel = hiltViewModel())
        }

        composable(
            "manageBarbers/{branchId}",
            arguments = listOf(navArgument("branchId") { type = NavType.IntType })
        ) {
            ManageBarbersPage(navController = navController, viewModel = hiltViewModel())
        }

        composable("map") {
            MapPage(navController = navController)
        }
    }
}