package com.example.baddit.presentation.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.data.model.posts.PostDTOItem
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
        val fetchedPosts = _postRepository.getPosts(null, null, null, null);

        posts.clear()
        fetchedPosts.body()?.forEach { it ->
            posts.add(it);
        }
    }

    init {
        viewModelScope.launch {
            refreshPosts();
        }
    }
}