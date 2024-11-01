package com.example.baddit.data.dto.comment

data class VoteCommentRequestBody(
    val commentId: String,
    val state: String
)