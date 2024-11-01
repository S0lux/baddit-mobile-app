package com.example.baddit.presentation.components

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.screens.login.LoginViewModel
import com.example.baddit.ui.theme.CustomTheme.PrimaryContainter
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@Composable
fun CreatePostActionButton(onClick: () -> Unit) {
    FloatingActionButton(
        containerColor = MaterialTheme.colorScheme.mutedAppBlue,
        contentColor = MaterialTheme.colorScheme.textPrimary,
        onClick = onClick

    ) {
        Icon(
            painter = painterResource(id = R.drawable.round_add_24),
            contentDescription = null,
        )
    }

}
