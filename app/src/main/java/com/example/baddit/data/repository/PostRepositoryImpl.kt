package com.example.baddit.data.repository

import com.example.baddit.data.dto.posts.VotePostRequestBody
import com.example.baddit.data.utils.httpToError
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.PostRepository
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : PostRepository {

    override suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Result<PostResponseDTO, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.getPosts(
                communityName = communityName,
                authorName = authorName,
                cursor = cursor,
                postTitle = postTitle
            )
        }
    }

    override suspend fun votePost(
        postId: String,
        voteState: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.votePost(postId, VotePostRequestBody(voteState)) }
    }
}