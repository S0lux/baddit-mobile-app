package com.example.baddit.presentation.screens.createPost

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.CommunityDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePostViewodel @Inject constructor(private val auth: AuthRepository, private val community: CommunityRepository) : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var link by mutableStateOf("")
    var selectedCommunity by mutableStateOf("")
    var linkTypeSelected by mutableStateOf(false)
    var selectedImage by mutableStateOf<Uri?>(null)
    var res by mutableStateOf("")
    val isLoggedIn = auth.isLoggedIn

    var communities = mutableListOf<CommunityDTO>()

    init {
        viewModelScope.launch(context = Dispatchers.IO) {
            when (val res = community.getCommunity(name = "")) {
                is Result.Error -> {
                    Log.d("test", "has error")
                }

                is Result.Success -> {
                    communities.addAll(res.data)
                }
            }
            Log.d("test", communities.toString())
        }
    }
}