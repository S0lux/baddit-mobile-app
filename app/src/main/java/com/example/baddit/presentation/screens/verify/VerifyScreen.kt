package com.example.baddit.presentation.screens.verify

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun VerifyScreen(navigateLogin: () -> Unit, authToken: String? = null) {
    if (authToken is String) {
        Text(authToken)
    }
}