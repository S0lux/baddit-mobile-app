package com.example.baddit.domain.error

sealed interface DataError: Error {
    enum class NetworkError: DataError {
        NO_INTERNET,
        INTERNAL_SERVER_ERROR,
        UNAUTHORIZED,
        FORBIDDEN,
        CONFLICT,
        TOKEN_INVALID,
        UNKNOWN_ERROR,
    }

    enum class RegisterError: DataError {
        USERNAME_TAKEN,
        EMAIL_TAKEN,
        NO_INTERNET,
        INTERNAL_SERVER_ERROR,
        UNKNOWN_ERROR,
    }

    enum class CreateCommunityError: DataError{
        NO_INTERNET,
        COMMUNITY_NAME_TAKEN,
        UNAUTHORIZED,
        INTERNAL_SERVER_ERROR,
        UNKNOWN_ERROR
    }

}