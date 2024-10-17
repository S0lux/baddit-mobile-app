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
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _postRepository: PostRepository
) : ViewModel() {

    var isRefreshsing by mutableStateOf(false)
        private set;

    var posts = mutableStateListOf<PostResponseDTOItem>();
    var error by mutableStateOf("");

    fun refreshPosts() {
        viewModelScope.launch {
            isRefreshsing = true;
            when (val fetchPosts = _postRepository.getPosts(null, null, null, null)) {
                is Result.Error -> {
                    error = when (fetchPosts.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection detected"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    posts.clear()
                    error = "";
                    fetchPosts.data.body()?.forEach { item -> posts.add(item) }
                }
            }
            isRefreshsing = false;
        }
    }

    init {
        refreshPosts();
    }
}