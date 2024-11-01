package com.example.baddit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.baddit.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.baddit.ui.theme.CustomTheme.textPrimary


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileItem(icon: Painter, text: String, onClick: () -> Unit){
    Row(
        modifier = Modifier.clickable(
            onClick = onClick
        )
            .fillMaxWidth()
            .defaultMinSize(Dp.Unspecified, 40.dp)
            .height(50.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(modifier = Modifier.size(24.dp),tint = MaterialTheme.colorScheme.textPrimary, painter = icon, contentDescription = "")
            Spacer(modifier = Modifier.fillMaxWidth(0.02f))
            Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.textPrimary)
        }

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "",tint = MaterialTheme.colorScheme.textPrimary
        )
    }
}

@Preview
@Composable
fun ProfileItemPreview() {
    ProfileItem(painterResource(id = R.drawable.comment), onClick = {}, text = "Comments")
}
