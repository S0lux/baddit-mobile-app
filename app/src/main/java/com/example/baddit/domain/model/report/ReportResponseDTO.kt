package com.example.baddit.domain.model.report

data class ReportResponseDTO(
    val id: String,
    val type: ReportType,
    val content: String,
    val reporterId: String,
    val status: ReportStatus,
    val createdAt: String,
    val resolvedById: String?,
    val reportedUserId: String?,
    val reportedPostId: String?,
    val reporter: User,
    val reportedPost: Post?,
    val reportedUser: User?,
    val resolvedBy: User?
)

data class MutableReportResponseDTO(
    val id: String,
    val type: ReportType,
    val content: String,
    val reporterId: String,
    val status: ReportStatus,
    val createdAt: String,
    val resolvedById: String?,
    val reportedUserId: String?,
    val reportedPostId: String?,
    val reporter: User,
    val reportedPost: Post?,
    val reportedUser: User?,
    val resolvedBy: User?
)

fun ReportResponseDTO.toMutableReportResponseDTO():MutableReportResponseDTO{
    return MutableReportResponseDTO(
        id, type, content, reporterId, status, createdAt, resolvedById, reportedUserId, reportedPostId, reporter, reportedPost, reportedUser, resolvedBy
    )
}

enum class ReportType {
    USER, POST
}

enum class ReportStatus {
    PENDING, RESOLVED
}

data class User(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val status: String,
    val role: String
)

data class Post(
    val id: String,
    val type: String,
    val title: String,
    val content: String,
    val mediaUrls: List<String>,
    val authorId: String,
    val deleted: Boolean,
    val score: Int
)