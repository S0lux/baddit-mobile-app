package com.example.baddit.domain.model.chat.chatChannel

data class ChatMember(
    val id: String,
    val username: String,
    val avatarUrl: String
)

data class ChatModerators(
    val id: String,
    val username: String,
    val avatarUrl: String
)