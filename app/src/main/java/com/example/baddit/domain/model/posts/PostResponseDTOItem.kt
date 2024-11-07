package com.example.baddit.domain.model.posts

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable

@Serializable
data class PostResponseDTOItem(
    val author: Author,
    val commentCount: Int,
    val community: Community?,
    val content: String,
    val createdAt: String,
    val id: String,
    val score: Int,
    val title: String,
    val type: String,
    val updatedAt: String,
    val voteState: String?,
    val mediaUrls: List<String>
)

data class MutablePostResponseDTOItem(
    val author: Author,
    val commentCount: MutableState<Int>,
    val community: Community?,
    val content: MutableState<String>,
    val createdAt: String,
    val id: String,
    val score: MutableState<Int>,
    val title: String,
    val type: String,
    val updatedAt: String,
    val voteState: MutableState<String?>,
    val mediaUrls: List<String>
)

fun PostResponseDTOItem.toMutablePostResponseDTOItem(): MutablePostResponseDTOItem {
    return MutablePostResponseDTOItem(
        author = this.author,
        commentCount = mutableIntStateOf(this.commentCount),
        community = this.community,
        content = mutableStateOf(this.content),
        createdAt = this.createdAt,
        id = this.id,
        score = mutableIntStateOf(this.score),
        title = this.title,
        type = this.type,
        updatedAt = this.updatedAt,
        voteState = mutableStateOf(this.voteState),
        mediaUrls = this.mediaUrls
    )
}