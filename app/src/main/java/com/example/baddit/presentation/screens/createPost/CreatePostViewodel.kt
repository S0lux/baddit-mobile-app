package com.example.baddit.presentation.screens.createPost

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.baddit.data.repository.AuthRepositoryImpl
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreatePostViewodel @Inject constructor(private val auth: AuthRepositoryImpl) : ViewModel() {
    var title by mutableStateOf("")
    var content by mutableStateOf("")
    var isLoggedIn by mutableStateOf(auth.isLoggedIn)


}