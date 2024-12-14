package com.example.baddit.presentation.screens.comment

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.baddit.R
import com.example.baddit.presentation.components.AnimatedLogo
import com.example.baddit.presentation.components.BaseTopNavigationBar
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.styles.textFieldColorsNoBorder
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary

@Composable
fun CommentScreen(
    navController: NavHostController,
    viewModel: CommentViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    var loadingIcon by remember { mutableIntStateOf(0) }
    loadingIcon =
        if (viewModel.arguments.darkMode) R.raw.loadingiconwhite else R.raw.loadingicon

    if (viewModel.error.isNotEmpty() && viewModel.error != "Success") {
        Toast.makeText(context, viewModel.error, Toast.LENGTH_LONG).show()
    }

    LaunchedEffect(viewModel.success) {
        if (viewModel.success) {
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.cardBackground)
    ) {
        BaseTopNavigationBar(
            title = "Comment",
            leftIcon = R.drawable.round_arrow_back_24,
            onLeftIconClick = { navController.popBackStack() },
            rightIcons = listOf(
                Pair(R.drawable.round_send_24) { viewModel.onSend() }
            ),
            rightIconsLoading = viewModel.isLoading,
            loadingIcon = {
                AnimatedLogo(icon = loadingIcon, iteration = 999, size = 45.dp)
            }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (viewModel.arguments.commentContent != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                TextField(
                    value = viewModel.arguments.commentContent,
                    onValueChange = { },
                    readOnly = true,
                    modifier = Modifier.fillMaxSize(),
                    singleLine = false,
                    minLines = 10,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = textFieldColors()
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.textSecondary)
        }

        TextField(
            value = viewModel.userInput,
            onValueChange = { viewModel.onUserInput(it) },
            modifier = Modifier.fillMaxSize(),
            singleLine = false,
            isError = viewModel.error.isNotEmpty(),
            minLines = 10,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = "Type a comment",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = {
                Text(text = viewModel.error)
            },
            colors = textFieldColorsNoBorder()
        )
    }
}