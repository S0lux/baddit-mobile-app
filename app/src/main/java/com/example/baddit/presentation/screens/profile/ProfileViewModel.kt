package com.example.baddit.presentation.screens.profile

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.model.profile.UserProfile
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

    val user = mutableStateOf<GetOtherResponseDTO?>(null)

    val me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;

    var posts = mutableListOf<PostResponseDTOItem>();

    private var lastPostId: String? = null;

    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("")

    fun refreshPosts(username: String) {
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

    fun fetchUserProfile(username: String) {
        viewModelScope.launch {
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

    fun onPostsSelected(username: String) {
        refreshPosts(username)
    }

    fun onCommentsSelected() {

    }

}