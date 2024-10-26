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

    val ColorScheme.appOrange: Color
        @Composable
        get() = Color(0xFFFF7315)

    val ColorScheme.mutedAppOrange: Color
        @Composable
        get() = Color(0xFFD67945)

    val ColorScheme.appBlue: Color
        @Composable
        get() = Color(0xFF0378FF)

    val ColorScheme.mutedAppBlue: Color
        @Composable
        get() = Color(0xFF4E8DFA)

    val ColorScheme.neutralGray: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFFB0B0B0) else Color(0xFF6E6E6E)

    val ColorScheme.errorRed: Color
        @Composable
        get() = Color(0xFFE57373)

    val ColorScheme.PrimaryContainter:Color
        @Composable
        get() = if(!isSystemInDarkTheme()) Color(80, 211, 215) else Color(40, 170, 175)

    val ColorScheme.scaffoldBackground: Color
        @Composable
        get() = if (isSystemInDarkTheme()) Color(0xFF181818) else Color(0xFFF0F0F0)

}