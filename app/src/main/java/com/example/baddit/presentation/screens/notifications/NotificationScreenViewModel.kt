package com.example.baddit.presentation.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.data.dto.notification.NotificationResponseItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationScreenViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
    private val authRepository: AuthRepository
): ViewModel() {
    private val _notifications = notificationRepository.notifications
    val notifications: List<NotificationResponseItem> = _notifications
    val isUserLoggedIn = authRepository.isLoggedIn.value

    fun onRefresh() {
        viewModelScope.launch {
            notificationRepository.fetchNewNotifications()
        }
    }

    suspend fun markAllAsRead() {
        val unreadNotifications = _notifications.filterIndexed { index, item -> !item.isRead }

        // Update local state
        unreadNotifications.forEachIndexed { index, item ->
            _notifications[index] = item.copy(isRead = true)
        }

        // Perform all async operations concurrently
        coroutineScope {
            unreadNotifications.map { notification ->
                async {
                    notificationRepository.markNotificationAsRead(notification.id)
                }
            }.awaitAll()
        }
    }
}