package com.raymondHariyono.playcut

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.raymondHariyono.playcut.navigation.AppNavigator
import com.raymondHariyono.playcut.ui.theme.PlayCUtTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlayCUtTheme {
                SplashScreen()
            }
        }
    }
}

@Composable
fun SplashScreen()  {
    var isSplashFinished by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2000)
        isSplashFinished = true
    }

    if (isSplashFinished) {
        AppNavigator()
    } else {
        SplashContent()
    }
}

@Composable
fun SplashContent() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.barbershop_logo))
    val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.primary)
            .fillMaxSize(),
        contentAlignment = Alignment.Center,

    ) {
        LottieAnimation(composition = composition, progress = { progress })
    }
}
