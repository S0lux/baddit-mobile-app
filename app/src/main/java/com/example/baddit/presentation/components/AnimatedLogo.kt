package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieDynamicProperties
import com.airbnb.lottie.compose.LottieDynamicProperty
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.model.KeyPath

@Composable
fun AnimatedLogo(
    icon: Int,
    size: Dp = 300.dp,
    iteration: Int = 1,
    tintColor: Color? = null
) {
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(icon))

    LottieAnimation(
        composition = composition,
        modifier = Modifier.size(size),
        iterations = iteration,
        dynamicProperties = tintColor?.let { color ->
            LottieDynamicProperties(
                listOf(
                    LottieDynamicProperty(
                        property = LottieProperty.COLOR_FILTER,
                        value = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
                            color.hashCode(),
                            BlendModeCompat.SRC_ATOP
                        ),
                        keyPath = KeyPath("**")
                ))
            )
        }
    )
}