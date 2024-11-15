package com.example.baddit.presentation.screens.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.Member
import com.example.baddit.presentation.components.BadditDialog
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.screens.profile.bottomBorder
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.EditCommunity
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.cardForeground
import com.example.baddit.ui.theme.CustomTheme.errorRed
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState as rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityDetailScreen(
    name: String,
    navController: NavController,
    navigatePost: (String) -> Unit,
    viewModel: CommunityViewModel = hiltViewModel(),
    navigateLogin: () -> Unit,
    navigateReply: (String, String) -> Unit,
    darkMode: Boolean
) {
    val community = viewModel.community
    val error = viewModel.error
    val me = viewModel.me
    val memberList = viewModel.memberList
    val isRefreshing = viewModel.isRefreshing
    val moderatorList = viewModel.moderatorList


    val loggedIn by viewModel.loggedIn

    var isPostSectionSelected by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    LaunchedEffect(name) {
        viewModel.fetchCommunity(name)
        viewModel.refreshPosts(name)
        viewModel.fetchMembers(name)
        viewModel.fetchModerators(name)
    }


    when {
        error.isNotEmpty() -> {
            Text(
                text = error,
                color = Color.Red,
            )
        }

        community.value != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                BannerCommunity(community.value!!, navController)
                AvatarCommunity(
                    commmunity = community.value!!,
                    me = me,
                    viewModel,
                    navController,
                    loggedIn,
                    moderatorList.value
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(80.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .bottomBorder(
                                    2.dp,
                                    MaterialTheme.colorScheme.neutralGray,
                                    isPostSectionSelected
                                )
                                .clickable(
                                    onClick = {
                                        isPostSectionSelected = true
                                    },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                )
                        ) {
                            Text(
                                modifier = Modifier.padding(14.dp),
                                text = "Posts",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.textPrimary,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .bottomBorder(
                                    2.dp,
                                    MaterialTheme.colorScheme.neutralGray,
                                    !isPostSectionSelected
                                )
                                .clickable(
                                    onClick = { isPostSectionSelected = false },
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                )
                        ) {
                            Text(
                                modifier = Modifier.padding(14.dp),
                                text = "Members",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.textPrimary
                            )
                        }

                    }

                }
                PostViewCommunity(
                    name = name,
                    loggedIn = loggedIn,
                    navigateLogin = { navController.navigate(Login) },
                    navigatePost = navigatePost,
                    viewModel = viewModel,
                    isPostSectionSelected = isPostSectionSelected,
                    navController = navController,
                    darkMode = darkMode
                )

                if (!isPostSectionSelected) {
                    Box(modifier = Modifier.fillMaxWidth()) {
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

                            memberList.value.isNotEmpty() -> {
                                MembersView(memberList.value, navController, viewModel, name)
                            }

                            else -> {
                                Text(text = "No members")
                            }
                        }
                    }
                }

            }
        }

        else -> {
        }

    }
}

@Composable
fun BannerCommunity(community: GetACommunityResponseDTO, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(community.community.bannerUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
                .fillMaxSize()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = { navController.navigate(Community) },
                modifier = Modifier.background(Color.Transparent),
            ) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.textPrimary
                )
            }
        }
    }
}

