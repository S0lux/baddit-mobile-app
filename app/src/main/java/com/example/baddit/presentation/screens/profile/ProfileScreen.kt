package com.example.baddit.presentation.screens.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.screens.login.LoginViewModel
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userId: String,
    username: String,
    navController: NavController,
    navigatePost: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateLogin: () -> Unit,
    navigateReply: (String, String) -> Unit,
    darkMode: Boolean
) {

    val error = viewModel.error
    val loggedIn by viewModel.loggedIn
    val imageLoader = viewModel.imageLoader
    val actionMenuScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    val contentSelectionModalState = rememberModalBottomSheetState()
    var showContentSelectionModal by remember { mutableStateOf(false) }

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }


    LaunchedEffect(username) {
        viewModel.fetchUserProfile(username)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.scaffoldBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.textPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.textPrimary,
                scrolledContainerColor = MaterialTheme.colorScheme.textPrimary,
                titleContentColor = MaterialTheme.colorScheme.textPrimary
            ),
            title = {
                Text(
                    text = "Profile",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(Home) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            actions = {},
        )

        ProfileHeader(
            loggedIn = loggedIn,
            currentUser = viewModel.user.value,
            viewModel = viewModel,
            navController
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(actionMenuScrollState)
                .background(MaterialTheme.colorScheme.scaffoldBackground)
                .padding(horizontal = 10.dp, vertical = 16.dp)
        ) {
            IconMenuItem(
                icon = if (viewModel.isPostSectionSelected.value) R.drawable.comment_empty else R.drawable.comment,
                tint = MaterialTheme.colorScheme.textPrimary,
                text = if (viewModel.isPostSectionSelected.value) "Posts" else "Comments",
                isDropdown = true,
                onClick = { showContentSelectionModal = true }
            )

            if (!viewModel.isMe) {
//
//                val messagePrivacy = viewModel.user.value?.friendRequestStatus;
//                val isFriend = viewModel.user.value?.isFriend;
//                if (messagePrivacy != null && isFriend != null) {
//                    if (messagePrivacy == "EVERYONE") {
//                        IconMenuItem(
//                            icon = R.drawable.outline_message,
//                            tint = MaterialTheme.colorScheme.textPrimary,
//                            text = "Message",
//                            onClick = { TODO() }
//                        )
//                    } else {
//                        if (isFriend == true) {
//                            IconMenuItem(
//                                icon = R.drawable.outline_message,
//                                tint = MaterialTheme.colorScheme.textPrimary,
//                                text = "Message",
//                                onClick = { TODO() }
//                            )
//                        }
//                    }
//                }

                if (viewModel.friendRepository.currentFriends.any { it.id == userId }) {
                    IconMenuItem(
                        icon = R.drawable.remove_user,
                        tint = MaterialTheme.colorScheme.textPrimary,
                        text = "Unfriend",
                        onClick = {
                            coroutineScope.launch {
                                viewModel.friendRepository.removeFriend(
                                    userId
                                )
                            }
                        }
                    )
                }

                if (viewModel.friendRepository.outgoingFriendRequests.any { it.id == userId }) {
                    IconMenuItem(
                        icon = R.drawable.pending,
                        tint = MaterialTheme.colorScheme.textPrimary,
                        text = "Pending",
                        disabled = true
                    )
                }

                if (viewModel.friendRepository.outgoingFriendRequests.all { it.id != userId } &&
                    viewModel.friendRepository.currentFriends.all { it.id != userId }) {
                    IconMenuItem(
                        icon = R.drawable.add_user,
                        tint = MaterialTheme.colorScheme.textPrimary,
                        text = "Friend",
                        onClick = {
                            coroutineScope.launch {
                                viewModel.friendRepository.sendFriendRequest(
                                    userId
                                )
                            }
                        },
                    )
                }

                IconMenuItem(
                    icon = R.drawable.block,
                    tint = MaterialTheme.colorScheme.textPrimary,
                    text = "Block",
                    onClick = { TODO() }
                )
            }
        }

        ProfilePostSection(
            username = username,
            loggedIn = loggedIn,
            navigateLogin = { navController.navigate(Login) },
            navigatePost = navigatePost,
            viewModel = viewModel,
            isPostSectionSelected = viewModel.isPostSectionSelected.value,
            navController = navController,
            darkMode = darkMode,
            imageLoader = imageLoader
        )

        ProfileCommentsSection(
            username = username,
            viewModel = viewModel,
            isPostSectionSelected = viewModel.isPostSectionSelected.value,
            navigateLogin = navigateLogin,
            navigateReply = navigateReply,
            navController = navController,
            darkMode = darkMode
        )

        if (showContentSelectionModal) {
            ModalBottomSheet(
                onDismissRequest = { showContentSelectionModal = false },
                sheetState = contentSelectionModalState,
                containerColor = MaterialTheme.colorScheme.cardBackground
            ) {
                Column(
                    modifier = Modifier.safeContentPadding(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconMenuItem(
                        icon = R.drawable.comment,
                        text = "Posts",
                        tint = MaterialTheme.colorScheme.textPrimary,
                        iconGap = 12.dp,
                        onClick = {
                            viewModel.togglePostSection(true);
                            showContentSelectionModal = false;
                        }
                    )
                    IconMenuItem(
                        icon = R.drawable.comment_empty,
                        text = "Comments",
                        tint = MaterialTheme.colorScheme.textPrimary,
                        iconGap = 12.dp,
                        onClick = {
                            viewModel.togglePostSection(false);
                            showContentSelectionModal = false;
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun IconMenuItem(
    icon: Int,
    tint: Color = MaterialTheme.colorScheme.textSecondary,
    text: String,
    isDropdown: Boolean = false,
    onClick: () -> Unit = { },
    iconGap: Dp = 6.dp,
    disabled: Boolean = false
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(iconGap),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .then(if (!disabled) Modifier.clickable { onClick() } else Modifier)
            .background(MaterialTheme.colorScheme.cardBackground)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            tint = if (!disabled) tint else tint.copy(alpha = 0.5F),
            contentDescription = null)
        Text(
            text = text,
            color = if (!disabled) tint else tint.copy(alpha = 0.5F),
            fontSize = 15.sp)
        if (isDropdown)
            Icon(
                painter = painterResource(R.drawable.arrow_downvote),
                tint = if (!disabled) tint else tint.copy(alpha = 0.5F),
                contentDescription = null)
    }
}

@SuppressLint("WeekBasedYear")
@Composable
fun ProfileHeader(
    loggedIn: Boolean,
    currentUser: GetOtherResponseDTO?,
    viewModel: ProfileViewModel,
    navController: NavController,
    loginViewModel: LoginViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var avatarImg by remember { mutableStateOf<File?>(null) }
    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == RESULT_CANCELED) {
            viewModel.isEditing = false;
            avatarImg = null;
        } else if (result.resultCode == RESULT_OK) {
            viewModel.isEditing = true;
            result.data?.data?.let { uri ->
                val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                avatarImg = file
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.scaffoldBackground)
            .defaultMinSize(100.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(100.dp)
                .padding(10.dp)
                .background(
                    Color.Transparent
                ),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loggedIn) {
                currentUser?.let { currentUser ->
                    if (!viewModel.isMe) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(currentUser.avatarUrl)
                                .build(),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(10.dp)
                                .height(100.dp)
                                .aspectRatio(1f)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box() {
                            Box(
                                modifier = Modifier
                                    .size(104.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp)
                                    .align(Alignment.Center)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                ) {
                                    if (!viewModel.isEditing) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(currentUser.avatarUrl)
                                                .build(),
                                            contentDescription = "Avatar Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .fillMaxSize(),
                                        )
                                    } else {
                                        AsyncImage(
                                            model = ImageRequest.Builder(LocalContext.current)
                                                .data(avatarImg)
                                                .build(),
                                            contentDescription = "Avatar Image",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .fillMaxSize()
                                        )
                                    }
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .clickable {
                                        avatarLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                                            type = "image/*"
                                        });
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Icon",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }
                }
            } else {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentUser?.avatarUrl ?: "https://i.imgur.com/mJQpR31.png")
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .height(130.dp)
                        .aspectRatio(1.0f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(100.dp)
                    .padding(10.dp)
                    .background(Color.Transparent)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        currentUser?.username?.let {
                            Text(
                                text = it,
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 32.sp
                                )
                            )
                        }
                        currentUser?.registeredAt?.let {
                            val localDateTime =
                                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                            val dateTimeFormatted =
                                localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.cake),
                                    tint = MaterialTheme.colorScheme.textSecondary,
                                    contentDescription = null)

                                Text(
                                    text = dateTimeFormatted,
                                    color = MaterialTheme.colorScheme.textSecondary,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (viewModel.isMe && viewModel.isEditing && avatarImg != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                OutlinedButton(
                                    modifier = Modifier.width(87.dp),
                                    onClick = { viewModel.isEditing = false },
                                    border = BorderStroke(
                                        1.dp,
                                        MaterialTheme.colorScheme.neutralGray
                                    ),
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Cancel", style = TextStyle(
                                            color = MaterialTheme.colorScheme.textPrimary,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        )
                                    )

                                }
                                Button(modifier = Modifier.width(100.dp),
                                    onClick = {
                                        viewModel.updateAvatar((avatarImg!!));
                                    },
                                    contentPadding = PaddingValues(0.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.appBlue
                                    )
                                ) {
                                    Text(
                                        text = "Save", style = TextStyle(
                                            color = Color.White,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 16.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePostSection(
    username: String,
    loggedIn: Boolean,
    navigateLogin: () -> Unit,
    navigatePost: (String) -> Unit,
    viewModel: ProfileViewModel,
    isPostSectionSelected: Boolean,
    navController: NavController,
    darkMode: Boolean,
    imageLoader: ImageLoader
) {

    val listState = rememberLazyListState()
    val refreshBoxState = rememberPullToRefreshState()
    LaunchedEffect(username) {
        viewModel.refreshPosts(username)
    }

    LaunchedEffect(username, listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                val lastItem = listState.layoutInfo.totalItemsCount - 1
                lastVisibleItem?.index == lastItem
            }
            .distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd) {
                    viewModel.loadMorePosts(username)
                }
            }
    }

    AnimatedVisibility(
        visible = viewModel.isPostSectionSelected.value,
        exit = slideOutHorizontally(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshingPost,
            onRefresh = { viewModel.refreshPosts(username) },
            state = refreshBoxState,
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = viewModel.isRefreshingPost,
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
                            imageLoader = imageLoader,
                            setSubscriptionStatus = { status: Boolean ->
                                if (status) {
                                    viewModel.postRepository.subscribeToPost(item.id)
                                } else {
                                    viewModel.postRepository.unsubcribeFromPost(item.id)
                                }
                            }
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCommentsSection(
    username: String,
    viewModel: ProfileViewModel,
    isPostSectionSelected: Boolean,
    navigateLogin: () -> Unit,
    navigateReply: (String, String) -> Unit,
    navController: NavController,
    darkMode: Boolean
) {

    val listStateComments = rememberLazyListState()
    val refreshBoxState = rememberPullToRefreshState()
    LaunchedEffect(username, listStateComments) {
        snapshotFlow { listStateComments.layoutInfo.visibleItemsInfo }
            .map { visibleItems ->
                val lastVisibleItem = visibleItems.lastOrNull()
                val lastItem = listStateComments.layoutInfo.totalItemsCount - 1
                lastVisibleItem?.index == lastItem
            }
            .distinctUntilChanged()
            .collect { isAtEnd ->
                if (isAtEnd) {
                    viewModel.loadMoreComments(username)
                }
            }
    }

    AnimatedVisibility(
        visible = !viewModel.isPostSectionSelected.value,
        exit = slideOutHorizontally(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshingComment,
            onRefresh = { viewModel.refreshComments(username) },
            state = refreshBoxState,
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = viewModel.isRefreshingComment,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                state = listStateComments
            ) {
                items(items = viewModel.comments) { it ->
                    CommentCard(
                        details = it,
                        navigateLogin = {},
                        navigateReply = navigateReply,
                        voteFn = { commentId: String, state: String ->
                            viewModel.commentRepository.voteComment(
                                commentId,
                                state
                            )
                        },
                        isLoggedIn = viewModel.loggedIn.value,
                        onComponentClick = {},
                        navigateEdit = { commentId: String, content: String ->
                            navController.navigate(
                                Editing(
                                    postId = null,
                                    commentContent = content,
                                    commentId = commentId,
                                    darkMode = darkMode
                                )
                            )
                        },
                        deleteFn = { commentId: String ->
                            viewModel.commentRepository.deleteComment(
                                commentId
                            )
                        },
                        loggedInUser = viewModel.authRepository.currentUser.value
                    )
                }
            }
        }
    }
}

fun Modifier.bottomBorder(strokeWidth: Dp, color: Color, isSelected: Boolean) = composed(
    factory = {
        val density = LocalDensity.current
        val strokeWidthPx = density.run { strokeWidth.toPx() }

        Modifier.drawBehind {
            val width = size.width
            val height = size.height - strokeWidthPx / 2
            if (isSelected) {

                drawLine(
                    color = color,
                    start = Offset(x = 0f, y = height),
                    end = Offset(x = width, y = height),
                    strokeWidth = strokeWidthPx
                )
            }
        }
    }
)

