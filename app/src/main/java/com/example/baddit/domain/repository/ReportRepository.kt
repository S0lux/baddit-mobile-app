package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.MutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.report.MutableReportResponseDTO
import com.example.baddit.domain.model.report.ReportResponseDTO

interface ReportRepository {
    var reportList: SnapshotStateList<MutableReportResponseDTO>

    suspend fun createReport(type: String, content: String, reportedUserId:String?, reportedPostId: String?): Result<ReportResponseDTO,DataError.NetworkError>
    suspend fun getAllReports(): Result<ArrayList<ReportResponseDTO>,DataError.NetworkError>
    suspend fun resolveReports(reportId: String): Result<ReportResponseDTO,DataError.NetworkError>
}