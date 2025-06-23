package com.raymondHariyono.playcut.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun CarouselImage(navController: NavController) {
    val imgCarousel = listOf(
        R.drawable.placeholder_barber,
        R.drawable.barber1,
        R.drawable.barber2
    )
    val pagerState = rememberPagerState(pageCount = { imgCarousel.size })
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    LaunchedEffect(Unit) {
        while(true){
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % imgCarousel.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            verticalAlignment = Alignment.Top,
            modifier = if (isPortrait) {
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(26.dp)
            }else {
                Modifier
                    .width(400.dp)
                    .height(250.dp)
            }
        ) { currentPage ->
            Card(
                modifier = Modifier
                        .fillMaxSize(),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                Image(
                    painter = painterResource(id = imgCarousel[currentPage]),
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
                    contentDescription = ""
                )
            }
        }
        IconButton(
            onClick = {
                val nextPage = pagerState.currentPage + 1
                if (nextPage < imgCarousel.size){
                    scope.launch {
                        pagerState.animateScrollToPage(nextPage)
                    }
                }
            },
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.CenterStart)
                .clip(CircleShape)
                .size(40.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )

        }

        IconButton(
            onClick = {
                val prevPage = pagerState.currentPage - 1
                if (prevPage >= 0){
                    scope.launch {
                        pagerState.animateScrollToPage(prevPage)
                    }
                }
            },
            modifier = Modifier
                .padding(30.dp)
                .align(Alignment.CenterEnd)
                .clip(CircleShape)
                .size(60.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary
            )

        }
    }
}
