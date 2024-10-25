package com.example.baddit.presentation.screens.login

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.presentation.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var usernameState by mutableStateOf(FieldState())
        private set;

    var passwordState by mutableStateOf(FieldState())
        private set;

    var generalError by mutableStateOf("")
        private set;

    val loggedIn = authRepository.isLoggedIn;
    val currentUser = authRepository.currentUser;

    fun onUsernameChange(input: String) {
        usernameState = usernameState.copy(value = input, error = "")
    }

    fun onPasswordChange(input: String) {
        passwordState = passwordState.copy(value = input, error = "")
    }

    suspend fun login(): Result<LoginResponseDTO, DataError.NetworkError> {
        val result =
            authRepository.login(username = usernameState.value, password = passwordState.value)

        when (result) {
            is Result.Error -> {
                generalError = when (result.error) {
                    DataError.NetworkError.NO_INTERNET -> "No internet connection."
                    DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                    DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                    DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                    DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred."
                    DataError.NetworkError.FORBIDDEN -> "Email not verified."
                    else -> "This error shouldn't happen unless something changed in the backend."
                }
            }

            is Result.Success -> {
                generalError = ""
                authRepository.getMe()
            }
        }
        return result;
    }
}
