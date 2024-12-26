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
import com.example.baddit.domain.model.friend.BaseFriendUser
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.repository.ChatRepository
import com.example.baddit.domain.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    val imageLoader: ImageLoader,
    val chatRepository: ChatRepository,
    val authRepository: AuthRepository,
    val friendRepository: FriendRepository,
    val socketManager: SocketManager
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

    val _socketMessages = mutableStateListOf<MutableMessageResponseDTOItem>()
    var socketMessages: List<MutableMessageResponseDTOItem> = _socketMessages

    var uploadedImageUrls by mutableStateOf<List<String>>(emptyList())
    var isUploading by mutableStateOf(false)

    var availableFriends by mutableStateOf<List<BaseFriendUser>>(emptyList())
    var selectedFriendsForChannel by mutableStateOf<List<BaseFriendUser>>(emptyList())


    var isCreatingDirectChannel by mutableStateOf(false)

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
                // Clear and replace socket messages
                _socketMessages.clear()
                _socketMessages.addAll(messages)
                socketMessages = _socketMessages
            }
        }
    }

    fun clearPreviousMessages() {
        _socketMessages.clear()
        socketMessages = _socketMessages
        chatRepository.channelMessageCache.clear()
    }

    fun fetchAvailableFriends() {
        viewModelScope.launch {
            // Use the existing method that updates local friends
            when (val result = friendRepository.updateLocalUserFriend()) {
                is Result.Success -> {
                    // Directly use the local friends list
                    availableFriends = friendRepository.currentFriends
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to fetch friends"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    fun toggleFriendSelection(friend: BaseFriendUser) {
        selectedFriendsForChannel = if (selectedFriendsForChannel.contains(friend)) {
            selectedFriendsForChannel.filter { it != friend }
        } else {
            selectedFriendsForChannel + friend
        }
    }

    fun createOrGetChatChannel(targetUserId: String){
        viewModelScope.launch {
            isCreatingDirectChannel = true
            when(val result = chatRepository.getOrCreateDirectChannel(targetUserId)){
                is Result.Error -> {
                    isCreatingDirectChannel = false
                }
                is Result.Success -> {
                    chatRepository.channelListCache.add(result.data.toMutableChannelResponseDTOItem())
                    isCreatingDirectChannel = false
                }
            }
        }
    }

    fun createChatChannel(channelName: String) {
        viewModelScope.launch {
            val selectedFriendIds = selectedFriendsForChannel.map { it.id }
            when (val result = chatRepository.createChatChannel(channelName, selectedFriendIds)) {
                is Result.Success -> {
                    refreshChannelList()
                    selectedFriendsForChannel = emptyList()
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to create channel"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    fun connectToChannel(channelId: String) {
        socketManager.disconnect()

        clearPreviousMessages()

        socketManager.connect(channelId)
    }

    fun sendMessageToChannel(channelId: String, message: String, sender: Sender) {

        socketManager.sendMessage(
            channelId,
            message,
            sender,
            uploadedImageUrls
        )

        uploadedImageUrls = emptyList()
    }

    fun deleteMessage(channelId: String, messageId: String){
        socketManager.deleteMessage(channelId, messageId);
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

    fun updateChannelName(channelId: String, newName: String) {
        viewModelScope.launch {
            when (val result = chatRepository.updateChatChannelName(channelId, newName)) {
                is Result.Success -> {
                    // Update the local cache if needed
                    val channelIndex =
                        chatRepository.channelListCache.indexOfFirst { it.id == channelId }
                    if (channelIndex != -1) {
                        chatRepository.channelListCache[channelIndex] =
                            result.data.toMutableChannelResponseDTOItem()
                    }
                    error = "" // Clear any previous errors
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to update channel name"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    fun uploadChannelAvatar(channelId: String, avatarFile: File) {
        viewModelScope.launch {
            isUploading = true
            when (val result = chatRepository.updateChatChannelAvatar(channelId, avatarFile)) {
                is Result.Success -> {
                    // Update the local cache if needed
                    val channelIndex =
                        chatRepository.channelListCache.indexOfFirst { it.id == channelId }
                    if (channelIndex != -1) {
                        chatRepository.channelListCache[channelIndex] =
                            result.data.toMutableChannelResponseDTOItem()
                    }
                    error = "" // Clear any previous errors
                    isUploading = false
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to upload channel avatar"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                    isUploading = false
                }
            }
        }
    }

    fun addModeratorsToChannel(channelId: String, moderatorIds: List<String>) {
        viewModelScope.launch {
            when (val result = chatRepository.addModeratorsToChannel(channelId, moderatorIds)) {
                is Result.Success -> {
                    // Update the local cache if needed
                    val channelIndex =
                        chatRepository.channelListCache.indexOfFirst { it.id == channelId }
                    if (channelIndex != -1) {
                        chatRepository.channelListCache[channelIndex] =
                            result.data.toMutableChannelResponseDTOItem()
                    }
                    error = "" // Clear any previous errors
                    selectedFriendsForChannel = emptyList() // Reset selected friends
                    fetchAvailableFriends();
                    fetchChannelDetail(channelId)
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to add moderators"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    fun addMembersToChannel(channelId: String, memberIds: List<String>) {
        viewModelScope.launch {
            when (val result = chatRepository.addMembersToChannel(channelId, memberIds)) {
                is Result.Success -> {
                    // Update the local cache if needed
                    val channelIndex =
                        chatRepository.channelListCache.indexOfFirst { it.id == channelId }
                    if (channelIndex != -1) {
                        chatRepository.channelListCache[channelIndex] =
                            result.data.toMutableChannelResponseDTOItem()
                    }
                    error = "" // Clear any previous errors
                    selectedFriendsForChannel = emptyList() // Reset selected friends
                    fetchAvailableFriends();
                    fetchChannelDetail(channelId)
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to add members"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    //TODO make remove members from chat channel function
    fun removeMembersFromChannel(channelId: String, memberIds: List<String>) {
        viewModelScope.launch {
            when (val result = chatRepository.removeMembers(channelId, memberIds)) {
                is Result.Success -> {
                    val channelIndex =
                        chatRepository.channelListCache.indexOfFirst { it.id == channelId }
                    if (channelIndex != -1) {
                        chatRepository.channelListCache[channelIndex] =
                            result.data.toMutableChannelResponseDTOItem()
                    }
                    error = "" // Clear any previous errors
                    selectedFriendsForChannel = emptyList() // Reset selected friends
                    fetchAvailableFriends();
                    fetchChannelDetail(channelId)
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to remove members"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }


    fun deleteChannel(channelId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.deleteChannel(channelId)) {
                is Result.Success -> {
                    // Remove the channel from local cache
                    refreshChannelList()
                    error = "" // Clear any previous errors
                    // Optionally navigate back or refresh channel list
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to delete channel"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    fun deleteMessage(messageId: String) {
        viewModelScope.launch {
            when (val result = chatRepository.deleteMessage(messageId)) {
                is Result.Success -> {
                    // Remove the message from local cache
                    _socketMessages.removeAll { it.id == messageId }
                    chatRepository.channelMessageCache.removeAll { it.id == messageId }
                    error = "" // Clear any previous errors
                }

                is Result.Error -> {
                    error = when (result.error) {
                        DataError.NetworkError.INTERNAL_SERVER_ERROR -> "Unable to delete message"
                        DataError.NetworkError.NO_INTERNET -> "No internet connection"
                        else -> "An unknown error occurred"
                    }
                }
            }
        }
    }

    // Helper method to check if current user is a moderator of a specific channel
    fun isUserModerator(channelId: String): Boolean {
        val currentChannel = chatRepository.channelListCache.find { it.id == channelId }
        val currentUserId = me.value?.id
        return currentChannel?.moderators?.any { it.id == currentUserId } ?: false
    }

    fun fetchChannelDetail(channelId: String) {
        viewModelScope.launch {
            isRefreshing = true
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
                    isRefreshing = false
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