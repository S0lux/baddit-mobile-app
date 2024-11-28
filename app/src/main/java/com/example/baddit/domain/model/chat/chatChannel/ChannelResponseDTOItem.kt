package com.example.baddit.domain.model.chat.chatChannel

import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.MutableCommunityResponseDTOItem

data class ChannelResponseDTOItem(
    val id: String,
    val name: String,
    val members: List<ChatMember>,
    val createdAt: String,
)

data class MutableChannelResponseDTOItem(
    val id: String,
    val name: String,
    val members: List<ChatMember>,
    val createdAt: String
)



fun ChannelResponseDTOItem.toMutableChannelResponseDTOItem(): MutableChannelResponseDTOItem {
    return MutableChannelResponseDTOItem(
       id,name, members, createdAt
    )
}