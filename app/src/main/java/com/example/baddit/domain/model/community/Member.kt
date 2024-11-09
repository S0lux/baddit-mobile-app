package com.example.baddit.domain.model.community

data class Member(
    val userId: String,
    val username: String,
    val avatarUrl: String,
    val communityRole: String,
    val joined: Boolean,
    val banned: Boolean
)