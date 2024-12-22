package com.example.baddit.data.repository

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.lifecycleScope
import com.example.baddit.data.dto.ErrorResponse
import com.example.baddit.data.dto.auth.ChangePasswordRequestBody
import com.example.baddit.data.dto.auth.EmailVerificationRequestBody
import com.example.baddit.data.dto.auth.LoginRequestBody
import com.example.baddit.data.dto.auth.RegisterRequestBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.FriendRepository
import com.example.baddit.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI,
    private val friendRepository: Lazy<FriendRepository>,
    private val notificationRepository: Lazy<NotificationRepository>
) : AuthRepository {

    override val isLoggedIn: MutableState<Boolean> = mutableStateOf(false)

    override val currentUser: MutableState<GetMeResponseDTO?> = mutableStateOf(null)

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getMe()
            if (isLoggedIn.value) friendRepository.get().updateLocalUserFriend()
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<LoginResponseDTO, DataError.NetworkError> {
        val loginResult = safeApiCall<LoginResponseDTO, DataError.NetworkError> {
            badditAPI.login(LoginRequestBody(username, password))
        }

        if (loginResult is Result.Success) {
            try {
                val token = suspendCancellableCoroutine<String> { continuation ->
                    FirebaseMessaging.getInstance().token
                        .addOnSuccessListener { token ->
                            continuation.resume(token)
                        }
                        .addOnFailureListener { exception ->
                            continuation.resumeWithException(exception)
                        }
                }

                // Send token to server
                when (val tokenResult = notificationRepository.get().sendFcmTokenToServer(token)) {
                    is Result.Success -> {
                        Log.d("Activity", "FCM token sent successfully")
                    }
                    is Result.Error -> {
                        Log.e("Activity", "Failed to send FCM token: ${tokenResult.error}")
                    }
                }
            } catch (e: Exception) {
                Log.e("Activity", "Failed to get/send FCM token", e)
            }
        }

        return loginResult
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
            if (result.data.status == "SUSPENDED") {
                isLoggedIn.value = false;
                currentUser.value = null;
                return Result.Error(DataError.NetworkError.FORBIDDEN)
            }
            isLoggedIn.value = true;
            currentUser.value = result.data;
        }

        if (result is Result.Error) {
            isLoggedIn.value = false;
            currentUser.value = null;
            return Result.Error(result.error)
        }

        Log.d("GetMe", "getMe: ${currentUser.value?.username ?: "null"}")
        return result;
    }

    override suspend fun verifyEmail(token: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.verify(EmailVerificationRequestBody(token)) }
    }

    override suspend fun getOther(username: String): Result<GetOtherResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getOther(username) }
    }

    override suspend fun logout(): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.logout() }

        if (result is Result.Success) {
            CoroutineScope(Dispatchers.IO).launch {
                isLoggedIn.value = false;
                currentUser.value = null;
                getMe()
            }
        }

        return result
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): Result<Unit, DataError.NetworkError> {

        val body = ChangePasswordRequestBody(oldPassword, newPassword)

        return safeApiCall { badditAPI.changePassword(body)}
    }

}