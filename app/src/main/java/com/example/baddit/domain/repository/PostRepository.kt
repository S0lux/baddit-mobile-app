package com.example.baddit.domain.repository

import com.example.baddit.domain.error.Result
import com.example.baddit.domain.error.errors.NetworkError
import com.example.baddit.domain.model.posts.PostDTO
import retrofit2.Response

interface PostRepository {
    suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Result<Response<PostDTO>, NetworkError>;
}