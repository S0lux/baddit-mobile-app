package com.example.baddit.data.repository

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import com.example.baddit.data.dto.notification.NotificationResponseItem
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.notification.FcmTokenBody
import com.example.baddit.domain.repository.NotificationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor (
    private val badditAPI: BadditAPI
): NotificationRepository {
    private val fetchLimit = 15;
    private var currentOffset = 0;
    private var latestNotificationId: String? = null;

    override val notifications = mutableStateListOf<NotificationResponseItem>()

    init {
        // Fetch initial notifications
        CoroutineScope(Dispatchers.IO).launch { fetchNextNotifications() }
    }

    override suspend fun fetchNextNotifications(): Result<Array<NotificationResponseItem>, DataError.NetworkError> {
        val results = safeApiCall<Array<NotificationResponseItem>, DataError.NetworkError> {
            badditAPI.fetchUserNotifications(currentOffset, fetchLimit)
        }

        return when (results) {
            is Result.Success -> {
                if (results.data.isNotEmpty()) {
                    notifications.addAll(results.data.toList())
                    currentOffset += fetchLimit
                    latestNotificationId = results.data.maxByOrNull { it.createdAt }?.id
                }

                results
            }
            is Result.Error -> {
                results
            }
        }
    }

    override suspend fun fetchNewNotifications(): Result<Array<NotificationResponseItem>, DataError.NetworkError> {
        // If no previous notifications exist, fall back to regular fetch
        val latestId = latestNotificationId ?: return fetchNextNotifications()

        val results = safeApiCall<Array<NotificationResponseItem>, DataError.NetworkError> {
            badditAPI.fetchNewNotifications(latestId)
        }

        return when (results) {
            is Result.Success -> {
                if (results.data.isNotEmpty()) {
                    notifications.addAll(0, results.data.toList())

                    // Update the latest notification ID
                    latestNotificationId = results.data.maxByOrNull { it.createdAt }?.id
                }
                results
            }
            is Result.Error -> {
                results
            }
        }
    }

    override suspend fun sendFcmTokenToServer(fcmToken: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.sendFcmTokenToServer(FcmTokenBody(fcmToken)) }
    }

    override suspend fun markNotificationAsRead(notificationId: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.markNotificationAsRead(notificationId) }
    }
}