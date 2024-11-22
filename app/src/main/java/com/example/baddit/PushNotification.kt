package com.example.baddit

import android.util.Log
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PushNotification: FirebaseMessagingService() {
    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        CoroutineScope(Dispatchers.IO).launch {
            when (val result = notificationRepository.sendFcmTokenToServer(token)) {
                is com.example.baddit.domain.error.Result.Success -> {
                    Log.d("FCM", "New token sent successfully to server")
                }
                is com.example.baddit.domain.error.Result.Error -> {
                    Log.e("FCM", "Failed to send new token to server: ${result.error}")
                }
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = notificationRepository.fetchNewNotifications()) {
                is com.example.baddit.domain.error.Result.Success -> {
                    Log.d("FCM", "New notification fetched")
                }
                is com.example.baddit.domain.error.Result.Error -> {
                    Log.e("FCM", "Failed to fetch new notifications from server: ${result.error}")
                }
            }
        }
    }
}