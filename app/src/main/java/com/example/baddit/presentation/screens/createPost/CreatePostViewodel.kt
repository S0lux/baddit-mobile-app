package com.example.baddit.presentation.screens.createPost

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewodel @Inject constructor(private val auth: AuthRepository) : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var link by mutableStateOf("")
    var linkTypeSelected by mutableStateOf(false)
    var selectedImage by mutableStateOf<Uri?>(null)
    var res by mutableStateOf("")
    val isLoggedIn = auth.isLoggedIn

}