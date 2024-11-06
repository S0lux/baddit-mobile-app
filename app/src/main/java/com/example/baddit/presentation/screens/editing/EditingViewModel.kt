package com.example.baddit.presentation.screens.editing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.presentation.utils.Editing
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditingViewModel @Inject constructor(
    val postRepository: PostRepository,
    val commentRepository: CommentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    val arguments = savedStateHandle.toRoute<Editing>()

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
            val result = postRepository.editPost(postId = arguments.postId!!, content = userInput)
            isLoading = false

            when (result) {
                is Result.Success -> {
                    success = true
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        DataError.NetworkError.UNAUTHORIZED -> "You do not have permission"
                        else -> "An unexpected error has occurred"
                    }
                }
            }
        }
    }

    init {
        if (arguments.commentId == null) {
            val post = postRepository.postCache.find { it.id == arguments.postId }

            if (post != null && post.type != "MEDIA") {
                userInput = post.content.value
            }
        } else if (arguments.commentContent != null) userInput = arguments.commentContent;
    }
}