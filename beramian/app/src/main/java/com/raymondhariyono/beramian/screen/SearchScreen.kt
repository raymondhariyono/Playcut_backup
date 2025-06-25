package com.raymondhariyono.beramian.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raymondhariyono.beramian.components.SearchPlaceCard
import com.raymondhariyono.beramian.components.TopBar
import com.raymondhariyono.beramian.data.kalselPlaces
import com.raymondhariyono.beramian.ui.theme.BeramianTheme



@Composable
fun SearchScreen() {
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlaces = if (searchQuery.isBlank()) {
        kalselPlaces
    } else {
        kalselPlaces.filter { place ->
            place.name.contains(searchQuery, ignoreCase = true) ||
                    place.location.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopBar(
                title = "Cari",
                onBack = { /*  */ },
                rightButtonText = "Batal",
                onRightButtonClick = { /* TODO: aksi tombol kanan */ }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari Destinasi") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Ikon Cari")
                },
                trailingIcon = {
                    IconButton(onClick = { /* Tidak melakukan apa-apa */ }) {
                        Icon(Icons.Default.Mic, contentDescription = "Ikon Mikrofon")
                    }
                },
                shape = RoundedCornerShape(24.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.LightGray,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Cari Tempat",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Button(
                    onClick = { /* Tidak melakukan apa-apa */ },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text("Filter", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredPlaces.isEmpty() && searchQuery.isNotBlank()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Destinasi tidak ditemukan",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredPlaces, key = { place -> place.name + place.location }) { place ->
                        SearchPlaceCard(
                            place = place,
                            modifier = Modifier.clickable { /* Tidak melakukan apa-apa */ }
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun SearchScreenPreview() {
    BeramianTheme {
        SearchScreen()
    }
}