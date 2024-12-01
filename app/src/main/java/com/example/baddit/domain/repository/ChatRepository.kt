package com.example.baddit.domain.repository

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.MutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import java.io.File

interface ChatRepository {
    var channelListCache: SnapshotStateList<MutableChannelResponseDTOItem>
    var channelMessageCache: SnapshotStateList<MutableMessageResponseDTOItem>
    suspend fun getOrCreateDirectChannel(targetUserId: String): Result<ChannelResponseDTOItem, DataError.NetworkError>
    suspend fun sendMessage(
        channelId: String,
        content: String
    ): Result<Unit, DataError.NetworkError>

    suspend fun getChannelMessages(channelId: String): Result<ArrayList<MessageResponseDTOItem>, DataError.NetworkError>
    suspend fun getAllChannels(): Result<ArrayList<ChannelResponseDTOItem>,DataError.NetworkError>
    suspend fun uploadChatImages(
        channelId: String,
        imageFiles: List<File>
    ): Result<List<String>, DataError.NetworkError>
}