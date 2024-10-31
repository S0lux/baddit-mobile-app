package com.example.baddit.presentation.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.model.profile.UserProfile
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val postRepository: PostRepository,
    val commentRepository: CommentRepository
    ) : ViewModel() {

    //user
    val user = mutableStateOf<GetOtherResponseDTO?>(null)
    val me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;
    //posts
    var posts = mutableListOf<PostResponseDTOItem>();
    private var lastPostId: String? = null;
    var endReached = false;
    //comments
    val comments: MutableList<CommentResponseDTOItem> = mutableStateListOf()
    val lastCommentId: String? = null
    //refreshing variable
    var isRefreshing by mutableStateOf(false)
        private set;
    //error
    var error by mutableStateOf("")

    fun refreshPosts(username: String) {
        endReached = false;
        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts(authorName = username)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    posts.clear()
                    error = ""
                    if(!fetchPosts.data.isEmpty())
                        lastPostId = fetchPosts.data.last().id
                    posts.addAll(fetchPosts.data)
                }
            }
            isRefreshing = false
        }
    }
    fun loadMorePosts(){
        if (endReached)
            return;
        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts(cursor = lastPostId)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    if (fetchPosts.data.isNotEmpty()) {
                        lastPostId = fetchPosts.data.last().id
                        posts.addAll(fetchPosts.data)
                    }
                    else {
                        endReached = true
                    }
                }
            }
            isRefreshing = false;
        }
    }
    fun refreshComments(username: String){
        viewModelScope.launch {
            isRefreshing = true;
            when (val result = commentRepository.getComments(authorName = username)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.NO_INTERNET -> "No internet connection."
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Internal server error."
                        else -> "Unknown error."
                    }
                }

                is Result.Success -> {
                    comments.clear()
                    error = ""
                    val dto = result.data
                    dto.forEach { comment -> comments.add(comment) }
                }
            }
            isRefreshing = false
        }
    }
    fun fetchUserProfile(username: String) {
        viewModelScope.launch {
            refreshPosts(username)
            refreshComments(username)
            when (val result = authRepository.getOther(username)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    user.value = result.data
                    error = ""
                }
            }

        }
    }


}