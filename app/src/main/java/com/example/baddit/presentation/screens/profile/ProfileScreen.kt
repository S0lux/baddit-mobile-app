package com.example.baddit.presentation.screens.profile

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.styles.gradientBackGroundBrush
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    navController: NavController,
    navigatePost: (PostResponseDTOItem) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
    navigateLogin: () -> Unit
) {

    val error = viewModel.error
    val loggedIn by viewModel.loggedIn

    var isPostSectionSelected by remember { mutableStateOf(true) }

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }


    LaunchedEffect(username) {
        viewModel.fetchUserProfile(username)
        viewModel.refreshPosts(username)
        viewModel.refreshComments(username)
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
        ProfileHeader(loggedIn = loggedIn, currentUser = viewModel.user.value)
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
                TextButton(
                    modifier = Modifier
                        .bottomBorder(3.dp, Color.Blue, isPostSectionSelected),
                    onClick = { isPostSectionSelected = true }) {
                    Text(
                        text = "Posts",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.textPrimary
                    )
                }
                TextButton(
                    modifier = Modifier
                        .bottomBorder(3.dp, Color.Blue, !isPostSectionSelected),
                    onClick = { isPostSectionSelected = false }
                ) {
                    Text(
                        text = "Comments",
                        style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
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
            isPostSectionSelected = isPostSectionSelected
        )
        ProfileCommentsSection(
            username = username,
            viewModel = viewModel,
            isPostSectionSelected = isPostSectionSelected,
            navigateLogin = navigateLogin
        )
    }
}


@SuppressLint("WeekBasedYear")
@Composable
fun ProfileHeader(
    loggedIn: Boolean,
    currentUser: GetOtherResponseDTO?
) {
    val gradientList = listOf(
        Color(0xFF232526),
        Color(0xFF414345),
    )
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
                    gradientBackGroundBrush(
                        isVerticalGradient = true,
                        colors = gradientList
                    )
                ),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (loggedIn) {
                currentUser?.let { currentUser ->
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
                    .height(100.dp)
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
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 30.sp
                                )
                            )
                            currentUser.registeredAt.let {
                                val localDateTime =
                                    LocalDateTime.parse(it, DateTimeFormatter.ISO_DATE_TIME)
                                val dateTimeFormatted =
                                    localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                                Text(
                                    text = "Cake day: $dateTimeFormatted",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                modifier = Modifier.border(
                                    border = BorderStroke(1.dp, Color.White),
                                    shape = RoundedCornerShape(50)
                                )
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                onClick = { /*TODO*/ }
                            ) {
                                Text(
                                    text = "Edit",
                                    style = TextStyle(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePostSection(
    username: String,
    loggedIn: Boolean,
    navigateLogin: () -> Unit,
    navigatePost: (PostResponseDTOItem) -> Unit,
    viewModel: ProfileViewModel,
    isPostSectionSelected: Boolean
) {

    val listState = rememberLazyListState()
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
        visible = isPostSectionSelected,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshPosts(username) }) {
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
                            navigatePost = navigatePost
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
    navigateLogin: () -> Unit
) {

    val listState = rememberLazyListState()
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
                    //viewModel.loadMorePosts(username)
                }
            }
    }

    val comments = viewModel.comments

    AnimatedVisibility(
        visible = !isPostSectionSelected,
        exit = slideOutHorizontally() + fadeOut(),
        enter = slideInHorizontally()
    ) {
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            onRefresh = { viewModel.refreshComments(username) }) {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                items(items = viewModel.comments) { it ->
                    CommentCard(
                        details = it,
                        navigateLogin = navigateLogin,
                        navigateReply = { a: String?, b: String?, c: String? -> Unit },
                        voteFn = { commentId: String, state: String ->
                            viewModel.commentRepository.voteComment(
                                commentId,
                                state
                            )
                        },
                        isLoggedIn = viewModel.loggedIn.value
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

