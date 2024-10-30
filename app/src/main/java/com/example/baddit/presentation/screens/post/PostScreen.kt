package com.example.baddit.presentation.screens.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.presentation.components.CommentCard
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.components.PostCard
import com.example.baddit.presentation.utils.decodePostResponseDTOItem

@Composable
fun PostScreen(
    navigateLogin: () -> Unit,
    postDetails: PostResponseDTOItem,
    viewModel: PostViewModel = hiltViewModel()
) {
    val decoded = decodePostResponseDTOItem(postDetails)

    LaunchedEffect(true) {
        viewModel.loadComments(decoded.id)
    }

    if (viewModel.error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = viewModel.error)
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

        CommentSection(viewModel.comments)
    }
}

@Composable
fun CommentSection(comments: List<CommentResponseDTOItem>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        items(items = comments) {
            it -> CommentCard(it)
        }
    }
}