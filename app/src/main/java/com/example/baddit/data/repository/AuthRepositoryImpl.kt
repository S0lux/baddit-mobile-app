package com.example.baddit.data.repository

import android.util.Log
import com.example.baddit.data.dto.ErrorResponse
import com.example.baddit.data.dto.auth.LoginRequestBody
import com.example.baddit.data.dto.auth.RegisterRequestBody
import com.example.baddit.data.mapper.httpToError
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.LoginResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    val badditAPI: BadditAPI
) : AuthRepository {

    var isLoggedIn: Boolean = false
        get() {
            return currentUser?.id != null
        }
        private set

    var currentUser: GetMeResponseDTO? = null
        private set;

    init {
        CoroutineScope(Dispatchers.IO).launch {
            getMe()
        }
    }

    override suspend fun login(
        username: String,
        password: String
    ): Result<Response<LoginResponseDTO>, DataError.NetworkError> {
        return try {
            val response = badditAPI.login(LoginRequestBody(username, password))
            if (!response.isSuccessful) throw HttpException(response)
            Result.Success(response)
        } catch (err: IOException) {
            Result.Error(DataError.NetworkError.NO_INTERNET)
        } catch (err: HttpException) {
            Result.Error(httpToError(err.code()))
        }
    }

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Result<Unit, DataError.RegisterError> {
        return try {
            val response = badditAPI.signup(RegisterRequestBody(email, username, password));
            if (!response.isSuccessful) {
                val errorBody =
                    Gson().fromJson(response.errorBody()!!.string(), ErrorResponse::class.java)
                return when (errorBody.error) {
                    "USERNAME_TAKEN" -> Result.Error(DataError.RegisterError.USERNAME_TAKEN)
                    "EMAIL_TAKEN" -> Result.Error(DataError.RegisterError.EMAIL_TAKEN)
                    else -> throw HttpException(response)
                }
            }
            Result.Success(Unit)
        } catch (err: IOException) {
            Result.Error(DataError.RegisterError.NO_INTERNET)
        } catch (err: HttpException) {
            Log.d("REGISTER", "Http catch hit!")
            when (err.code()) {
                500 -> Result.Error(DataError.RegisterError.INTERNAL_SERVER_ERROR)
                else -> Result.Error(DataError.RegisterError.UNKNOWN_ERROR)
            }
        }
    }

    override suspend fun getMe(): Result<Response<GetMeResponseDTO>, DataError.NetworkError> {
        return try {
            val response = badditAPI.getMe();
            if (!response.isSuccessful) throw HttpException(response)
            currentUser = response.body()
            Result.Success(response);
        } catch (err: IOException) {
            Result.Error(DataError.NetworkError.NO_INTERNET)
        } catch (err: HttpException) {
            Result.Error(httpToError(err.code()))
        }
    }

}