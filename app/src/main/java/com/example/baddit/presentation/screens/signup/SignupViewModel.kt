package com.example.baddit.presentation.screens.signup

import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    var emailField by mutableStateOf("")
        private set;

    var emailFieldError by mutableStateOf("")
        private set;

    var usernameField by mutableStateOf("")
        private set;

    var usernameFieldError by mutableStateOf("")
        private set;

    var passwordField by mutableStateOf("")
        private set;

    var passwordFieldError by mutableStateOf("")
        private set;

    var confirmPasswordField by mutableStateOf("")
        private set;

    var confirmPasswordFieldError by mutableStateOf("")
        private set;

    var isLoading by mutableStateOf(false)
        private set;

    var isSignupDone by mutableStateOf(false)
        private set;

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailRegex)
    }

    fun setUsername(input: String) {
        usernameField = input
    }

    fun setEmail(input: String) {
        emailField = input
        emailFieldError = if (!isValidEmail(emailField)) "Please enter a valid email."
        else ""
    }

    fun setPassword(input: String) {
        passwordField = input
        passwordFieldError = if (passwordField.length < 6) "Password is too short." else ""
    }

    fun setConfirmationPassword(input: String) {
        confirmPasswordField = input
        confirmPasswordFieldError =
            if (confirmPasswordField != passwordField) "Password mismatch." else ""
    }

    suspend fun trySignUp(): Boolean {
        isLoading = true;

        when (val result =
            authRepository.register(emailField, usernameField, passwordField)) {
            is Result.Error -> {
                when (result.error) {
                    DataError.RegisterError.USERNAME_TAKEN -> {
                        isLoading = false;
                        usernameFieldError = "This username is already taken."
                        return false;
                    }

                    DataError.RegisterError.EMAIL_TAKEN -> {
                        isLoading = false;
                        emailFieldError = "This email is already in use."
                        return false;
                    }

                    DataError.RegisterError.NO_INTERNET -> {
                        isLoading = false;
                        return false;
                    }

                    DataError.RegisterError.INTERNAL_SERVER_ERROR -> {
                        isLoading = false;
                        return false;
                    }

                    DataError.RegisterError.UNKNOWN_ERROR -> {
                        isLoading = false;
                        return false;
                    }
                }
            }

            is Result.Success -> {
                isLoading = false;
                isSignupDone = true;
                return true;
            }
        }
    }
}