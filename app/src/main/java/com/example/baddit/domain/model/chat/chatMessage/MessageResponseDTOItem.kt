package com.example.baddit.domain.model.chat.chatMessage

import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.MutableChannelResponseDTOItem

data class MessageResponseDTOItem(
    val id: String,
    val sender: Sender,
    val content: String,
    val type:String,
    val createdAt: String
)

data class MutableMessageResponseDTOItem(
    val id: String,
    val sender: Sender,
    val content: String,
    val type:String,
    val createdAt: String
)

fun MessageResponseDTOItem.toMutableMessageResponseDTOItem(): MutableMessageResponseDTOItem {
    return MutableMessageResponseDTOItem(
       id, sender, content,type,createdAt
    )
}