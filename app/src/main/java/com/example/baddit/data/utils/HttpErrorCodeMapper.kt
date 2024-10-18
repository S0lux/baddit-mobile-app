package com.example.baddit.data.utils

import com.example.baddit.domain.error.DataError

fun httpToError(errCode: Int): DataError.NetworkError {
    return when (errCode) {
        401 -> DataError.NetworkError.UNAUTHORIZED
        409 -> DataError.NetworkError.CONFLICT
        500 -> DataError.NetworkError.INTERNAL_SERVER_ERROR
        else -> DataError.NetworkError.UNKNOWN_ERROR
    }
}