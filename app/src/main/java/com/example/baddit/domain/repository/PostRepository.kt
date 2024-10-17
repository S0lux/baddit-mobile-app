package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTO
import retrofit2.Response

interface PostRepository {
    suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Result<Response<PostResponseDTO>, DataError.NetworkError>;
}