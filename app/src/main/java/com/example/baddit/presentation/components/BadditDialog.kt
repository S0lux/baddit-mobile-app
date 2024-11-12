package com.example.baddit.presentation.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.neutralGray

@Composable
fun BadditDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    confirmColor: Color = MaterialTheme.colorScheme.mutedAppBlue,
    dismissColor: Color = MaterialTheme.colorScheme.neutralGray,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        title = { Text(title) },
        text = { Text(text) },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(confirmText, color = confirmColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(dismissText, color = dismissColor)
            }
        },
        shape = RoundedCornerShape(10.dp),
        containerColor = MaterialTheme.colorScheme.background
    )
}