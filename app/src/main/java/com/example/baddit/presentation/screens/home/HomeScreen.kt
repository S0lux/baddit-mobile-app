package com.example.baddit.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.presentation.components.AnimatedLogo
import com.example.baddit.presentation.components.BadditDialog
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateLogin: () -> Unit,
    navigatePost: (String) -> Unit
) {
    val listState = rememberLazyListState()
    viewModel.endReached = !listState.canScrollForward

    LaunchedEffect(viewModel.endReached) {
        if (viewModel.endReached) {
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
            navigateLogin = { navigateLogin() },
            onDismiss = { showLoginDialog = false })
    }

    if (viewModel.noMorePosts) {
        BadditDialog(
            title = "Woah",
            text = "It seems like you have scrolled to the end of of all posts. Impressive!",
            confirmText = "Okay",
            dismissText = "Cancel",
            onConfirm = { viewModel.noMorePosts = false }) {

        }
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }) {
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
                        navigateLogin = { navigateLogin() },
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
                        }
                    )
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