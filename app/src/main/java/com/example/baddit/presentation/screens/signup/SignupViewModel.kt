package com.example.baddit.presentation.screens.signup

import androidx.compose.runtime.*
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

    // State data classes for grouping related states together
    data class FieldState(
        var value: String = "", var error: String = ""
    )

    var emailState by mutableStateOf(FieldState())
        private set

    var usernameState by mutableStateOf(FieldState())
        private set

    var passwordState by mutableStateOf(FieldState())
        private set

    var confirmPasswordState by mutableStateOf(FieldState())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var isSignupDone by mutableStateOf(false)
        private set

    // Validation methods
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()
        return email.matches(emailRegex)
    }

    private fun validateEmail(input: String): String {
        return if (!isValidEmail(input)) "Please enter a valid email." else ""
    }

    private fun validatePassword(input: String): String {
        return if (input.length < 6) "Password is too short." else ""
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String {
        return if (password != confirmPassword) "Password mismatch." else ""
    }

    // State update methods
    fun setUsername(input: String) {
        usernameState = usernameState.copy(value = input, error = "")
    }

    fun setEmail(input: String) {
        emailState = emailState.copy(
            value = input, error = validateEmail(input)
        )
    }

    fun setPassword(input: String) {
        passwordState = passwordState.copy(
            value = input, error = validatePassword(input)
        )
    }

    fun setConfirmationPassword(input: String) {
        confirmPasswordState = confirmPasswordState.copy(
            value = input, error = validateConfirmPassword(passwordState.value, input)
        )
    }

    // Error handling method
    private fun handleRegisterError(error: DataError.RegisterError) {
        when (error) {
            DataError.RegisterError.USERNAME_TAKEN -> {
                usernameState = usernameState.copy(error = "This username is already taken.")
            }

            DataError.RegisterError.EMAIL_TAKEN -> {
                emailState = emailState.copy(error = "This email is already in use.")
            }

            DataError.RegisterError.NO_INTERNET,
            DataError.RegisterError.INTERNAL_SERVER_ERROR,
            DataError.RegisterError.UNKNOWN_ERROR -> {
                // Handle other errors
            }
        }
        isLoading = false
    }

    suspend fun trySignUp(): Result<Unit, DataError.RegisterError> {
        isLoading = true

        val result =
            authRepository.register(emailState.value, usernameState.value, passwordState.value)

        when (result) {
            is Result.Error -> handleRegisterError(result.error)
            is Result.Success -> {
                isLoading = false
                isSignupDone = true
            }
        }

        return result;
    }
}
