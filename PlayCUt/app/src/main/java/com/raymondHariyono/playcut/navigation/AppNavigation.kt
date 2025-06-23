package com.raymondHariyono.playcut.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.raymondHariyono.playcut.data.OnBoarding
import com.raymondHariyono.playcut.data.barberShopData
import com.raymondHariyono.playcut.screens.*

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onBoarding"
    ) {
        composable("onBoarding")
        {
            OnBoardingPage(navController)
        }

        composable("login")
        {
            LoginPage(navController)
        }

        composable("register")
        {
            RegisterPage(navController)
        }

        composable("home")
        {
            HomePage(navController)
        }

        composable("profile")
        {
            ProfilePage(navController)
        }

        composable("branch")
        {
            SearchBranchPage(navController)
        }


        composable(
            route = "DetailBranch/{branchId}",
            arguments = listOf(navArgument("branchId") { type = NavType.IntType })
        ) { backStackEntry ->
            val branchId = backStackEntry.arguments?.getInt("branchId")
            val branch = barberShopData.find { it.id == branchId }
            branch?.let {
                DetailBranchPage(navController = navController, branchId = it.id)
            }
        }
        composable("booking/{barberId}",
            arguments = listOf(navArgument("barberId") { type = NavType.IntType })
        ) { backStackEntry ->
            val barberId = backStackEntry.arguments?.getInt("barberId") ?: return@composable
            val barber = barberShopData
                .flatMap { it.barbers }
                .find { it.id == barberId } ?: return@composable

            BookingPage(
                navController = navController,
                barber = barber,
                onDismiss = { navController.popBackStack() }
            )
        }
        composable("yourReservation") { YourReserationPage(navController) }
    }
}

