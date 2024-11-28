package com.example.baddit.domain.model.friend

data class IncomingFriendRequestDto(
    val createdAt: String,
    val id: String,
    val sender: BaseFriendUser,
    val receiverId: String,
    val senderId: String,
    val status: String
)