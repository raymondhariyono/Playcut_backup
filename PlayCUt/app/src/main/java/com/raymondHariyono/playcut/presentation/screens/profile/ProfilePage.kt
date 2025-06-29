package com.raymondHariyono.playcut.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.raymondHariyono.playcut.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.UserProfile
import com.raymondHariyono.playcut.presentation.components.ButtonNavBar

@Composable
fun ProfilePage(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            navController.navigate("login") {
                popUpTo(navController.graph.id) { inclusive = true }
            }
        }
    }

    Scaffold(
        bottomBar = { ButtonNavBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(text = stringResource(R.string.error_message, uiState.error ?: ""))
                else -> {
                    uiState.userProfile?.let {
                        ProfileContent(
                            navController = navController,
                            profile = it,
                            onLogoutClick = viewModel::onLogoutClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(
    navController: NavController,
    profile: UserProfile,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        val defaultUserName = stringResource(R.string.unknown_user)
        when (profile) {
            is UserProfile.Admin -> ProfileHeader(profile.name, null)
            is UserProfile.Barber -> ProfileHeader(profile.name, profile.imageRes)
            is UserProfile.Customer -> ProfileHeader(profile.name, null)
            is UserProfile.Unknown -> ProfileHeader(defaultUserName, null)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol edit profil
        Button(
            onClick = { navController.navigate("editProfile") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.edit_profile))
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = stringResource(R.string.edit_icon_desc))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (profile) {
                    is UserProfile.Barber -> {
                        ProfileInfoRow(
                            icon = Icons.Outlined.Phone,
                            label = stringResource(R.string.contact_label),
                            value = profile.contact
                        )
                    }
                    is UserProfile.Customer -> {
                        ProfileInfoRow(
                            icon = Icons.Outlined.Phone,
                            label = stringResource(R.string.phone_label),
                            value = profile.phoneNumber
                        )
                    }
                    is UserProfile.Admin -> {
                        ProfileInfoRow(
                            icon = Icons.Outlined.Person,
                            label = stringResource(R.string.role_label),
                            value = stringResource(R.string.admin_role)
                        )
                    }
                    is UserProfile.Unknown -> {
                        Text(text = stringResource(R.string.unknown_profile_detail))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Tombol logout
        OutlinedButton(
            onClick = onLogoutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = stringResource(R.string.logout_icon_desc)
            )
            Spacer(modifier = Modifier.size(ButtonDefaults.IconSpacing))
            Text(text = stringResource(R.string.logout))
        }
    }
}

@Composable
fun ProfileHeader(name: String, imageUrl: String?) {
    AsyncImage(
        model = imageUrl,
        contentDescription = stringResource(R.string.profile_photo_desc),
        placeholder = painterResource(id = R.drawable.ic_default_profile),
        error = painterResource(id = R.drawable.ic_default_profile),
        modifier = Modifier.size(120.dp).clip(CircleShape),
        contentScale = ContentScale.Crop
    )
    Spacer(modifier = Modifier.height(16.dp))
    Text(
        text = name,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ProfileInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value.ifEmpty { "-" },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
