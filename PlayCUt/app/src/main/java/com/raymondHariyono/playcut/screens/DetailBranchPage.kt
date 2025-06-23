package com.raymondHariyono.playcut.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.components.BranchBarberList
import com.raymondHariyono.playcut.components.ButtonNavBar
import com.raymondHariyono.playcut.data.barberShopData

@Composable
fun DetailBranchPage(navController: NavController, branchId: Int) {
    val branch = barberShopData.find { it.id == branchId } ?: return
    val pagerState = rememberPagerState(pageCount = { branch.barbers.size })

    val mainServices = listOf(
        "Haircut" to "Rp 50.000",
        "Haircut + Keramas" to "Rp 65.000",
        "Haircut + Keramas + Treatment" to "Rp 90.000"
    )

    val otherServices = listOf(
        "Coloring" to "Rp 150.000",
        "Shave" to "Rp 30.000",
        "Hair Spa" to "Rp 100.000",
        "Perming" to "Rp 180.000",
        "Smoothing" to "Rp 200.000",
        "Braids" to "Rp 120.000"
    )

    Scaffold(
        bottomBar = { ButtonNavBar(navController = navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = branch.imageRes),
                contentDescription = stringResource(R.string.branch_image_desc),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.barber_di_cabang_ini),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(8.dp))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val barber = branch.barbers[page]
                BranchBarberList(
                    navController = navController,
                    onBookNowClick = {
                        navController.navigate("booking/${barber.id}")
                    },
                    barber = barber
                )
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                repeat(branch.barbers.size) { index ->
                    val color = if (index == pagerState.currentPage) Color.Blue else Color.Gray
                    Spacer(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.paket_harga),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.reguler),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    mainServices.forEach { (service, price) ->
                        ServiceCard(title = service, price = price)
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.other_service),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    otherServices.forEach { (service, price) ->
                        ServiceCard(title = service, price = price)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ServiceCard(title: String, price: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary
            )
            Text(
                text = price,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
