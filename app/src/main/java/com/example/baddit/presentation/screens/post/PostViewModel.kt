package com.example.baddit.presentation.screens.post

import androidx.lifecycle.ViewModel
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val _authRepository: AuthRepository
) : ViewModel() {
}