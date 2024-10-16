package com.example.baddit.domain.error.errors

enum class NetworkError: Error {
    NO_INTERNET,
    INTERNAL_SERVER_ERROR,
    UNKNOWN_ERROR,
}