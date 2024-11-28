package com.example.baddit.presentation.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.toMutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.toMutableMessageResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    val chatRepository: ChatRepository,
    val authRepository: AuthRepository
) : ViewModel() {
    val channelList = mutableStateListOf<ArrayList<ChannelResponseDTOItem>>(ArrayList<ChannelResponseDTOItem>())
    val channelMessages  = mutableStateListOf<ArrayList<ChannelResponseDTOItem>>(ArrayList<ChannelResponseDTOItem>())
    val me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;
    var isRefreshing by mutableStateOf(false)
        private set;
    var error by mutableStateOf("")
    var isRefreshingChannelList by mutableStateOf(false)

    fun refreshChannelList(){
        viewModelScope.launch {
            isRefreshingChannelList = true
            when (val fetchChannels = chatRepository.getAllChannels()) {
                is Result.Error -> {
                    error = when (fetchChannels.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    fetchChannels.data.forEach {
                        Log.d("Channel", it.name)
                    }
                    chatRepository.channelListCache.clear()
                    isRefreshingChannelList = false;
                    error = ""
                    chatRepository.channelListCache.addAll(fetchChannels.data.map { it.toMutableChannelResponseDTOItem();})
                }
            }
        }
    }
    fun fetchChannelDetail(channelId: String){
        viewModelScope.launch {
            when(val result = chatRepository.getChannelMessages(channelId)){
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to establish connection to server"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown network error has occurred"
                    }
                }

                is Result.Success -> {
                    chatRepository.channelMessageCache.clear()
                    error = ""
                    chatRepository.channelMessageCache.addAll(result.data.map { it.toMutableMessageResponseDTOItem() })
                }
            }
        }
    }
}