package com.raymondhariyono.beramian.components

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.raymondhariyono.beramian.R
import com.raymondhariyono.beramian.data.Place
import com.raymondhariyono.beramian.data.kalselPlaces

@SuppressLint("DefaultLocale")
@Composable
fun SearchPlaceCard(place: Place, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.width(IntrinsicSize.Max),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Image(
                painter = painterResource(id = place.imageResId),
                contentDescription = "Gambar ${place.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = place.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = place.location,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.heightIn(min = 16.dp)
                ) {
                    place.rating?.let { ratingValue ->
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", ratingValue).replace(",", "."),
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        place.reviewCount?.let { reviewCountValue ->
                            Text(
                                text = " ($reviewCountValue)",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    } ?: run {
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchPlaceCard() {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SearchPlaceCard(place = kalselPlaces.first { it.name == "Pasar Terapung Lok Baintan" })
            SearchPlaceCard(place = kalselPlaces.first { it.name == "Goa Batu Hapu" }) // Contoh dengan rating null
            SearchPlaceCard(
                place = Place(
                    name = "Contoh Tempat Kustom",
                    location = "Contoh Lokasi Kustom",
                    imageResId = R.drawable.kalsel,
                    rating = 3.9f,
                    reviewCount = 77
                )
            )
        }
    }
