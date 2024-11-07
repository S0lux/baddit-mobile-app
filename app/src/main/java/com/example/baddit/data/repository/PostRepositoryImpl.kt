package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.posts.PostEditRequestBody
import com.example.baddit.data.dto.posts.VotePostRequestBody
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.MutablePostResponseDTOItem
import com.example.baddit.domain.model.posts.toMutablePostResponseDTOItem
import com.example.baddit.domain.repository.PostRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class PostRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
) : PostRepository {

    override var postCache: SnapshotStateList<MutablePostResponseDTOItem> = mutableStateListOf()

    override suspend fun getPosts(
        communityName: String?,
        authorName: String?,
        cursor: String?,
        postTitle: String?
    ): Result<PostResponseDTO, DataError.NetworkError> {
        val result = safeApiCall<PostResponseDTO, DataError.NetworkError> {
            badditAPI.getPosts(
                communityName = communityName,
                authorName = authorName,
                cursor = cursor,
                postTitle = postTitle
            )
        }

        return result
    }

    override suspend fun getPost(postId: String): Result<PostResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getPost(postId) }
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

        return safeApiCall {
            badditAPI.upLoadPost(
                title = title.toRequestBody("text/plain".toMediaTypeOrNull()),
                content = content.toRequestBody("text/plain".toMediaTypeOrNull()),
                communityName = communityName.toRequestBody("text/plain".toMediaTypeOrNull()),
                type = type.toRequestBody("text/plain".toMediaTypeOrNull()),
                image = prepareFilePart("files", image)
            )
        }
    }

    override suspend fun editPost(
        postId: String,
        content: String
    ): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> {
            badditAPI.editPost(
                postId = postId,
                content = PostEditRequestBody(content = content)
            )
        }

        if (result is Result.Success) {
            postCache.find { it.id == postId }?.content?.value = content
        }

        return result
    }

    override suspend fun deletePost(postId: String): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.deletePost(postId = postId) }

        if (result is Result.Success) {
            postCache.removeIf { it.id == postId }
        }

        return result
    }

    private fun prepareFilePart(partName: String, file: File?): MultipartBody.Part? {
        return file?.let {
            val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, it.name, requestFile)
        }
    }
}