package com.raymondHariyono.playcut.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.components.*
import com.raymondHariyono.playcut.ui.theme.*

@Composable
fun HomePage(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    navController,
                    onReservationClick = {
                        navController.navigate("yourReservation")
                    }
                )
            },
            containerColor = Color.Transparent,
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 100.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CarouselImage(navController)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .wrapContentHeight(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = stringResource(R.string.services),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .padding(start = 18.dp)
                    )
                    ServiceIcons(navController)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    HairCutReferences(navController)
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 4.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            ButtonNavBar(navController)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewHomePage() {
    PlayCUtTheme {
        HomePage(navController = rememberNavController())
    }
}
