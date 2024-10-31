package com.example.baddit.presentation.screens.post

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.presentation.utils.Post
import com.example.baddit.presentation.utils.PostResponseNavType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.reflect.typeOf

@HiltViewModel
class PostViewModel @Inject constructor(
    authRepository: AuthRepository,
    val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var post by mutableStateOf(
        savedStateHandle.toRoute<Post>(
            typeMap = mapOf(
                typeOf<PostResponseDTOItem>() to PostResponseNavType
            )
        ).postDetails
    )

    var error by mutableStateOf("")
        private set;

    var isLoading by mutableStateOf(false)
        private set;

    val isLoggedIn by authRepository.isLoggedIn

    val comments: MutableList<CommentResponseDTOItem> = mutableStateListOf()
    val lastCommentId: String? = null

    fun loadComments(postId: String) {
        viewModelScope.launch {
            isLoading = true
            when (val result = commentRepository.getComments(postId)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Internal server error"
                        else -> "Unknown error"
                    }
                }

                is Result.Success -> {
                    error = ""
                    val dto = result.data
                    comments.clear()
                    comments.addAll(dto)
                }
            }
            isLoading = false
        }
    }

    fun refresh() {
        viewModelScope.launch {
            isLoading = true
            loadComments(post.id)

            val result = postRepository.getPost(post.id)
            when (result) {
                is Result.Success -> {
                    error = ""
                    post = result.data[0]
                }

                is Result.Error -> {
                    error = "No internet connection"
                }
            }

            isLoading = false
        }
    }

    suspend fun voteComment(
        commentId: String,
        state: String
    ): Result<Unit, DataError.NetworkError> {
        return commentRepository.voteComment(commentId, state)
    }
}