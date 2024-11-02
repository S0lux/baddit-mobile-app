package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import retrofit2.Response
import java.io.File

interface PostRepository {
    suspend fun getPosts(
        communityName: String? = null,
        authorName: String? = null,
        cursor: String? = null,
        postTitle: String? = null
    ): Result<PostResponseDTO, DataError.NetworkError>;

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
        communityName: String,
        image: File?
    ): Result<Unit, DataError.NetworkError>
}