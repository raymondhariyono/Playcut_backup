// File: app/src/main/java/com/raymondHariyono/playcut/presentation/components/ServiceIcons.kt
package com.raymondHariyono.playcut.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.domain.model.HomeService

@Composable
fun ServiceIcons(
    services: List<HomeService>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 8.dp),
            modifier = Modifier.height(190.dp),
            userScrollEnabled = false
        ) {
            items(services) { service ->
                Column(
                    modifier = Modifier.padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    val iconResId = when (service.iconName) {
                        "ic_haircut" -> R.drawable.haircut
                        "ic_coloring" -> R.drawable.hair_coloring
                        "ic_shave" -> R.drawable.hair_shave
                        "ic_spa" -> R.drawable.hair_spa
                        "ic_perming" -> R.drawable.hair_perming
                        "ic_braids" -> R.drawable.hair_braids
                        else -> R.drawable.placeholder_branch
                    }

                    Image(
                        painter = painterResource(id = iconResId),
                        contentDescription = service.label,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = service.label,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}