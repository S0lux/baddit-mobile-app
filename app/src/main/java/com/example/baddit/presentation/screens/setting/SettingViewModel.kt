package com.example.baddit.presentation.screens.setting

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.presentation.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel(){

    val auth = authRepository;

    val error = mutableStateOf("");

    var oldPasswordState by mutableStateOf(FieldState())
        private set
    var newPasswordState by mutableStateOf(FieldState())
        private set
    var confirmPasswordState by mutableStateOf(FieldState())
        private set

    var newPassword = mutableStateOf(FieldState());
    var checkPassword= mutableStateOf(FieldState());
    fun setOldPassword(content: String){
        oldPasswordState = oldPasswordState.copy(value = content, error = validatePassword(content))
    }

    private fun validatePassword(input: String): String {
        return if (input.length < 6) "Password is too short." else ""
    }

    private fun validateConfirmPassword(password: String, confirmPassword: String): String {
        return if (password != confirmPassword) "Password mismatch." else ""
    }

    fun setNewPassword(content: String){
        newPasswordState = newPasswordState.copy(value = content, error = validatePassword(content))
    }

    fun setConfirmPassword(content: String){
        confirmPasswordState = confirmPasswordState.copy(value = content, error = validateConfirmPassword(newPasswordState.value, content))
    }

    var isLoading by mutableStateOf(false)
        private set

    suspend fun ChangePassword(): Result<Unit, DataError.NetworkError>{
        isLoading = true;

        var result = authRepository.changePassword(oldPasswordState.value, newPasswordState.value);

        if(result is Result.Error){
            error.value = when (result.error) {
                DataError.NetworkError.NO_INTERNET -> "No internet connection."
                DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                DataError.NetworkError.UNAUTHORIZED -> "Wrong password"
                DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred."
                else -> "This error shouldn't happen unless something changed in the backend."
            }
        }

        isLoading = false;

        return result;
    }

}