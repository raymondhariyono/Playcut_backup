package com.raymondHariyono.playcut.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.raymondHariyono.playcut.presentation.navigation.ButtonNav

@Composable
fun ButtonNavBar(navController: NavController) {
    val button = listOf(
        ButtonNav.Home,
        ButtonNav.Search,
        ButtonNav.Profile
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry.value?.destination?.route

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    BottomAppBar(
        modifier = Modifier
            .padding(10.dp)
            .clip(RoundedCornerShape(30.dp))
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                if (currentDestination != ButtonNav.Home.route) {
                    navController.navigate(ButtonNav.Home.route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }) {
                Icon(
                    imageVector = ButtonNav.Home.icon,
                    contentDescription = "Home",
                    tint = if (currentDestination == ButtonNav.Home.route)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                onClick = {
                    if (currentDestination != ButtonNav.Search.route) {
                        navController.navigate(ButtonNav.Search.route) {
                            popUpTo("home") { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                },
                shape = CircleShape,
                containerColor = if (currentDestination == ButtonNav.Search.route)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onPrimary,
                contentColor = if (currentDestination == ButtonNav.Search.route)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ) {
                Icon(Icons.Rounded.Search, contentDescription = "Search")
            }

            Spacer(modifier = Modifier.weight(1f))

            IconButton(onClick = {
                if (currentDestination != ButtonNav.Profile.route) {
                    navController.navigate(ButtonNav.Profile.route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }) {
                Icon(
                    imageVector = ButtonNav.Profile.icon,
                    contentDescription = "Profile",
                    tint = if (currentDestination == ButtonNav.Profile.route)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
