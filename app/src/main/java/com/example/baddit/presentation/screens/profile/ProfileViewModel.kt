package com.example.baddit.presentation.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.domain.model.posts.toMutablePostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import com.example.baddit.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    val authRepository: AuthRepository,
    val postRepository: PostRepository,
    val commentRepository: CommentRepository,
    val userRepository: UserRepository,
    val imageLoader: ImageLoader,
) : ViewModel() {

    //user
    var user = mutableStateOf<GetOtherResponseDTO?>(null)
    var me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;
    var isMe by mutableStateOf(false)
        private set;

    //posts
    val posts = postRepository.postCache;
    private var lastPostId: String? = null;
    var endPostReached = false;

    //comments
    val comments: MutableList<CommentResponseDTOItem> = mutableStateListOf()
    private var lastCommentId: String? = null;
    var endCommentReached = false;

    //refreshing variable
    var isRefreshingPost by mutableStateOf(false)
    var isRefreshingComment by mutableStateOf(false)
        private set;
    var isEditing by mutableStateOf(false)

    var isUpdating by mutableStateOf(false)

    val isPostSectionSelected = mutableStateOf(true)

    fun togglePostSection(boolean: Boolean) {
        viewModelScope.launch {
            isPostSectionSelected.value = boolean
            if (boolean) {
                refreshPosts(username = user.value!!.username)
            } else {
                refreshComments(username = user.value!!.username)

            }
        }
    }

    //error
    var error by mutableStateOf("")

    fun refreshPosts(username: String) {
        endPostReached = false;
        viewModelScope.launch {
            isRefreshingPost = true;
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
                    if (!fetchPosts.data.isEmpty())
                        lastPostId = fetchPosts.data.last().id
                    posts.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                }
            }
            isRefreshingPost = false
        }
    }

    fun loadMorePosts(username: String) {
        if (endPostReached)
            return;
        viewModelScope.launch {
            isRefreshingPost = true;
            when (val fetchPosts =
                postRepository.getPosts(cursor = lastPostId, authorName = username)) {
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
                        posts.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                    } else {
                        endPostReached = true
                    }
                }
            }
            isRefreshingPost = false;
        }
    }

    fun refreshComments(username: String) {
        endCommentReached = false;
        viewModelScope.launch {
            isRefreshingComment = true;
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
                    posts.clear()
                    error = ""
                    if (!result.data.isEmpty())
                        lastCommentId = result.data.last().id
                    comments.addAll(result.data.filter { !it.deleted })
                }
            }
            isRefreshingComment = false
        }
    }

    fun loadMoreComments(username: String) {
        if (endCommentReached)
            return;
        viewModelScope.launch {
            isRefreshingComment = true;
            when (val fetchComments =
                commentRepository.getComments(cursor = lastCommentId, authorName = username)) {
                is Result.Error -> {
                    error = when (fetchComments.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    error = ""
                    if (fetchComments.data.isNotEmpty()) {
                        lastCommentId = fetchComments.data.last().id
                        comments.addAll(fetchComments.data.filter { !it.deleted })
                    } else {
                        endCommentReached = true
                    }
                }
            }
            isRefreshingComment = false;
        }
    }

    fun fetchUserProfile(username: String) {
        viewModelScope.launch {
            isMe = if (username == me.value!!.username) {
                true;
            } else {
                false;
            }
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
                    Log.d("ProfileScreen", "Data: " + user.value!!.username)
                    error = ""
                }
            }

        }
    }

    fun reloadAvatarDisplay() {
        viewModelScope.launch {
            authRepository.getMe();
            me = authRepository.currentUser;
            fetchUserProfile(me.value!!.username)
            refreshPosts(me.value!!.username)
            refreshComments(me.value!!.username)
        }
    }

    fun updateAvatar(imageFile: File) {
        viewModelScope.launch {

            isUpdating = true
            viewModelScope.launch {
                val result = userRepository.updateAvatar(imageFile)
                when (result) {
                    is Result.Error -> {
                        error = when (result.error) {
                            DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                            DataError.NetworkError.NO_INTERNET -> "No internet connection"
                            else -> "An unknown network error has occurred"
                        }
                    }

                    is Result.Success -> {
                        error = ""
                        isUpdating = false
                        isEditing = false
                        reloadAvatarDisplay()
                    }
                }
            }
            isUpdating = false
        }
    }

}