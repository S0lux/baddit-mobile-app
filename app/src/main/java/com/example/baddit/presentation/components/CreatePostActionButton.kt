package com.example.baddit.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@Composable
fun BadditActionButton(onClick: () -> Unit, @DrawableRes icon: Int = R.drawable.round_add_24) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.mutedAppBlue,
        contentColor = MaterialTheme.colorScheme.textPrimary,
        onClick = onClick

    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
    }
}
