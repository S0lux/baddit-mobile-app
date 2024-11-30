package com.example.baddit.presentation.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.domain.model.chat.chatChannel.ChatMember
import com.example.baddit.presentation.components.BodyBottomSheet
import com.example.baddit.presentation.components.CreateCommunity
import com.example.baddit.presentation.components.LoginDialog
import com.example.baddit.presentation.screens.community.ListViewCommunities
import com.example.baddit.presentation.utils.ChannelDetail
import com.example.baddit.presentation.utils.CommunityDetail
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
fun ChannelListScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    val scopeCreateChatChannel = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState()
    var showBottomSheetCreateChatChannel by remember { mutableStateOf(false) }
    val loggedIn by viewModel.loggedIn

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.refreshChannelList()
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
                val titleText = "Chat Channels"
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
//                BodyBottomSheet(viewModel.chatRepository.channelListCache, navController) {
//                    scope.launch { sheetState.hide() }.invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            showBottomSheet = false
//                        }
//                    }
//                }
            }
        }

        if (showBottomSheetCreateChatChannel) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                onDismissRequest = {
                    showBottomSheetCreateChatChannel = false
                },
                sheetState = sheetState,
                shape = MaterialTheme.shapes.large,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.textPrimary
            ) {
                // Sheet content
//                CreateCommunity(viewModel) {
//                    scopeCreateCommunity.launch { bottomSheetState.hide() }.invokeOnCompletion {
//                        if (!bottomSheetState.isVisible) {
//                            showBottomSheetCreateCommunity = false
//                        }
//                    }
//                }
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
                            showBottomSheetCreateChatChannel = true
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
                    Text(
                        text = "Create Chat Channel",
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
                Spacer(modifier = Modifier.padding(10.dp))

                ListViewChannels(viewModel = viewModel, navController)

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListViewChannels(viewModel: ChatViewModel, navController: NavController) {
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
            .collect {
            }
    }

    AnimatedVisibility(
        visible = true,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshingChannelList,
            onRefresh = { viewModel.refreshChannelList() },
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
                    items(items = viewModel.chatRepository.channelListCache) { item ->
                        var otherUser: ChatMember? = null
                        if (item.members.size > 1) {
                            otherUser = item.members.firstOrNull { member ->
                                member.id != viewModel.me.value?.id
                            }
                        }
                        if (otherUser != null) {

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .clickable {
                                    navController.navigate(ChannelDetail(channelId = item.id, channelName = otherUser.username, channelAvatar = otherUser.avatarUrl))
                                    }
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(otherUser.avatarUrl).build(),
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
                                        otherUser.username,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.textPrimary
                                    )
//                                    Text(
//                                        "${item.members.size} members",
//                                        color = MaterialTheme.colorScheme.textSecondary
//                                    )
                                }
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