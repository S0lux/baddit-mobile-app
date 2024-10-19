@file:Suppress("UNCHECKED_CAST")

package com.example.baddit.data.utils

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import retrofit2.Response
import java.io.IOException

suspend fun <T, E: DataError> safeApiCall(
    errorHandler: ((Response<T>) -> Result.Error<T, E>)? = null,
    apiCall: suspend () -> Response<T>,
): Result<T, E> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            Result.Success(response.body()!!)
        } else {
            errorHandler?.invoke(response) ?: Result.Error(httpToError(response.code()) as E)
        }
    } catch (err: IOException) {
        Result.Error(DataError.NetworkError.NO_INTERNET as E)
    }
}
