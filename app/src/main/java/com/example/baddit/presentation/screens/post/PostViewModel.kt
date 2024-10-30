package com.example.baddit.presentation.screens.post

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    authRepository: AuthRepository,
    val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
) : ViewModel() {
    var error by mutableStateOf("")
    val isLoggedIn by authRepository.isLoggedIn

    val comments: MutableList<CommentResponseDTOItem> = mutableStateListOf()
    val lastCommentId: String? = null

    fun loadComments(postId: String) {
        viewModelScope.launch {
            when (val result = commentRepository.getComments(postId)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.NO_INTERNET -> "No internet connection."
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Internal server error."
                        else -> "Unknown error."
                    }
                }

                is Result.Success -> {
                    error = ""
                    val dto = result.data
                    comments.clear()
                    comments.addAll(dto)
                }
            }
        }
    }
}