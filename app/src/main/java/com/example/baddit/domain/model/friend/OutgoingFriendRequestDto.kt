package com.example.baddit.domain.model.friend

data class OutgoingFriendRequestDto (
    val createdAt: String,
    val id: String,
    val receiver: BaseFriendUser,
    val receiverId: String,
    val senderId: String,
    val status: String
)