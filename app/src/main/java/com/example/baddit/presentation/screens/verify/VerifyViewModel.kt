package com.example.baddit.presentation.screens.verify

import androidx.lifecycle.ViewModel
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val _authRepository: AuthRepository
) : ViewModel() {
}