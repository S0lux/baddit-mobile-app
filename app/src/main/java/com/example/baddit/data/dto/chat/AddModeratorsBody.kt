package com.example.baddit.data.dto.chat

data class AddModeratorsBody(
    val channelId: String,
    val moderatorIds: List<String>
)
