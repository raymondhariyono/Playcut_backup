// File: app/src/main/java/com/raymondHariyono/playcut/presentation/screens/home/HomePage.kt
package com.raymondHariyono.playcut.presentation.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.domain.model.Inspiration
import com.raymondHariyono.playcut.presentation.components.ButtonNavBar
import com.raymondHariyono.playcut.presentation.components.CarouselImage
import com.raymondHariyono.playcut.presentation.components.HairCutReferences
import com.raymondHariyono.playcut.presentation.components.TopBar
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomePage(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    userName = uiState.userName,
                    onReservationClick = { navController.navigate("yourReservation") }
                )
            },
            bottomBar = { ButtonNavBar(navController) },
            containerColor = Color.Transparent
        ) { paddingValues ->
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(bottom = 80.dp)
                ) {
                    // 1. KARTU UTAMA (MAIN ACTION CARD)
                    MainActionCard(
                        onClick = { navController.navigate("branch") }
                    )

                    // 2. AKSES CEPAT (QUICK ACCESS GRID)
                    QuickAccessGrid(navController = navController)

                    // 3. PROMO CAROUSEL
                    SectionTitle(title = "Promo & Penawaran Spesial")
                    CarouselImage(
                        navController = navController,
                        imageList = uiState.promotions.map { it.imageRes }
                    )

                    // 4. INSPIRASI GAYA (MENGGUNAKAN KOMPONEN LAMA)
                    SectionTitle(title = "Inspirasi Gaya Rambut")
                    HairCutReferences(inspirations = uiState.inspirations)

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

// ================================================================
// KOMPONEN-KOMPONEN BARU UNTUK DESAIN MODERN
// ================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActionCard(onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    // Latar belakang gradien menggunakan warna tema
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Cari & Pesan Sekarang",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary, // Warna teks yang kontras
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Temukan cabang terdekat dan barber favoritmu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f)
                )
            }
            Icon(
                imageVector = Icons.Outlined.ArrowForward,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd).size(32.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun QuickAccessGrid(navController: NavController) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            QuickAccessItem(
                title = "Cari Cabang",
                icon = Icons.Outlined.Search,
                onClick = { navController.navigate("branch") }
            )
            QuickAccessItem(
                title = "Reservasi",
                icon = Icons.Outlined.DateRange,
                onClick = { navController.navigate("yourReservation") }
            )
            QuickAccessItem(
                title = "Profile",
                icon = Icons.Outlined.Person,
                onClick = { /* TODO: Navigasi ke halaman bantuan */ }
            )
        }
    }
}

@Composable
fun QuickAccessItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    )
}