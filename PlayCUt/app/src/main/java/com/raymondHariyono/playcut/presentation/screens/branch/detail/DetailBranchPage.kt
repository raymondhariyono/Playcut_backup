package com.raymondHariyono.playcut.presentation.screens.branch.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.presentation.components.BranchBarberList
import com.raymondHariyono.playcut.presentation.components.ButtonNavBar

@Composable
fun DetailBranchPage(
    navController: NavController,
    viewModel: DetailBranchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = { ButtonNavBar(navController = navController) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.error != null -> Text(text = "Error: ${uiState.error}")
                uiState.branch != null -> {
                    val branch = uiState.branch!!
                    val pagerState = rememberPagerState(pageCount = { branch.barbers.size })

                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            val imageResId = when (branch.imageRes) {
                                "placeholder_branch" -> R.drawable.placeholder_branch
                                else -> R.drawable.placeholder_branch
                            }
                            Image(
                                painter = painterResource(id = imageResId),
                                contentDescription = branch.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(220.dp).padding(16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                            )
                            Text(
                                branch.name,
                                style = MaterialTheme.typography.headlineMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item {
                            SectionTitle("Barber di Cabang Ini")
                            HorizontalPager(
                                state = pagerState,
                                modifier = Modifier.fillMaxWidth()
                            ) { page ->
                                BranchBarberList(
                                    navController = navController,
                                    onBookNowClick = {
                                        val barberId = branch.barbers[page].id
                                        navController.navigate("booking?barberId=$barberId")
                                    },
                                    barber = branch.barbers[page]
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        item { SectionTitle("Layanan & Harga") }
                        items(uiState.services) { service -> ServiceItem(service = service) }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun ServiceItem(service: com.raymondHariyono.playcut.domain.model.Service) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = service.name, style = MaterialTheme.typography.bodyLarge)
        Text(text = service.price, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
}