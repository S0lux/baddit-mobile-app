package com.example.baddit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.components.BadditDialog
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigateLogin: () -> Unit) {

    val posts = viewModel.posts
    val error = viewModel.error
    val loggedIn by viewModel.loggedIn

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }

    if (showLoginDialog) {
        LoginDialog(
            navigateLogin = { navigateLogin() },
            onDismiss = { showLoginDialog = false })
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxSize()
        ) {
            if (error.isEmpty()) {
                items(items = posts) { item ->
                    PostCard(
                        postDetails = item,
                        loggedIn,
                        navigateLogin,
                        votePostFn = { voteState: String ->
                            viewModel.postRepository.votePost(
                                item.id,
                                voteState
                            )
                        })
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