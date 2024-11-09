package com.example.baddit.presentation.screens.community

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.Member
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
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

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
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(80.dp)
                    ) {
                        TextButton(
                            modifier = Modifier
                                .bottomBorder(3.dp, Color.Blue, isPostSectionSelected),
                            onClick = { isPostSectionSelected = true }) {
                            Text(
                                text = "Post",
                                style = TextStyle(
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = MaterialTheme.colorScheme.textPrimary
                            )
                        }
                        TextButton(
                            modifier = Modifier
                                .bottomBorder(3.dp, Color.Blue, !isPostSectionSelected),
                            onClick = { isPostSectionSelected = false }
                        ) {
                            Text(
                                text = "Members",
                                style = TextStyle(
                                    fontSize = 20.sp,
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
                    Box(modifier = Modifier.padding(10.dp)) {
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
                                MembersView(memberList.value, navController)
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
fun BannerCommunity(commmunity: GetACommunityResponseDTO, navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(5.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(commmunity.community.bannerUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.Fit,
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
            onRefresh = { viewModel.refreshPosts(name) }) {
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
                            navController = navController
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
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Leave Community",
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        },
        text = {
            Text(
                text = "Are you sure you want to leave this community?",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red,
                    contentColor = Color.White
                ),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Text(text = "Leave")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray,
                    contentColor = Color.White
                )
            ) {
                Text(text = "Cancel")
            }
        }
    )
}

@Composable
fun MembersView(memberList: ArrayList<Member>, navController: NavController) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.semantics { traversalIndex = 1f },
    ) {
        items(memberList) { member ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
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

fun checkModerator(moderatorList: ArrayList<Member>, user: GetMeResponseDTO): Boolean {
    return moderatorList.any { it.userId == user.id }
}


