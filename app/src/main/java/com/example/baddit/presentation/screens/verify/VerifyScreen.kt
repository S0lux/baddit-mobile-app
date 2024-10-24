package com.example.baddit.presentation.screens.verify

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.components.AnimatedLogo
import com.example.baddit.ui.theme.BadditTheme
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary

@Composable
fun VerifyScreen(
    navigateLogin: () -> Unit,
    navigateHome: () -> Unit,
    authToken: String? = null,
    viewModel: VerifyViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.verifyEmail(authToken)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        when (viewModel.displayLogoState) {
            "Loading" -> AnimatedLogo(icon = R.raw.loading, iteration = 999)
            "Error" -> AnimatedLogo(icon = R.raw.error)
            "Success" -> AnimatedLogo(icon = R.raw.congrats)
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    viewModel.title,
                    color = MaterialTheme.colorScheme.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )

                Text(
                    viewModel.description,
                    color = MaterialTheme.colorScheme.textSecondary
                )

                Spacer(modifier = Modifier.size(5.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Button(
                onClick = {
                    if (viewModel.isVerifySuccess) navigateLogin() else navigateHome()
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.mutedAppBlue
                )
            ) {
                Text(
                    if (viewModel.isVerifySuccess) "Login" else "Home",
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }
        }
    }
}

@Preview
@Composable
fun VerifyPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            VerifyScreen(navigateLogin = { }, navigateHome = {  })
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun VerifyDarkPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        )
        {
            VerifyScreen(navigateLogin = { }, navigateHome = {  })
        }
    }
}