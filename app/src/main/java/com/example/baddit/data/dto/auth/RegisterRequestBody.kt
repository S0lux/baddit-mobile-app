package com.example.baddit.data.dto.auth

data class RegisterRequestBody(
    val email: String,
    val username: String,
    val password: String,
)
