package com.example.baddit.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.errorRed

@Composable
fun Alert(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int = R.drawable.error,
    color: Color = MaterialTheme.colorScheme.errorRed,
    content: @Composable () -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = modifier) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = color
        )
        content()
    }
}