package com.example.listxml

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.listxml.data.Films


@Composable
fun FilmListItems(
    films: Films,
    navController: NavController,
    viewModel: FilmViewModel
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(modifier = Modifier.padding(8.dp)) {
            FilmImage(films = films)
            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Text(
                    text = films.title,
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )
                Text(
                    text = "(${films.year})",
                    style = TextStyle(fontSize = 10.sp, color = Color.Gray)
                )

                Text(
                    text = films.description,
                    style = TextStyle(fontSize = 10.sp),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 30.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Button(
                        onClick = {
                            viewModel.logImdbClick(films)
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(films.imdbUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("IMDB", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 8.sp))
                    }

                    Spacer(modifier = Modifier.width(5.dp))

                    Button(
                        onClick = {
                            viewModel.selectFilm(films)
                            viewModel.logDetailClick(films)
                            navController.navigate("detail/${films.title}")
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(16.dp),
                    ) {
                        Text("Detail", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 8.sp))
                    }
                }
            }
        }
    }
}


@Composable
fun FilmImage(films: Films){
    Image(
        painter = painterResource(id = films.image),
        contentDescription = null,
        modifier = Modifier
            .size(150.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop,
    )
}
