package com.example.baddit.data.repository

import com.example.baddit.data.dto.comment.CommentCommentRequestBody
import com.example.baddit.data.dto.comment.EditCommentRequestBody
import com.example.baddit.data.dto.comment.PostCommentRequestBody
import com.example.baddit.data.dto.comment.VoteCommentRequestBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.CommentResponseDTO
import com.example.baddit.domain.repository.CommentRepository
import com.example.baddit.domain.repository.PostRepository
import javax.inject.Inject

class CommentRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI,
    private val postRepository: PostRepository
) : CommentRepository {
    override suspend fun getComments(
        postId: String?,
        commentId: String?,
        authorName: String?,
        cursor: String?,
        orderByScore: String?
    ): Result<CommentResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.getComments(postId, commentId, authorName, cursor,orderByScore) }
    }

    override suspend fun voteComment(
        commentId: String,
        state: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.voteComment(VoteCommentRequestBody(commentId, state)) }
    }

    override suspend fun replyPost(
        postId: String,
        content: String
    ): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> {
            badditAPI.replyPost(
                PostCommentRequestBody(
                    content = content,
                    postId = postId
                )
            )
        }

        if (result is Result.Success) {
            postRepository.postCache.find { it.id == postId }?.commentCount?.value =
                postRepository.postCache.find { it.id == postId }?.commentCount?.value!! + 1
        }

        return result
    }

    override suspend fun replyComment(
        parentId: String,
        postId: String,
        content: String,
    ): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> {
            badditAPI.replyComment(
                CommentCommentRequestBody(
                    parentId = parentId,
                    content = content,
                    postId = postId
                )
            )
        }

        if (result is Result.Success) {
            postRepository.postCache.find { it.id == postId }?.commentCount?.value =
                postRepository.postCache.find { it.id == postId }?.commentCount?.value!! + 1
        }

        return result
    }

    override suspend fun editComment(
        commentId: String,
        content: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.editComment(
                EditCommentRequestBody(
                    commentId = commentId,
                    content = content
                )
            )
        }
    }

    override suspend fun deleteComment(commentId: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.deleteComment(commentId) }
    }
}