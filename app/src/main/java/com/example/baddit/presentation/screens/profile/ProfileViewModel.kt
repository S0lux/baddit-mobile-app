package com.example.baddit.presentation.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    val postRepository: PostRepository,

    ) : ViewModel() {

    val currentUser = authRepository.currentUser;

    val loggedIn = authRepository.isLoggedIn;

    var posts = mutableListOf<PostResponseDTOItem>();

    private var lastPostId: String? = null;

    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("")

    fun refreshPosts() {
        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts(authorName = currentUser.value!!.username)) {
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
                    lastPostId = fetchPosts.data.last().id

                    posts.addAll(fetchPosts.data)
                }
            }

            isRefreshing = false
        }
    }

    fun onPostsSelected() {
        refreshPosts();

    }

    fun onCommentsSelected() {

    }
}