package com.example.baddit.data.dto.notification

import com.google.gson.annotations.SerializedName

data class NotificationPayloadBase(
    @SerializedName("title")
    val title: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("typeId")
    val typeId: String?,
)

