// File: app/src/main/java/com/raymondHariyono/playcut/presentation/components/HairCutReferences.kt
package com.raymondHariyono.playcut.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.raymondHariyono.playcut.R
import com.raymondHariyono.playcut.domain.model.Inspiration

@Composable
fun HairCutReferences(
    inspirations: List<Inspiration>
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(inspirations) { inspiration ->
            val imageResId = when (inspiration.imageName) {
                "haircut_references" -> R.drawable.haircut_references
                else -> R.drawable.placeholder_branch
            }
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Inspiration: ${inspiration.id}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}