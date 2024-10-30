package com.example.baddit.data.repository

import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.CommentResponseDTO
import com.example.baddit.domain.repository.CommentRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI
): CommentRepository {
    override suspend fun getComments(
        postId: String?,
        commentId: String?,
        authorName: String?,
        cursor: String?
    ): Result<CommentResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getComments(postId, commentId, authorName, cursor) }
    }
}