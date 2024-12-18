package com.example.baddit.presentation.screens.home

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.model.posts.toMutablePostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.NotificationRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    val postRepository: PostRepository,
    val authRepository: AuthRepository,
    val notificationRepository: NotificationRepository
) : ViewModel() {
    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("");
    var noMorePosts by mutableStateOf(false)
    var showNoPostWarning by mutableStateOf(false)
    val loggedIn = authRepository.isLoggedIn;
    var endReached = false;

    val notifications = notificationRepository.notifications

    private var lastPostId: String? = null;

    fun refreshPosts(orderByScore : String? = null) {
        noMorePosts = false

        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts(orderByScore = orderByScore)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    lastPostId = if (fetchPosts.data.isNotEmpty()) fetchPosts.data.last().id else null
                    error = ""

                    postRepository.postCache.clear()
                    postRepository.postCache.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                }
            }
            isRefreshing = false;
        }
    }

    fun loadMorePosts() {
        if (noMorePosts) return

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

                        postRepository.postCache.addAll(fetchPosts.data.map { it.toMutablePostResponseDTOItem() })
                    }
                    else {
                        noMorePosts = true
                        showNoPostWarning = true
                    }
                }
            }
            isRefreshing = false;
        }
    }



    init {
        refreshPosts();
    }
}