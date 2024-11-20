package com.example.baddit.data.repository

import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.notification.FcmTokenBody
import com.example.baddit.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor (
    private val badditAPI: BadditAPI
): NotificationRepository {
    override suspend fun sendFcmTokenToServer(fcmToken: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.sendFcmTokenToServer(FcmTokenBody(fcmToken)) }
    }
}