package com.example.baddit.domain.model.auth

data class GetOtherResponseDTO (
    val avatarUrl: String,
    val id: String,
    val registeredAt: String,
    val username: String
)