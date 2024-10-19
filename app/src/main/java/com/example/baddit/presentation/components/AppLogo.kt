package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.baddit.R

@Composable
fun AppLogo(isDarkTheme: Boolean) {
    Icon(
        painter = painterResource(id = R.drawable.baddit_white),
        contentDescription = null,
        modifier = Modifier.size(200.dp),
        tint = if (isDarkTheme) Color.White else Color.Black
    )
}