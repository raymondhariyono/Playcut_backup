package com.raymondHariyono.playcut.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto // <-- 1. Import model yang benar

/**
 * Composable ini menampilkan judul seksi, tombol "Lihat Lainnya",
 * dan galeri gambar horizontal yang bisa digeser.
 */
@Composable
fun InspirationSection(
    photos: List<UnsplashPhoto>, // <-- 2. Terima List<UnsplashPhoto>
    onViewMoreClick: () -> Unit
) {
    Column(modifier = Modifier.padding(top = 16.dp)) {
        // Baris untuk Judul dan Tombol "Lihat Lainnya"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Inspirasi Gaya Rambut",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            TextButton(onClick = onViewMoreClick) {
                Text("Lihat Lainnya")
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Galeri Gambar Horizontal yang Bisa Digeser
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 3. Loop melalui List<UnsplashPhoto>
            items(photos, key = { it.id }) { photo ->
                InspirationItem(photo = photo)
            }
        }
    }
}

/**
 * Composable untuk menampilkan satu item gambar inspirasi.
 */
@Composable
private fun InspirationItem(photo: UnsplashPhoto) { // <-- 4. Terima UnsplashPhoto
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        AsyncImage(
            // 5. Sesuaikan dengan struktur UnsplashPhoto
            model = photo.urls.regular,
            contentDescription = "Foto oleh ${photo.user.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}