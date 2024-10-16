package com.example.baddit.data.repository

import com.example.baddit.domain.model.posts.PostDTO
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.error.errors.NetworkError
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
    ): Result<Response<PostDTO>, NetworkError> {
        return try {
            val response = badditAPI.getPosts(communityName, authorName, cursor, postTitle);
            return Result.Success(response);
        }

        catch (err: IOException) {
            return Result.Error(NetworkError.NO_INTERNET)
        }

        catch (err: HttpException) {
            when (err.code()) {
                500 -> Result.Error(NetworkError.INTERNAL_SERVER_ERROR)
                else -> Result.Error(NetworkError.UNKNOWN_ERROR)
            }
        }
    }
}