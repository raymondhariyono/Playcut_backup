package com.raymondHariyono.playcut.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class ButtonNav(
    val route: String,
    val icon: ImageVector
){
    object Home : ButtonNav("home",  Icons.Filled.Home)
    object Search : ButtonNav("branch", Icons.Filled.Search)
    object Profile : ButtonNav("profile",  Icons.Filled.Person)
}