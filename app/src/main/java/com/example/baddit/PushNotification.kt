package com.example.baddit

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.FriendRepository
import com.example.baddit.domain.repository.NotificationRepository
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.core.app.NotificationManagerCompat

@AndroidEntryPoint
class PushNotification: FirebaseMessagingService() {
    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var notificationRepository: NotificationRepository

    @Inject
    lateinit var friendRepository: FriendRepository

    companion object {
        const val CHANNEL_ID = "default_channel"
    }

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
        Log.d("Notification", "IM CALLED!!!")
        CoroutineScope(Dispatchers.IO).launch {
            when (val result = notificationRepository.fetchNewNotifications()) {
                is com.example.baddit.domain.error.Result.Success -> {
                    Log.d("FCM", "New notification fetched")
                }
                is com.example.baddit.domain.error.Result.Error -> {
                    Log.e("FCM", "Failed to fetch new notifications from server: ${result.error}")
                }
            }

            authRepository.getMe()

            if (message.data.isNotEmpty()) {
                if (message.data["type"] == "FRIEND_REQUEST") {
                    friendRepository.updateLocalUserFriend()
                }
            }
        }

        // Extract notification data
        val notificationType = message.data["type"] ?: return
        val notificationTypeId = message.data["typeId"]
        val title = message.notification?.title ?: "Notification"
        val body = message.notification?.body ?: "New message"

        // Create an intent that will be fired when notification is clicked
        val intent = Intent(this, MainActivity::class.java).apply {
            // Add extra data to the intent
            if (notificationTypeId != null) putExtra("typeId", notificationTypeId)

            // Flags to handle how the activity is launched
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        intent.action = notificationType

        // Create a PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            (0..100000).random(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Build the notification with the PendingIntent
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.baddit_black)
            .setAutoCancel(true)
            .build()

        // Show the notification
        try {
            NotificationManagerCompat.from(this).notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        }
        catch (error: SecurityException) {
            Log.d("Notification", "Cannot show notification. Missing permission!")
        }
    }
}