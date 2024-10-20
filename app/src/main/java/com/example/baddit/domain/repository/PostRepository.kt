package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTO
import retrofit2.Response

interface PostRepository {
    suspend fun getPosts(
        communityName: String? = null,
        authorName: String? = null,
        cursor: String? = null,
        postTitle: String? = null
    ): Result<PostResponseDTO, DataError.NetworkError>;

    suspend fun votePost(
        postId: String,
        voteState: String
    ): Result<Unit, DataError.NetworkError>
}