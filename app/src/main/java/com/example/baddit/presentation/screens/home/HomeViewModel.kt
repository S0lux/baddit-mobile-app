package com.example.baddit.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.model.posts.toMutablePostResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    val postRepository: PostRepository,
    val authRepository: AuthRepository
) : ViewModel() {
    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("");
    var noMorePosts by mutableStateOf(false)
    var showNoPostWarning by mutableStateOf(false)
    val loggedIn = authRepository.isLoggedIn;
    var endReached = false;

    private var lastPostId: String? = null;

    fun refreshPosts() {
        noMorePosts = false

        viewModelScope.launch {
            isRefreshing = true;
            when (val fetchPosts = postRepository.getPosts()) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    lastPostId = fetchPosts.data.last().id
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