package com.example.baddit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


object CustomTheme {
    val ColorScheme.cardBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xfff2f4f5) else Color(0xffffffff)

    val ColorScheme.cardHeaderText: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFF9E9E9E) else Color(0xFF9E9E9E)

    val ColorScheme.cardTitleText: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF000000)

    val ColorScheme.cardForeground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFFF2F4F5)
}