@Composable
fun AvatarCommunity(
    commmunity: GetACommunityResponseDTO,
    me: MutableState<GetMeResponseDTO?>,
    viewModel: CommunityViewModel,
    navController: NavController,
    loggedIn: Boolean,
    moderatorList: ArrayList<Member>
) {
    val showLeaveDialog = remember { mutableStateOf(false) }

    if (showLeaveDialog.value) {
        LeaveCommunityDialog(
            onConfirm = {
                showLeaveDialog.value = false
                viewModel.leaveCommunity(commmunity.community.name)
            },
            onDismiss = {
                showLeaveDialog.value = false
            }
        )
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(commmunity.community.logoUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(80.dp)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "r/${commmunity.community.name}",
                style = TextStyle(
                    color = MaterialTheme.colorScheme.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            )
            Text(
                "${commmunity.community.memberCount} members",
                color = MaterialTheme.colorScheme.textPrimary
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        if (loggedIn) {
            if (me.value?.id == commmunity.community.ownerId || checkModerator(
                    moderatorList,
                    me.value!!
                )
            ) {
                OutlinedButton(onClick = {
                    navController.navigate(EditCommunity(commmunity.community.name))
                }
                ) {
                    Text(text = "Edit", color = MaterialTheme.colorScheme.textPrimary)
                }
            } else {
                if (commmunity.joinStatus == "Not Joined") {
                    OutlinedButton(onClick = { viewModel.joinCommunity(commmunity.community.name) }) {
                        Text(text = "Join", color = MaterialTheme.colorScheme.textPrimary)
                    }
                } else {
                    OutlinedButton(onClick = { showLeaveDialog.value = true }) {
                        Text(text = "Joined", color = MaterialTheme.colorScheme.textPrimary)

                    }
                }
            }
        } else {
            OutlinedButton(onClick = { navController.navigate(Login) }) {
                Text(text = "Log in", color = MaterialTheme.colorScheme.textPrimary)
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostViewCommunity(
    name: String,
    loggedIn: Boolean,
    navigateLogin: () -> Unit,
    navigatePost: (String) -> Unit,
    viewModel: CommunityViewModel,
    isPostSectionSelected: Boolean,
    navController: NavController,
    darkMode: Boolean,
) {

    val listState = rememberLazyListState()
    val refreshBoxState = rememberPullToRefreshState()
    LaunchedEffect(name, listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                val lastItem = listState.layoutInfo.totalItemsCount - 1
                lastVisibleItem?.index == lastItem
            }
            .distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd) {
                    viewModel.loadMorePosts(name)
                }
            }
    }

    AnimatedVisibility(
        visible = isPostSectionSelected,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshPosts(name) },
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
                    items(items = viewModel.posts) { item ->
                        PostCard(
                            postDetails = item,
                            loggedIn = loggedIn,
                            navigateLogin = navigateLogin,
                            votePostFn = { voteState: String ->
                                viewModel.postRepository.votePost(
                                    item.id,
                                    voteState
                                )
                            },
                            navigatePost = navigatePost,
                            setPostScore = { score: Int ->
                                viewModel.postRepository.postCache.find { it.id == item.id }!!.score.value =
                                    score
                            },
                            setVoteState = { state: String? ->
                                viewModel.postRepository.postCache.find { it.id == item.id }!!.voteState.value =
                                    state
                            },
                            loggedInUser = viewModel.authRepository.currentUser.value,
                            deletePostFn = { postId: String ->
                                viewModel.postRepository.deletePost(
                                    postId
                                )
                            },
                            navigateEdit = { postId: String ->
                                navController.navigate(
                                    Editing(
                                        postId = postId,
                                        commentId = null,
                                        commentContent = null,
                                        darkMode = darkMode
                                    )
                                )
                            },
                            navigateReply = { postId: String ->
                                navController.navigate(
                                    Comment(
                                        postId = postId,
                                        darkMode = darkMode,
                                        commentContent = null,
                                        commentId = null
                                    )
                                )
                            },
                            onComponentClick = {},
                            navController = navController,
                            imageLoader = viewModel.imageLoader
                        )
                    }
                }
            }

        }
    }
}

@Composable
fun LeaveCommunityDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    BadditDialog(
        title = "Leave Community",
        text = "Are you sure you want to leave this community?",
        confirmText = "Leave", confirmColor = MaterialTheme.colorScheme.errorRed,
        dismissText = "Cancel",
        onConfirm = { onConfirm() },
        onDismiss = { onDismiss() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersView(
    memberList: ArrayList<Member>,
    navController: NavController,
    viewModel: CommunityViewModel,
    name: String
) {
    val refreshBoxState = rememberPullToRefreshState()

    AnimatedVisibility(
        visible = true,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isLoading,
            onRefresh = { viewModel.fetchMembers(name) },
            state = refreshBoxState,
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = viewModel.isLoading,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .semantics { traversalIndex = 1f }
                    .background(MaterialTheme.colorScheme.cardForeground),
            ) {
                items(memberList) { member ->
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp).background(MaterialTheme.colorScheme.cardBackground)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                                .clickable { navController.navigate(Profile(member.username)) }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(member.avatarUrl).build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .height(36.dp)
                                    .aspectRatio(1f),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    member.username,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.textPrimary
                                )
                                Text(
                                    text = member.communityRole,
                                    color = MaterialTheme.colorScheme.textPrimary
                                )

                            }
                        }

                    }
                }
            }
        }


    }
}

fun checkModerator(moderatorList: ArrayList<Member>, user: GetMeResponseDTO): Boolean {
    return moderatorList.any { it.userId == user.id }
}


