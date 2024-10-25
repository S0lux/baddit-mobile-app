package com.example.baddit.data.utils

import com.example.baddit.domain.error.DataError

fun httpToError(errCode: Int): DataError.NetworkError {
    return when (errCode) {
        401 -> DataError.NetworkError.UNAUTHORIZED
        403 -> DataError.NetworkError.FORBIDDEN
        409 -> DataError.NetworkError.CONFLICT
        498 -> DataError.NetworkError.TOKEN_INVALID
        500 -> DataError.NetworkError.INTERNAL_SERVER_ERROR
        else -> DataError.NetworkError.UNKNOWN_ERROR
    }
}