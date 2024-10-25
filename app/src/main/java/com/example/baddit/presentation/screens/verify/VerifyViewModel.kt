package com.example.baddit.presentation.screens.verify

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.R
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VerifyViewModel @Inject constructor(
    private val _authRepository: AuthRepository
) : ViewModel() {
    var title by mutableStateOf("Verifying...")
        private set;

    var description by mutableStateOf("Please wait while we verify your email address.")
        private set;

    var displayLogoState by mutableStateOf("Loading")
        private set;

    var isVerifySuccess by mutableStateOf(false)
        private set;

    fun verifyEmail(token: String?) {
        viewModelScope.launch {
            val result = if (token == null) {
                Result.Error(DataError.NetworkError.TOKEN_INVALID)
            } else _authRepository.verifyEmail(token)

            // Artificial delay
            delay(2000)

            when (result) {
                is Result.Error -> {
                    when (result.error) {
                        DataError.NetworkError.NO_INTERNET -> {
                            title = "No connection"
                            description = "Please check your internet connection."
                            displayLogoState = "Error"
                        }
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> {
                            title = "Oops"
                            description = "Something went wrong and it's not your fault."
                            displayLogoState = "Error"
                        }
                        DataError.NetworkError.UNAUTHORIZED -> {
                            title = "Hmmm"
                            description = "Something went wrong and it's not your fault."
                            displayLogoState = "Error"
                        }
                        DataError.NetworkError.FORBIDDEN -> {
                            title = "Hmmm"
                            description = "Something went wrong and it's not your fault."
                            displayLogoState = "Error"
                        }
                        DataError.NetworkError.CONFLICT -> {
                            title = "Hmmm"
                            description = "Something went wrong and it's not your fault."
                            displayLogoState = "Error"
                        }
                        DataError.NetworkError.UNKNOWN_ERROR -> {
                            title = "Hmmm"
                            description = "Something went wrong and it's not your fault."
                            displayLogoState = "Error"
                        }

                        DataError.NetworkError.TOKEN_INVALID -> {
                            title = "Invalid"
                            description = "We are unable to verify your token. Perhaps it is expired?"
                            displayLogoState = "Error"
                        }
                    }
                }

                is Result.Success -> {
                    title = "Verified"
                    description = "Your account is now verified. Please proceed to login to use our services."
                    displayLogoState = "Success"
                    isVerifySuccess = true
                }
            }
        }
    }
}