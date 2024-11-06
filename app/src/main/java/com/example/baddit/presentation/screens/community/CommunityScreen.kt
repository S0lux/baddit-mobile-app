package com.example.baddit.presentation.screens.community

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.baddit.R
import com.example.baddit.presentation.components.BodyBottomSheet
import com.example.baddit.presentation.components.CommunityList
import com.example.baddit.presentation.components.CreateCommunity
import com.example.baddit.presentation.utils.CommunityDetail
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val communityList = viewModel.communityList
    val isRefreshing = viewModel.isRefreshing
    val error = viewModel.error

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scopeCreateCommunity = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheetCreateCommunity by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(
            title = {
                val titleText = "Communities"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = titleText,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier.padding(start = 30.dp),
                        onClick = { showBottomSheet = true }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.scaffoldBackground)
        )

        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.textPrimary
            ) {
                // Sheet content
                BodyBottomSheet(communityList.value, navController) {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }
            }
        }

        if (showBottomSheetCreateCommunity) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                onDismissRequest = {
                    showBottomSheetCreateCommunity = false
                },
                sheetState = sheetState,
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.textPrimary
            ) {
                // Sheet content
                CreateCommunity(viewModel) {
                    scopeCreateCommunity.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) {
                            showBottomSheetCreateCommunity = false
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ) {
            Column(modifier = Modifier.padding(0.dp)) {
                OutlinedButton(
                    onClick = { showBottomSheetCreateCommunity = true },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.textPrimary)
                    Text(text = "Create Community", color = MaterialTheme.colorScheme.textPrimary)
                }
                Spacer(modifier = Modifier.padding(10.dp))
                when {
                    isRefreshing -> {
                        CircularProgressIndicator()
                    }

                    error.isNotEmpty() -> {
                        Text(
                            text = error,
                            color = Color.Red,
                        )
                    }

                    communityList.value.isNotEmpty() -> {
                        CommunityList(
                            paddingValues = PaddingValues(10.dp),
                            communities = communityList.value,
                            navController
                        )
                    }

                    else -> {
                        Text(text = "No communities found")
                    }
                }

            }
        }
    }

    viewModel.fetchCommunityList()
}