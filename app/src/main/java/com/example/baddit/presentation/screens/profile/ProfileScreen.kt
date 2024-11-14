package com.example.baddit.presentation.screens.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.font.FontStyle
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
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
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
                val titleText = if (viewModel.loggedIn.value) {
                    viewModel.user.value?.username?.let {
                        "u/$it"
                    } ?: "u/UnknownUser"
                } else {
                    "u/UnknownUser"
                }
                Text(
                    text = titleText,
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(125.dp)
            ) {
                Box(
                    modifier = Modifier
                        .bottomBorder(
                            2.dp,
                            MaterialTheme.colorScheme.neutralGray,
                            viewModel.isPostSectionSelected.value
                        )
                        .clickable(
                            onClick = { viewModel.togglePostSection(true) },
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
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .bottomBorder(
                            2.dp,
                            MaterialTheme.colorScheme.neutralGray,
                            !viewModel.isPostSectionSelected.value
                        )
                        .clickable(
                            onClick = { viewModel.togglePostSection(false) },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        )
                ) {
                    Text(
                        modifier = Modifier.padding(14.dp),
                        text = "Comments",
                        style = TextStyle(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }

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
                        .data("https://i.imgur.com/mJQpR31.png")
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
                                text = "u/$it",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 25.sp
                                )
                            )
                        }
                        currentUser?.registeredAt?.let {
                            val localDateTime =
                                LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                            val dateTimeFormatted =
                                localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            Text(
                                text = "Cake day: $dateTimeFormatted",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 15.sp
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        if (viewModel.isMe && viewModel.isEditing && avatarImg != null) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(20.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.isEditing = false },
                                    border = BorderStroke(
                                        1.dp,
                                        Color.Red
                                    ),
                                    contentPadding = PaddingValues(10.dp)
                                ) {
                                    Text(
                                        text = "Cancel", style = TextStyle(
                                            color = Color.Red,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
                                        )
                                    )

                                }
                                OutlinedButton(
                                    onClick = {
                                        viewModel.updateAvatar((avatarImg!!));
                                    },
                                    border = BorderStroke(
                                        1.dp,
                                        Color.Green
                                    ),
                                    contentPadding = PaddingValues(10.dp)
                                ) {
                                    Text(
                                        text = "Save", style = TextStyle(
                                            color = Color.Green,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 20.sp
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
        exit = slideOutHorizontally() + fadeOut(),
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
                            imageLoader = imageLoader
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
        exit = slideOutHorizontally() + fadeOut(),
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
                )}) {
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

