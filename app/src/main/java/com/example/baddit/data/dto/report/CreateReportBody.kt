package com.example.baddit.data.dto.report

data class CreateReportBody(
    val type: String,
    val content: String,
    val reportedUserId: String?,
    val reportedPostId: String?,
)


