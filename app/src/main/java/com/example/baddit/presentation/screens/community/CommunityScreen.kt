package com.example.baddit.presentation.screens.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.presentation.components.BadditDialog
import com.example.baddit.presentation.components.BodyBottomSheet
import com.example.baddit.presentation.components.CommunityList
import com.example.baddit.presentation.components.CreateCommunity
import com.example.baddit.presentation.components.LoginDialog
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.CommunityDetail
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val scopeCreateCommunity = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheetCreateCommunity by remember { mutableStateOf(false) }

    val loggedIn by viewModel.loggedIn

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.refreshCommunities()
    }
    if (showLoginDialog) {
        LoginDialog(
            navigateLogin = { navController.navigate(Login) },
            onDismiss = { showLoginDialog = false })
    }

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
                BodyBottomSheet(viewModel.communityRepository.communityCache, navController) {
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
                    onClick = {
                        if (loggedIn) {
                            showBottomSheetCreateCommunity = true
                        } else {
                            showLoginDialog = true
                        }
                    },
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.textPrimary
                    )
                    Text(text = "Create Community", color = MaterialTheme.colorScheme.textPrimary)
                }
                Spacer(modifier = Modifier.padding(10.dp))

                ListViewCommunities(viewModel = viewModel, navController)

            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewCommunities(viewModel: CommunityViewModel, navController: NavController) {
    val listState = rememberLazyListState()
    val refreshBoxState = rememberPullToRefreshState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                val lastItem = listState.layoutInfo.totalItemsCount - 1
                lastVisibleItem?.index == lastItem
            }
            .distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd) {
                    viewModel.loadMoreCommunities()
                }
            }
    }

    AnimatedVisibility(
        visible = true,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshingCommunity,
            onRefresh = { viewModel.refreshCommunities() },
            state = refreshBoxState,
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = viewModel.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                if (viewModel.error.isEmpty()) {
                    items(items = viewModel.communityRepository.communityCache) { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .clickable {
                                    navController.navigate(CommunityDetail(item.name))
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(item.logoUrl).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .height(50.dp)
                                    .aspectRatio(1f),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    item.name,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.textPrimary
                                )
                                Text(
                                    "${item.memberCount} members",
                                    color = MaterialTheme.colorScheme.textSecondary
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Text(
                            text = viewModel.error,
                            color = Color.Red,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}
