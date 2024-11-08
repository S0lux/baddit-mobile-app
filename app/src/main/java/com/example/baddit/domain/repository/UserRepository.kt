package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import java.io.File

interface UserRepository {
    suspend fun updateAvatar(imageFile: File): Result<Unit, DataError.NetworkError>
}