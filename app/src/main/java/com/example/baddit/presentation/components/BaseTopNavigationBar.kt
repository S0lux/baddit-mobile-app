package com.example.baddit.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@Composable
fun BaseTopNavigationBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.cardBackground,
    leftIcon: Int? = null,
    onLeftIconClick: () -> Unit = {},
    rightIcons: List<Pair<Int, () -> Unit>> = emptyList(),
    contentColor: Color = MaterialTheme.colorScheme.textPrimary,
    rightIconsLoading: Boolean = false,
    loadingIcon: @Composable () -> Unit = {}
) {
    Row(
        modifier = modifier
            .background(backgroundColor)
            .padding(
                bottom = 10.dp,
                top = WindowInsets.safeContent.asPaddingValues().calculateTopPadding().plus(5.dp)
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Icon and Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            leftIcon?.let {
                IconButton(
                    onClick = onLeftIconClick,
                    modifier = Modifier,
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = contentColor,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        painterResource(it),
                        contentDescription = null
                    )
                }
            }

            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Right Icons or Loading
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (rightIconsLoading) {
                loadingIcon()
            } else {
                rightIcons.forEach { (icon, onClick) ->
                    IconButton(
                        onClick = onClick,
                        modifier = Modifier,
                        colors = IconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = contentColor,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Icon(
                            painterResource(icon),
                            contentDescription = null
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BasicBarWithTitle() {
    BaseTopNavigationBar(
        title = "Profile",
        leftIcon = R.drawable.baseline_arrow_back_24,
        onLeftIconClick = {  }
    )
}

@Preview(showBackground = true)
@Composable
private fun BasicBarWithLoading() {
    BaseTopNavigationBar(
        title = "Comment",
        leftIcon = R.drawable.round_arrow_back_24,
        onLeftIconClick = {  },
        rightIcons = listOf(
            Pair(R.drawable.round_send_24) {  }
        ),
        rightIconsLoading = true,
        loadingIcon = {
            // Your custom loading icon
            AnimatedLogo(icon = R.raw.loadingicon, iteration = 999, size = 45.dp)
        }
    )
}