package com.example.baddit.presentation.screens.createPost

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.baddit.presentation.components.LoginDialog
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.styles.textFieldColorsNoBorder
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Main
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTextPostScreen(
    navController: NavHostController,
    isDarkTheme:Boolean,
    viewmodel: CreatePostViewodel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var loadingIcon by remember {
        mutableStateOf(0)
    }

    loadingIcon = if (isDarkTheme) R.raw.loadingiconwhite else R.raw.loadingicon

    if(!viewmodel.isLoggedIn.value){
        LoginDialog(navigateLogin = {
            navController.navigate(Auth)
        }, onDismiss = { navController.navigateUp() })
    }


    if (showBottomSheet) {
        SelectCommunityBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        )
    }
    if (viewmodel.error == "Success") {
        LaunchedEffect(key1 = "key") {
            navController.navigateUp()
        }

    }
    if (viewmodel.error.isNotEmpty() && viewmodel.error != "Success") {
        Toast.makeText(context, viewmodel.error, Toast.LENGTH_LONG).show()
    }
    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.cardBackground)
    ) {
        BaseTopNavigationBar(
            title = "Upload post",
            leftIcon = R.drawable.round_arrow_back_24,
            onLeftIconClick = { navController.popBackStack() },
            rightIcons = listOf(
                Pair(R.drawable.round_send_24) { viewmodel.uploadTextPost(context) }
            ),
            rightIconsLoading = viewmodel.isPosting,
            loadingIcon = {
                AnimatedLogo(icon = loadingIcon, iteration = 999, size = 45.dp)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        CommunitySelector(onClick = { showBottomSheet = true }, viewmodel = viewmodel)
        TextField(
            value = viewmodel.title.value,
            onValueChange = { viewmodel.onTitleChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = viewmodel.title.error.isNotEmpty(),
            textStyle = MaterialTheme.typography.titleMedium,
            placeholder = {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            supportingText = {
                Text(text = viewmodel.title.error)
            },
            colors = textFieldColors()
        )

        TextField(
            value = viewmodel.content.value,
            onValueChange = { viewmodel.onContentChange(it) },
            modifier = Modifier.fillMaxSize(),
            singleLine = false,
            isError = viewmodel.content.error.isNotEmpty(),
            minLines = 10,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = "Content",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            supportingText = {
                Text(text = viewmodel.content.error)
            },
            colors = textFieldColorsNoBorder()
        )
    }
}
