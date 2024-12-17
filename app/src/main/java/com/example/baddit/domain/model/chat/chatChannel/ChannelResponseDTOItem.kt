package com.example.baddit.domain.model.chat.chatChannel

import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.MutableCommunityResponseDTOItem

data class ChannelResponseDTOItem(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val members: List<ChatMember>,
    val moderators: List<ChatModerators>,
    val createdAt: String,
    val type: String,
    val isDeleted: Boolean
)

data class MutableChannelResponseDTOItem(
    val id: String,
    val name: String,
    val avatarUrl: String,
    val members: List<ChatMember>,
    val moderators: List<ChatModerators>,
    val createdAt: String,
    val type: String,
    val isDeleted: Boolean
)


fun ChannelResponseDTOItem.toMutableChannelResponseDTOItem(): MutableChannelResponseDTOItem {
    return MutableChannelResponseDTOItem(
        id, name, avatarUrl, members, moderators, createdAt, type, isDeleted
    )
}