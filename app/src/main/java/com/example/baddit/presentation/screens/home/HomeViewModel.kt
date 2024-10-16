package com.example.baddit.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.error.errors.NetworkError
import com.example.baddit.domain.model.posts.PostDTOItem
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val _postRepository: PostRepository
) : ViewModel() {

    var posts = mutableStateListOf<PostDTOItem>();

    suspend fun refreshPosts() {
        when (val fetchPosts = _postRepository.getPosts(null, null, null, null)) {
            is Result.Error -> {
                when (fetchPosts.error) {
                    NetworkError.INTERNAL_SERVER_ERROR -> TODO()
                    NetworkError.NO_INTERNET -> TODO()
                    NetworkError.UNKNOWN_ERROR -> TODO()
                }
            }

            is Result.Success -> {
                posts.clear()
                fetchPosts.data.body()?.forEach { item -> posts.add(item) }
            }
        }
    }

    init {
        viewModelScope.launch {
            refreshPosts();
        }
    }
}