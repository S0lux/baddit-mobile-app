package com.example.baddit.data.repository

import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.UserRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : UserRepository {
    override suspend fun updateAvatar(imageFile: File): Result<Unit, DataError.NetworkError> {
        val requestBody = MultipartBody.Part.createFormData(
            "avatar",
            imageFile.name,
            imageFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
        )
        return safeApiCall { badditAPI.updateAvatar( requestBody)}
    }

}