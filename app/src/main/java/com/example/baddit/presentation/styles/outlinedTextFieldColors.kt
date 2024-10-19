package com.example.baddit.presentation.styles

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import com.example.baddit.ui.theme.CustomTheme.errorRed
import com.example.baddit.ui.theme.CustomTheme.neutralGray

@Composable
fun textFieldColors(): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.neutralGray,
        focusedLabelColor = MaterialTheme.colorScheme.neutralGray,
        cursorColor = MaterialTheme.colorScheme.neutralGray,
        focusedLeadingIconColor = MaterialTheme.colorScheme.neutralGray,
        errorBorderColor = MaterialTheme.colorScheme.errorRed,
        errorSupportingTextColor = MaterialTheme.colorScheme.errorRed,
        errorCursorColor = MaterialTheme.colorScheme.errorRed,
        errorLabelColor = MaterialTheme.colorScheme.errorRed,
        errorLeadingIconColor = MaterialTheme.colorScheme.errorRed
    )
}