package com.example.baddit.presentation.screens.comment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.presentation.utils.Comment
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    val postRepository: PostRepository,
    val commentRepository: CommentRepository,
    val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val arguments = savedStateHandle.toRoute<Comment>()

    var isLoading by mutableStateOf(false)
        private set;

    var error by mutableStateOf("")
        private set;

    var success by mutableStateOf(false)
        private set;

    var userInput by mutableStateOf("")
        private set;

    fun onUserInput(input: String) {
        userInput = input
    }

    fun onSend() {
        if (userInput.isEmpty()) {
            error = "This field cannot be empty"
            return
        }

        isLoading = true
        viewModelScope.launch {
            if (arguments.postId != null && arguments.commentId == null) {
                commentRepository.replyPost(postId = arguments.postId, content = userInput)
            }

            if (arguments.commentId != null && arguments.postId != null) {
                commentRepository.replyComment(parentId = arguments.commentId, content = userInput, postId = arguments.postId)
            }

            isLoading = false
            success = true
        }
    }
}