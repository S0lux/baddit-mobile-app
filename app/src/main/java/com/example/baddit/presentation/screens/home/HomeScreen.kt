package com.example.baddit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.async

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {

    val pullToRefreshState = rememberPullToRefreshState()
    val posts = viewModel.posts;
    val error = viewModel.error;

    if (pullToRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            val refresh = async { viewModel.refreshPosts() };
            refresh.await()
            pullToRefreshState.endRefresh()
        }
    }

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    Box(modifier = Modifier.nestedScroll(pullToRefreshState.nestedScrollConnection)) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxSize()
        ) {
            if (error.isEmpty()) {
                itemsIndexed(posts) { _, item ->
                    PostCard(postDetails = item)
                }
            }
        }

        PullToRefreshContainer(
            state = pullToRefreshState, modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}