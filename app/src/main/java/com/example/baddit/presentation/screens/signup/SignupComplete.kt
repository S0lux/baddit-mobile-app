package com.example.baddit.presentation.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.launch

@Composable
fun SignUpComplete() {
    val scrollState = rememberScrollState()
    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.email_send))
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(
            property = LottieProperty.COLOR_FILTER,
            value = SimpleColorFilter(Color.White.toArgb()),
            keyPath = arrayOf("**")
        ),
    )

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            dynamicProperties = dynamicProperties,
            modifier = Modifier.size(300.dp),
            iterations = 999,
            speed = 0.25f
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    "Email verification",
                    color = MaterialTheme.colorScheme.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Text(
                    "We have sent you an email containing instructions on how to verify your account.",
                    color = MaterialTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.size(5.dp))

                Text(
                    "* Email will expire in 24 hours.",
                    color = MaterialTheme.colorScheme.textSecondary,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(50.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = {
                    TODO("Go to login page")
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.mutedAppBlue
                )
            ) {
                Text("Login", color = MaterialTheme.colorScheme.textPrimary)
            }
        }
    }
}