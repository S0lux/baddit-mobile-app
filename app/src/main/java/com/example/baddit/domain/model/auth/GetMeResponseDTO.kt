package com.example.baddit.domain.model.auth

data class GetMeResponseDTO(
    val avatarUrl: String,
    val communities: List<Community>,
    val email: String,
    val emailVerified: Boolean,
    val id: String,
    val registeredAt: String,
    val role: String,
    val username: String
)