package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.CommentResponseDTO

interface CommentRepository {
    suspend fun getComments(
        postId: String?=null,
        commentId: String? = null,
        authorName: String? = null,
        cursor: String? = null
    ): Result<CommentResponseDTO, DataError.NetworkError>

    suspend fun voteComment(
        commentId: String,
        state: String
    ): Result<Unit, DataError.NetworkError>

    suspend fun replyPost(
        postId: String,
        content: String
    ): Result<Unit, DataError.NetworkError>

    suspend fun replyComment(
        parentId: String,
        postId: String,
        content: String
    ): Result<Unit, DataError.NetworkError>

    suspend fun editComment(
        commentId: String,
        content: String,
    ): Result<Unit, DataError.NetworkError>

    suspend fun deleteComment(
        commentId: String
    ): Result<Unit, DataError.NetworkError>
}