package com.example.baddit.data.dto.auth

data class ChangePasswordRequestBody(
    val oldPassword: String,
    val newPassword: String,
)
