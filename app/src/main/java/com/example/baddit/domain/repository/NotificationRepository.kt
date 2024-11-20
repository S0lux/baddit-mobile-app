package com.example.baddit.domain.repository

import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result

interface NotificationRepository {
    suspend fun sendFcmTokenToServer(fcmToken: String): Result<Unit, DataError.NetworkError>
}