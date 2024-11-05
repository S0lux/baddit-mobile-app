package com.example.baddit.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.community.GetACommunityResponseDTO
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.domain.repository.CommunityRepository
import com.example.baddit.presentation.utils.FieldState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    val communityList = mutableStateOf<GetCommunityListResponseDTO>(GetCommunityListResponseDTO())

    val community = mutableStateOf<GetACommunityResponseDTO?>(null)

    var nameState by mutableStateOf(FieldState())
        private set;

    var descriptionState by mutableStateOf(FieldState())
        private set;

    var isLoading by mutableStateOf(false)
        private set

    var isCreateDone by mutableStateOf(false)
        private set

    var isRefreshing by mutableStateOf(false)
        private set;

    var error by mutableStateOf("")

    fun fetchCommunityList() {
        viewModelScope.launch {
            when (val result = communityRepository.getCommunities()) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    communityList.value = result.data
                    error = ""
                }
            }
        }
    }

    fun fetchCommunity(communityName: String) {
        viewModelScope.launch {
            when (val result = communityRepository.getCommunity(communityName)) {
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    community.value = result.data
                    error = ""
                }
            }
        }
    }

    fun setName(input: String) {
        nameState = nameState.copy(value = input, error = "")
    }

    fun setDescription(input: String) {
        descriptionState = descriptionState.copy(value = input, error = "")
    }

    fun handleCreateCommunityError(error: DataError.NetworkError) {
        when (error) {
            DataError.NetworkError.CONFLICT -> {
                nameState = nameState.copy(error = "This community name is already taken.")

            }

            DataError.NetworkError.NO_INTERNET,
            DataError.NetworkError.INTERNAL_SERVER_ERROR,
            DataError.NetworkError.FORBIDDEN,
            DataError.NetworkError.TOKEN_INVALID,
            DataError.NetworkError.UNKNOWN_ERROR -> {
            }

            DataError.NetworkError.UNAUTHORIZED -> {
            }

        }
    }

    fun resetCreateState() {
        setName("")
        setDescription("")
        isLoading = false
        isCreateDone = false
    }

    suspend fun createCommunity(): Result<Unit, DataError.NetworkError> {
        isLoading = true
        val result = communityRepository.createCommunity(nameState.value, descriptionState.value)
        isLoading = false
        when (result) {
            is Result.Error -> handleCreateCommunityError(result.error)
            is Result.Success -> {
                isCreateDone = true
            }
        }

        return result;
    }

    fun createCommunityNonSuspend() {
        viewModelScope.launch {
            createCommunity()
        }
    }

}
