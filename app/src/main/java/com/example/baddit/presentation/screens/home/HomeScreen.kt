package com.example.baddit.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.baddit.R
import com.example.baddit.presentation.components.BadditDialog
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.components.TopNavigationBar
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Post
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController,
    darkMode: Boolean,
    showAvatarMenu: MutableState<Boolean>,
    onComponentClick: () -> Unit,
    drawerState: DrawerState
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val refreshBoxState = rememberPullToRefreshState()
    viewModel.endReached = !listState.canScrollForward

    LaunchedEffect(viewModel.endReached) {
        if (viewModel.endReached && listState.firstVisibleItemIndex > 1) {
            Log.d("INFINITE_SCROLL", "LOADING MORE POSTS!")
            Log.d("INFINITE_SCROLL", "END REACHED STATE: ${viewModel.endReached}")
            viewModel.loadMorePosts()
        }
    }

    val error = viewModel.error
    val loggedIn by viewModel.loggedIn

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    var showLoginDialog by remember { mutableStateOf(false) }

    if (showLoginDialog) {
        LoginDialog(
            navigateLogin = { navController.navigate(Login) },
            onDismiss = { showLoginDialog = false })
    }

    if (viewModel.showNoPostWarning) {
        BadditDialog(
            title = "Woah",
            text = "It seems like you have scrolled to the end of all posts. Impressive!",
            confirmText = "Okay",
            dismissText = "Cancel",
            onConfirm = { viewModel.showNoPostWarning = false; },
            onDismiss = { viewModel.showNoPostWarning = false; }
        )
    }

    Scaffold(
        topBar = {
            TopNavigationBar(
                navController = navController,
                barState = true,
                userTopBarState = false,
                showAvatarMenu = showAvatarMenu,
                onDrawerClicked = {
                    scope.launch {
                        if (drawerState.isOpen) drawerState.close()
                        else drawerState.open()
                    }
                },
                hasNotification = viewModel.notifications.count { !it.isRead } > 0
            )
        }
    ) { it ->
        PullToRefreshBox(
            isRefreshing = viewModel.isRefreshing,
            state = refreshBoxState,
            modifier = Modifier.padding(it),
            onRefresh = { viewModel.refreshPosts()},
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = viewModel.isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.textPrimary)
            }
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                if (error.isEmpty()) {
                    items(items = viewModel.postRepository.postCache) { item ->
                        PostCard(
                            postDetails = item,
                            loggedIn = loggedIn,
                            navigateLogin = { navController.navigate(Login) },
                            votePostFn = { voteState: String ->
                                viewModel.postRepository.votePost(
                                    item.id,
                                    voteState
                                )
                            },
                            navigatePost = { postId: String ->
                                if (drawerState.isOpen) {
                                    scope.launch {
                                        drawerState.close()
                                    }
                                } else {
                                    navController.navigate(
                                        Post(postId = postId)
                                    )
                                }

                            },
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
                            onComponentClick = onComponentClick,
                            navController = navController,
                            imageLoader = viewModel.imageLoader,
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

@Composable
fun LoginDialog(navigateLogin: () -> Unit, onDismiss: () -> Unit) {
    BadditDialog(
        title = "Login required",
        text = "You need to login to perform this action.",
        confirmText = "Login",
        dismissText = "Cancel",
        onConfirm = { navigateLogin() },
        onDismiss = { onDismiss() })
}