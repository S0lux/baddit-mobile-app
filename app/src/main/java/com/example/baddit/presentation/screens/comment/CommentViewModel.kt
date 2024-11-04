package com.example.baddit.presentation.screens.comment

import androidx.lifecycle.ViewModel
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    postRepository: PostRepository,
    commentRepository: CommentRepository,
    authRepository: AuthRepository
) : ViewModel() {

}