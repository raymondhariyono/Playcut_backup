package com.raymondHariyono.playcut.presentation.screens.inspiration

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.raymondHariyono.playcut.domain.model.UnsplashPhoto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InspirationPage(
    viewModel: InspirationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Inspirasi Gaya Rambut") }) }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = "Gagal memuat: ${uiState.error}",
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 2. Gunakan uiState.photos yang sekarang bertipe List<UnsplashPhoto>
                        items(uiState.photos, key = { it.id }) { photo ->
                            InspirationPhotoCard(photo = photo)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InspirationPhotoCard(photo: UnsplashPhoto) { // <-- 3. Terima UnsplashPhoto
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            // 4. Sesuaikan dengan struktur UnsplashPhoto
            model = photo.urls.regular,
            contentDescription = "Foto oleh ${photo.user.name}",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(12.dp))
        )
    }
}