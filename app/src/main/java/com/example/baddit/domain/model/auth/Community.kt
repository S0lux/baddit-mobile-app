package com.example.baddit.domain.model.auth

data class Community(
    val banned: Boolean,
    val id: String,
    val name: String,
    val role: String
)