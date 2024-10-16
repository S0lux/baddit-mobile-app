package com.example.baddit.domain.repository

import com.example.baddit.data.model.posts.PostDTO
import retrofit2.Response

interface PostRepository {
    suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Response<PostDTO>;
}