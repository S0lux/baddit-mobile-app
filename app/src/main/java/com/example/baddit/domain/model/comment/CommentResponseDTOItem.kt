package com.example.baddit.domain.model.comment

data class CommentResponseDTOItem(
    val author: Author,
    val authorId: String,
    val children: List<CommentResponseDTOItem>,
    val content: String,
    val createdAt: String,
    val deleted: Boolean,
    val id: String,
    val parentId: String?,
    val postId: String,
    val score: Int,
    val updatedAt: String,
    val voteState: String?
)