package com.example.baddit.domain.model.posts

import kotlinx.serialization.Serializable

@Serializable
data class Community(
    val logoUrl: String,
    val name: String
)