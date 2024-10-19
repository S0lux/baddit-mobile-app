package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.LoginResponseDTO
import retrofit2.Response

interface AuthRepository {
    suspend fun login(username: String, password: String): Result<LoginResponseDTO, DataError.NetworkError>
    suspend fun register(email: String, username: String, password: String): Result<Unit, DataError.RegisterError>
    suspend fun getMe(): Result<GetMeResponseDTO, DataError.NetworkError>
}