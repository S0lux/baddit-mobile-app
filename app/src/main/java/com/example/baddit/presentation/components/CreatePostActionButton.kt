package com.example.baddit.presentation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.PrimaryContainter
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@Composable
fun CreatePostActionButton(onClick:()->Unit){
    SmallFloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.PrimaryContainter,
        contentColor = MaterialTheme.colorScheme.textPrimary,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.round_add_24),
            contentDescription = null,
        )
    }

}