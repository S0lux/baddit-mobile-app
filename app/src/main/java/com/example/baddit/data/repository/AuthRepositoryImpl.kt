package com.example.baddit.data.repository

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.example.baddit.data.dto.ErrorResponse
import com.example.baddit.data.dto.auth.LoginRequestBody
import com.example.baddit.data.dto.auth.RegisterRequestBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : AuthRepository {

    override val isLoggedIn: MutableState<Boolean> = mutableStateOf(false)

    override var currentUser: GetMeResponseDTO? = null
        private set;

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getMe()
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<LoginResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.login(LoginRequestBody(username, password)) }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Result<Unit, DataError.RegisterError> {
        return safeApiCall(
            apiCall = { badditAPI.signup(RegisterRequestBody(email, username, password)) },
            errorHandler = { response ->
                val errorCode = response.code()
                val errorBody = response.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                if (errorCode == 409) // Means there's a username/email conflict
                    when (errorResponse.error) {
                        "USERNAME_TAKEN" -> Result.Error(DataError.RegisterError.USERNAME_TAKEN)
                        "EMAIL_TAKEN" -> Result.Error(DataError.RegisterError.EMAIL_TAKEN)
                        else -> Result.Error(DataError.RegisterError.UNKNOWN_ERROR)
                    }
                else when (errorCode) {
                    500 -> Result.Error(DataError.RegisterError.INTERNAL_SERVER_ERROR)
                    else -> Result.Error(DataError.RegisterError.UNKNOWN_ERROR)
                }
            }
        )
    }

    override suspend fun getMe(): Result<GetMeResponseDTO, DataError.NetworkError> {
        val result = safeApiCall<GetMeResponseDTO, DataError.NetworkError> { badditAPI.getMe() }
        if (result is Result.Success) {
            isLoggedIn.value = true;
            currentUser = result.data;
        }
        return result;
    }
}