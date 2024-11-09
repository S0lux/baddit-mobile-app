package com.example.baddit.presentation.components.SideDrawerContent

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.CommunityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SideDrawerContentViewModel @Inject constructor(
    val auth: AuthRepository,
    val com: CommunityRepository
) : ViewModel() {
    var joinedCommunities = mutableStateListOf<GetACommunityResponseDTO>()

    var isRefreshing = mutableStateOf(false)
    var isLoggedIn = auth.isLoggedIn
    var error = mutableStateOf("")

    fun getJoinCommunity() {
        joinedCommunities.clear()
        viewModelScope.launch {
            isRefreshing.value = true
            delay(timeMillis = 100)
            when (val res = auth.getMe()) {
                is Result.Error -> {
                    isRefreshing.value = false
                    error.value = when (res.error) {
                        DataError.NetworkError.NO_INTERNET -> "No internet connection."
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                        DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                        DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                        DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred."
                        DataError.NetworkError.FORBIDDEN -> "Email not verified."
                        else -> "This error shouldn't happen unless something changed in the backend."
                    }
                }

                is Result.Success -> {
                    if (res.data.communities.isNotEmpty()) {
                        res.data.communities.forEach {
                            when (val result = com.getCommunity(communityName = it.name)) {
                                is Result.Error -> {
                                    error.value = when (result.error) {
                                        DataError.NetworkError.NO_INTERNET -> "No internet connection."
                                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Server is down."
                                        DataError.NetworkError.UNAUTHORIZED -> "Wrong username/password"
                                        DataError.NetworkError.CONFLICT -> "This error shouldn't happen unless something changed in the backend."
                                        DataError.NetworkError.UNKNOWN_ERROR -> "An unknown error has occurred in second."
                                        DataError.NetworkError.FORBIDDEN -> "Email not verified."
                                        else -> "This error shouldn't happen unless something changed in the backend."
                                    }
                                }

                                is Result.Success -> {
                                    joinedCommunities.add(result.data)
                                }

                            }
                        }
                    }
                    isRefreshing.value = false
                }

            }
        }

    }

}
