package com.example.baddit.data.dto.comment

data class CommentCommentRequestBody(
    val content: String,
    val parentId: String,
    val postId: String
)