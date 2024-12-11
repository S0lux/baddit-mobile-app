package com.example.baddit.data.dto.chat

data class AddMembersBody(
    val channelId: String,
    val memberIds: List<String>
)
