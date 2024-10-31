package com.example.baddit.presentation.screens.post

import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.utils.decodePostResponseDTOItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navigateLogin: () -> Unit,
    viewModel: PostViewModel = hiltViewModel()
) {
    val decoded = decodePostResponseDTOItem(viewModel.post)

    LaunchedEffect(true) {
        viewModel.loadComments(decoded.id)
    }

    if (viewModel.error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = viewModel.error)
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewModel.error.isNotEmpty()) return@LazyColumn

            item {
                PostCard(
                    postDetails = decoded,
                    loggedIn = viewModel.isLoggedIn,
                    navigateLogin = { navigateLogin() },
                    votePostFn = { voteState: String ->
                        viewModel.postRepository.votePost(
                            decoded.id, voteState
                        )
                    },
                    isExpanded = true,
                    navigatePost = { _: PostResponseDTOItem -> Unit }
                )
                
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(items = viewModel.comments) { it ->
                CommentCard(
                    details = it,
                    voteFn = { commentId: String, state: String ->
                        viewModel.voteComment(
                            commentId,
                            state
                        )
                    },
                    isLoggedIn = viewModel.isLoggedIn,
                    navigateLogin = navigateLogin,
                    navigateReply = { a: String?, b: String?, c: String? -> Unit }
                )
            }
        }
    }
}