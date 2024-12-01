package com.example.baddit.presentation.screens.chat

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.ImageLoader
import com.example.baddit.data.socket.SocketManager
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.toMutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.Sender
import com.example.baddit.domain.model.chat.chatMessage.toMutableMessageResponseDTOItem
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    val chatRepository: ChatRepository,
    val authRepository: AuthRepository,
    private val socketManager: SocketManager
) : ViewModel() {
    val channelList =
        mutableStateListOf<ArrayList<ChannelResponseDTOItem>>(ArrayList<ChannelResponseDTOItem>())
    val channelMessages =
        mutableStateListOf<ArrayList<ChannelResponseDTOItem>>(ArrayList<ChannelResponseDTOItem>())
    val me = authRepository.currentUser
    val loggedIn = authRepository.isLoggedIn;
    var isRefreshing by mutableStateOf(false)
        private set;
    var error by mutableStateOf("")
    var isRefreshingChannelList by mutableStateOf(false)

    var isSocketConnected by mutableStateOf(false)

    private val _socketMessages = mutableStateListOf<MutableMessageResponseDTOItem>()
    val socketMessages: List<MutableMessageResponseDTOItem> = _socketMessages

    var uploadedImageUrls by mutableStateOf<List<String>>(emptyList())
    var isUploading by mutableStateOf(false)

    init {
        // Observe socket connection status
        viewModelScope.launch {
            socketManager.connectionStatus.collectLatest { connected ->
                isSocketConnected = connected
            }
        }

        // Observe incoming socket messages
        viewModelScope.launch {
            socketManager.messages.collectLatest { messages ->
                _socketMessages.addAll(messages)
            }
        }
    }

    fun connectToChannel(channelId: String) {
        socketManager.connect(channelId)
    }

    fun sendMessageToChannel(channelId: String, message: String, sender: Sender) {
        socketManager.sendMessage(channelId, message, sender)
    }

    fun disconnectFromChannel() {
        socketManager.disconnect()
    }

    fun uploadChatImages(channelId: String, imageFiles: List<File>) {
        viewModelScope.launch {
            isUploading = true
            when (val result = chatRepository.uploadChatImages(channelId, imageFiles)) {
                is Result.Success -> {
                    uploadedImageUrls = result.data
                    isUploading = false
                }
                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to upload images"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                    isUploading = false
                }
            }
        }
    }

    fun refreshChannelList() {
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
                    chatRepository.channelListCache.addAll(fetchChannels.data.map { it.toMutableChannelResponseDTOItem(); })
                }
            }
        }
    }

    fun fetchChannelDetail(channelId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.getChannelMessages(channelId)) {
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

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}