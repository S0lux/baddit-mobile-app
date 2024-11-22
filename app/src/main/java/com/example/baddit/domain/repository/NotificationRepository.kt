package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.notification.NotificationResponseItem
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result

interface NotificationRepository {
    val notifications: SnapshotStateList<NotificationResponseItem>

    suspend fun fetchNextNotifications(): Result<Array<NotificationResponseItem>, DataError.NetworkError>
    suspend fun fetchNewNotifications(): Result<Array<NotificationResponseItem>, DataError.NetworkError>
    suspend fun sendFcmTokenToServer(fcmToken: String): Result<Unit, DataError.NetworkError>
    suspend fun markNotificationAsRead(notificationId: String): Result<Unit, DataError.NetworkError>
}