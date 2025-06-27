package com.raymondHariyono.playcut.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.raymondHariyono.playcut.R
import kotlinx.coroutines.delay

@Composable
fun CarouselImage(
    navController: NavController,
    imageList: List<String>
) {

    if (imageList.isEmpty()) {
        return
    }

    val pagerState = rememberPagerState(pageCount = { imageList.size })

    LaunchedEffect(pagerState.currentPage) {
        delay(3000)
        val nextPage = (pagerState.currentPage + 1) % imageList.size
        pagerState.animateScrollToPage(nextPage)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { currentPage ->
            Card(
                modifier = Modifier.fillMaxSize(),
                elevation = CardDefaults.cardElevation(8.dp),
            ) {
                val imagePainter = when(imageList[currentPage]) {
                    "placeholder_branch" -> painterResource(id = R.drawable.placeholder_branch)
                    else -> painterResource(id = R.drawable.placeholder_branch)
                }

                Image(
                    painter = imagePainter,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    contentDescription = "Promotion Image"
                )
            }
        }
    }
}