package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.friend.BaseFriendUser
import com.example.baddit.domain.model.friend.GetFriendsResponse
import com.example.baddit.domain.model.friend.IncomingFriendRequestDto
import com.example.baddit.domain.model.friend.OutgoingFriendRequestDto

interface FriendRepository {
    var currentFriends: SnapshotStateList<BaseFriendUser>
    var outgoingFriendRequests: SnapshotStateList<OutgoingFriendRequestDto>
    var incomingFriendRequests: SnapshotStateList<IncomingFriendRequestDto>

    suspend fun updateLocalUserFriend(): Result<Unit, DataError.NetworkError>
    suspend fun getFriends(userId: String): Result<GetFriendsResponse, DataError.NetworkError>
    suspend fun sendFriendRequest(userId: String): Result<Unit, DataError.NetworkError>
    suspend fun acceptFriendRequest(userId: String): Result<Unit, DataError.NetworkError>
    suspend fun rejectFriendRequest(userId: String): Result<Unit, DataError.NetworkError>
    suspend fun removeFriend(userId: String): Result<Unit, DataError.NetworkError>
}