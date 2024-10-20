package com.example.baddit.presentation.screens.home

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), navigateLogin: () -> Unit) {

    val posts = viewModel.posts
    val error = viewModel.error
    val loggedIn by viewModel.loggedIn
    Log.d("HomeScreen", "Logged in: $loggedIn")

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isRefreshing,
        onRefresh = { viewModel.refreshPosts() }) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxSize()
        ) {
            if (error.isEmpty()) {
                itemsIndexed(posts) { _, item ->
                    PostCard(
                        postDetails = item,
                        loggedIn,
                        navigateLogin,
                        votePost = { voteState: String -> viewModel.postRepository.votePost(item.id, voteState) })
                }
            }
        }

    }
}