package com.example.baddit.presentation.screens.post

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.baddit.R
import com.example.baddit.presentation.components.BaseTopNavigationBar
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.components.TopNavigationBar
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.Login
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(
    navController: NavHostController,
    navReply: (String, String) -> Unit,
    viewModel: PostViewModel = hiltViewModel(),
    darkMode: Boolean,
    onComponentClick: () -> Unit,
) {
    val refreshBoxState = rememberPullToRefreshState()
    LaunchedEffect(true) {
        viewModel.loadComments(viewModel.postId)
    }

    if (viewModel.error.isNotEmpty()) {
        ErrorNotification(
            icon = if (viewModel.postNotFound) R.drawable.not_found else R.drawable.wifi_off,
            text = viewModel.error
        )
    }

    PullToRefreshBox(
        isRefreshing = viewModel.isLoading,
        onRefresh = { viewModel.refresh() },
        state = refreshBoxState,
        indicator = {
            Indicator(
                state = refreshBoxState,
                isRefreshing = viewModel.isLoading,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = MaterialTheme.colorScheme.background,
                color = MaterialTheme.colorScheme.textPrimary)
        },
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (viewModel.error.isNotEmpty()) return@LazyColumn

            stickyHeader {
                BaseTopNavigationBar(
                    title = "Post",
                    leftIcon = R.drawable.baseline_arrow_back_24,
                    onLeftIconClick = { navController.popBackStack() })
            }
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
                    loggedInUser = viewModel.authRepository.currentUser.value,
                    deletePostFn = { postId: String -> viewModel.postRepository.deletePost(postId) },
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
                    allowNavigateSelf = false,
                    setSubscriptionStatus = { status: Boolean ->
                        if (status) {
                            viewModel.postRepository.subscribeToPost(viewModel.postId)
                        } else {
                            viewModel.postRepository.unsubcribeFromPost(viewModel.postId)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            if (viewModel.highlightedComment != null) {
                items(items = viewModel.comments.filter { it.id == viewModel.highlightedComment }) { it ->
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
                        navController = navController,
                        onComponentClick = onComponentClick,
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
                        loggedInUser = viewModel.authRepository.currentUser.value,
                        isHighlighted = true
                    )
                }
            }

            items(items = viewModel.comments) { it ->
                if (it.id == viewModel.highlightedComment) return@items
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
                    navController = navController,
                    onComponentClick = onComponentClick,
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