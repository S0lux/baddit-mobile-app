package com.example.baddit.domain.model.posts

data class PostResponseDTOItem(
    val author: Author,
    val commentCount: Int,
    val community: Community,
    val content: String,
    val createdAt: String,
    val id: String,
    val score: Int,
    val title: String,
    val type: String,
    val updatedAt: String,
    val voteState: Any
)