package com.example.baddit.data.dto.comment

data class PostCommentRequestBody(
    val content: String,
    val postId: String
)