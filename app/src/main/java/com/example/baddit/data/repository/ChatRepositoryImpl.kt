package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.chat.DirectChannelBody
import com.example.baddit.data.dto.chat.SendMessageBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.MutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.posts.PostResponseDTO
import com.example.baddit.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI,
) : ChatRepository {
    override var channelListCache: SnapshotStateList<MutableChannelResponseDTOItem> = mutableStateListOf()
    override var channelMessageCache: SnapshotStateList<MutableMessageResponseDTOItem> = mutableStateListOf()

    override suspend fun getOrCreateDirectChannel(targetUserId: String): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall { badditAPI.getOrCreateDirectChannel(DirectChannelBody(targetUserId)) }
    }

    override suspend fun sendMessage(
        channelId: String,
        content: String
    ): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.sendMessage(SendMessageBody(channelId, content)) }
    }

    override suspend fun getChannelMessages(channelId: String): Result<ArrayList<MessageResponseDTOItem>, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.getChannelMessages(
                channelId
            )
        }
    }

    override suspend fun getAllChannels(): Result<ArrayList<ChannelResponseDTOItem>, DataError.NetworkError> {
        return safeApiCall { badditAPI.getAllChannels() }
    }
}