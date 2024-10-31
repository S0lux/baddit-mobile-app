package com.example.baddit.data.repository

import com.example.baddit.data.dto.posts.UploadPostRequestBody
import com.example.baddit.data.dto.posts.VotePostRequestBody
import com.example.baddit.data.utils.httpToError
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.repository.PostRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.File
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
        return safeApiCall { badditAPI.getPosts() }
    }

    override suspend fun votePost(
        postId: String,
        voteState: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.votePost(postId, VotePostRequestBody(voteState)) }
    }

    override suspend fun upLoadPost(
        title: String,
        content: String,
        type: String,
        communityName: String,
        image: File?
    ): Result<Unit, DataError.NetworkError> {

        return safeApiCall { badditAPI.upLoadPost(
            title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
            content = content.toRequestBody("text/plain".toMediaTypeOrNull()),
            communityName = communityName.toRequestBody("text/plain".toMediaTypeOrNull()),
            type = type.toRequestBody("text/plain".toMediaTypeOrNull()),
            image = prepareFilePart("files", image)
        ) }
    }

    private fun prepareFilePart(partName: String, file: File?): MultipartBody.Part? {
        return file?.let {
            val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
        }
    }
}