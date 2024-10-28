package com.example.baddit.domain.model.posts

import kotlinx.serialization.Serializable

@Serializable
data class Author(
    val avatarUrl: String,
    val id: String,
    val username: String
)