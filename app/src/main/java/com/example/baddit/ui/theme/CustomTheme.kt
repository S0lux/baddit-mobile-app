package com.example.baddit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


object CustomTheme {
    val ColorScheme.textPrimary: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFFFFFFFF) else Color(0xFF000000)

    val ColorScheme.textSecondary: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFF828282) else Color(0xFF828282)

    val ColorScheme.cardBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xff1c1c1c) else Color(0xffffffff)

    val ColorScheme.cardForeground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFF222222) else Color(0xFFF2F4F5)
}