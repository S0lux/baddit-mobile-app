package com.example.baddit.data.repository

import com.example.baddit.data.mapper.httpToError
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.data.remote.BadditAPI
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
    ): Result<Response<PostResponseDTO>, DataError.NetworkError> {
        return try {
            val response = badditAPI.getPosts(communityName, authorName, cursor, postTitle);
            return Result.Success(response);
        }

        catch (err: IOException) {
            return Result.Error(DataError.NetworkError.NO_INTERNET)
        }

        catch (err: HttpException) {
            Result.Error(httpToError(err.code()))
        }
    }
}