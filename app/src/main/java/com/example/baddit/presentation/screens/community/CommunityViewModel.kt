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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val communityRepository: CommunityRepository
) : ViewModel() {

    val communityList = mutableStateOf<GetCommunityListResponseDTO>(GetCommunityListResponseDTO())

    val community = mutableStateOf<GetACommunityResponseDTO?>(null)

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

    fun fetchCommunity(communityName: String){
        viewModelScope.launch {
            when(val result = communityRepository.getCommunity(communityName)){
                is Result.Error ->{
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }
                is Result.Success ->{
                    community.value = result.data
                    error = ""
                }
            }
        }
    }

}
