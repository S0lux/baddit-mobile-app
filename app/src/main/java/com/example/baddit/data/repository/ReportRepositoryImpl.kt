package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.report.CreateReportBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.report.MutableReportResponseDTO
import com.example.baddit.domain.model.report.ReportResponseDTO
import com.example.baddit.domain.repository.ChatRepository
import com.example.baddit.domain.repository.ReportRepository
import javax.inject.Inject

class ReportRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI,
) : ReportRepository {
    override var reportList: SnapshotStateList<MutableReportResponseDTO> = mutableStateListOf();
    override suspend fun createReport(
        type: String,
        content: String,
        reportedUserId: String?,
        reportedPostId: String?
    ): Result<ReportResponseDTO, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.createReport(
                CreateReportBody(
                    type,
                    content,
                    reportedUserId,
                    reportedPostId
                )
            )
        }
    }

    override suspend fun getAllReports(): Result<ArrayList<ReportResponseDTO>, DataError.NetworkError> {
        return safeApiCall { badditAPI.getAllReports() }
    }

    override suspend fun resolveReports(reportId: String): Result<ReportResponseDTO, DataError.NetworkError> {
        return safeApiCall { badditAPI.resolveReport(reportId) }
    }

}