package com.example.baddit.presentation.screens.post

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.baddit.R
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.utils.Login

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navigateLogin: () -> Unit,
    onComponentCLick:()->Unit,
    navController: NavHostController,
    navReply: (String, String) -> Unit,
    viewModel: PostViewModel = hiltViewModel()
) {
    LaunchedEffect(true) {
        viewModel.loadComments(viewModel.postId)
    }

    if (viewModel.error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = viewModel.error)
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isLoading,
        onRefresh = { viewModel.refresh() },
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewModel.error.isNotEmpty()) return@LazyColumn

            item {
                PostCard(
                    postDetails = viewModel.post,
                    loggedIn = viewModel.isLoggedIn,
                    navigateLogin = { navController.navigate(Login) },
                    votePostFn = { voteState: String ->
                        viewModel.postRepository.votePost(
                            viewModel.post.id, voteState
                        )
                    },
                    isExpanded = true,
                    navigatePost = { _: String -> Unit },
                    setPostScore = { score: Int ->
                        viewModel.postRepository.postCache.find { it.id == viewModel.post.id }!!.score.value =
                            score
                    },
                    setVoteState = { state: String? ->
                        viewModel.postRepository.postCache.find { it.id == viewModel.post.id }!!.voteState.value =
                            state
                    },
                    onComponentClick = onComponentCLick
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
                    navigateLogin = { navController.navigate(Login) },
                    navigateReply = navReply,
                    onComponenClick = onComponentCLick
                )
            }
        }
    }
}