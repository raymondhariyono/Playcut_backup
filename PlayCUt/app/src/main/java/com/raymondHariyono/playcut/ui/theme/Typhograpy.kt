package com.raymondHariyono.playcut.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.raymondHariyono.playcut.R

val Poppins = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Medium)
)

val Inter = FontFamily(
    Font(R.font.inter_regular, FontWeight.Normal)
)

val Playfair = FontFamily(
    Font(R.font.playfair_display_italic, FontWeight.Normal)
)

val AppTypography = Typography(
    displayLarge = TextStyle( // Logo/App Name
        fontFamily = Playfair,
        fontWeight = FontWeight.Bold,
    ),
    headlineMedium = TextStyle( // Page Title
        fontFamily = Poppins,
        fontWeight = FontWeight.Bold
    ),
    titleMedium = TextStyle( // Section Title
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold
    ),
    bodyLarge = TextStyle( // Body text
        fontFamily = Inter,
        fontWeight = FontWeight.Normal
    ),
    labelLarge = TextStyle( // Button Text
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold
    )
)
