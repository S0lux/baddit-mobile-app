package com.example.baddit.data.repository

import com.example.baddit.data.model.posts.PostDTO
import com.example.baddit.data.remote.PostAPI
import com.example.baddit.domain.repository.PostRepository
import retrofit2.Response
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val PostAPI: PostAPI
) : PostRepository {

    override suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Response<PostDTO> {
        return PostAPI.getPosts(communityName, authorName, cursor, postTitle);
    }
}