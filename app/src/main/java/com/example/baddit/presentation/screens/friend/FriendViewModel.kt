package com.example.baddit.presentation.screens.friend

import androidx.lifecycle.ViewModel
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FriendViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val friendRepository: FriendRepository
): ViewModel() {
    val isLoggedIn = authRepository.isLoggedIn
    val currentFriends = friendRepository.currentFriends
    val incomingRequests = friendRepository.incomingFriendRequests

    suspend fun updateFriendsInfo() {
        if (authRepository.isLoggedIn.value.not()) return
        friendRepository.updateLocalUserFriend()
    }

    suspend fun acceptFriendRequest(userId: String) {
        friendRepository.acceptFriendRequest(userId)
    }

    suspend fun rejectFriendRequest(userId: String) {
        friendRepository.rejectFriendRequest(userId)
    }

    suspend fun removeFriend(userId: String) {
        friendRepository.removeFriend(userId)
    }
}