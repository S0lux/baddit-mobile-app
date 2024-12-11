package com.example.baddit.data.dto.chat

data class CreateChannelBody(
    val channelName: String,
    val memberIds: List<String>
)