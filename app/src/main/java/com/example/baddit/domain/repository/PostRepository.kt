package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.MutablePostResponseDTOItem
import com.example.baddit.domain.model.posts.PostResponseDTO
import java.io.File

interface PostRepository {
    suspend fun getPosts(
        communityName: String? = null,
        authorName: String? = null,
        cursor: String? = null,
        postTitle: String? = null,
        orderByScore: String? = null
    ): Result<PostResponseDTO, DataError.NetworkError>;

    var postCache: SnapshotStateList<MutablePostResponseDTOItem>

    suspend fun getPost(
        postId: String
    ): Result<PostResponseDTO, DataError.NetworkError>

    suspend fun votePost(
        postId: String,
        voteState: String
    ): Result<Unit, DataError.NetworkError>

    suspend fun upLoadPost(
        title: String,
        content: String,
        type: String,
        communityName: String?,
        image: File?
    ): Result<Unit, DataError.NetworkError>

    suspend fun editPost(
        postId: String,
        content: String,
    ): Result<Unit, DataError.NetworkError>

    suspend fun deletePost(
        postId: String
    ): Result<Unit, DataError.NetworkError>
}