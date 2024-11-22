package com.example.baddit.data.dto.notification

data class NotificationResponseItem(
    val createdAt: String,
    val id: String,
    var isRead: Boolean,
    val payload: NotificationPayloadBase,
    val type: String
)