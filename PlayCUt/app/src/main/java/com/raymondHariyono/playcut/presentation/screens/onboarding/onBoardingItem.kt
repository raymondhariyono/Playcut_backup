package com.raymondHariyono.playcut.presentation.screens.onboarding

import com.raymondHariyono.playcut.R

sealed class OnBoarding(
    val imageOnBoarding: Int,
    val title: String,
    val desc: String,
) {
    data object FirstPage : OnBoarding(
        imageOnBoarding = R.drawable.onboarding1,
        title = "Welcome to PlayCut",
        desc = "Find and book barbershop easily."
    )

    data object SecondPage : OnBoarding(
        imageOnBoarding = R.drawable.onboarding2,
        title = "Book with Your Favorite Barber",
        desc = "Schedule your haircut without hassle."
    )

    data object ThirdPage : OnBoarding(
        imageOnBoarding = R.drawable.onboarding3,
        title = "Let’s Get Started!",
        desc = "Enjoy PlayCut with smooth experience."
    )
}

