package com.example.baddit.domain.model.community

data class CommunityDTO(
    val id: String,
    val name: String,
    val ownerId: String,
    val description: String,
    val logoUrl: String,
    val bannerUrl: String,
    val status: String,
    val memberCount: Int,
    val deleted: Boolean,
    val createdAt: String,
    val updateAt: String
)
