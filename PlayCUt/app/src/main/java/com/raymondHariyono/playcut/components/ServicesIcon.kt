package com.raymondHariyono.playcut.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R

@Composable
fun ServiceIcons(navController: NavController) {
    val services = listOf(
        "Haircut" to  R.drawable.haircut,
        "Coloring" to  R.drawable.hair_coloring,
        "Shaving" to  R.drawable.hair_shave,
        "Spa" to  R.drawable.hair_spa,
        "Coloring" to  R.drawable.hair_coloring,
        "perming" to  R.drawable.hair_perming,
        "Braids" to  R.drawable.hair_braids,
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            items(services) { (label, drawableId) ->
                Column(
                    modifier = Modifier.padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = drawableId),
                        contentDescription = "icon_image",
                        modifier = Modifier.size(40.dp)
                    )
                    Text(text = label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp)
                }
            }
        }
    }
}
