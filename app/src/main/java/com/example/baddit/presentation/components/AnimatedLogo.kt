package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun AnimatedLogo(icon: Int, size: Dp = 300.dp, iteration: Int = 1) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(icon))

    LottieAnimation(
        composition = composition,
        modifier = Modifier.size(size),
        iterations = iteration
    )
}