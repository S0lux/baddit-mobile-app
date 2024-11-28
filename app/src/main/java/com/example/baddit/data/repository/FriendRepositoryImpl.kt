package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.friend.BaseFriendUser
import com.example.baddit.domain.model.friend.GetFriendsResponse
import com.example.baddit.domain.model.friend.IncomingFriendRequestDto
import com.example.baddit.domain.model.friend.OutgoingFriendRequestDto
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.FriendRepository
import javax.inject.Inject

class FriendRepositoryImpl @Inject constructor (
    private val badditAPI: BadditAPI,
    private val authRepository: AuthRepository
) : FriendRepository {
    override var currentFriends = mutableStateListOf<BaseFriendUser>()
    override var outgoingFriendRequests = mutableStateListOf<OutgoingFriendRequestDto>()
    override var incomingFriendRequests = mutableStateListOf<IncomingFriendRequestDto>()

    override suspend fun updateLocalUserFriend(): Result<Unit, DataError.NetworkError> {
        if (authRepository.isLoggedIn.value.not()) return Result.Error(DataError.NetworkError.UNAUTHORIZED)
        val result = getFriends(authRepository.currentUser.value!!.id)

        return when (result) {
            is Result.Success -> {
                currentFriends.clear()
                outgoingFriendRequests.clear()
                incomingFriendRequests.clear()

                currentFriends.addAll(result.data.currentFriends)
                outgoingFriendRequests.addAll(result.data.outgoingRequests)
                incomingFriendRequests.addAll(result.data.incomingRequests)

                Result.Success(Unit)
            }

            is Result.Error -> {
                Result.Success(Unit)
            }
        }
    }

    override suspend fun getFriends(userId: String): Result<GetFriendsResponse, DataError.NetworkError> {
        val result = safeApiCall<GetFriendsResponse, DataError.NetworkError> {
            badditAPI.getFriends(userId)
        }
        return result
    }

    override suspend fun sendFriendRequest(userId: String): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.sendFriendRequest(userId) }

        return when (result) {
            is Result.Success -> {
                updateLocalUserFriend()
                result
            }

            is Result.Error -> {
                result
            }
        }
    }

    override suspend fun acceptFriendRequest(userId: String): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.acceptFriendRequest(userId) }

        return when (result) {
            is Result.Success -> {
                updateLocalUserFriend()
                result
            }

            is Result.Error -> {
                result
            }
        }
    }

    override suspend fun rejectFriendRequest(userId: String): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.rejectFriendRequest(userId) }

        return when (result) {
            is Result.Success -> {
                updateLocalUserFriend()
                result
            }

            is Result.Error -> {
                result
            }
        }
    }

    override suspend fun removeFriend(userId: String): Result<Unit, DataError.NetworkError> {
        val result = safeApiCall<Unit, DataError.NetworkError> { badditAPI.removeFriend(userId) }

        return when (result) {
            is Result.Success -> {
                updateLocalUserFriend()
                result
            }

            is Result.Error -> {
                result
            }
        }
    }
}