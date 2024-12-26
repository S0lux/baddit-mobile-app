package com.example.baddit.data.repository

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.baddit.data.dto.chat.AddMembersBody
import com.example.baddit.data.dto.chat.AddModeratorsBody
import com.example.baddit.data.dto.chat.CreateChannelBody
import com.example.baddit.data.dto.chat.DirectChannelBody
import com.example.baddit.data.dto.chat.RemoveMembersBody
import com.example.baddit.data.dto.chat.SendMessageBody
import com.example.baddit.data.dto.chat.UpdateChannelNameBody
import com.example.baddit.data.remote.BadditAPI
import com.example.baddit.data.utils.safeApiCall
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.chat.chatChannel.ChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatChannel.MutableChannelResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.toMutableMessageResponseDTOItem
import com.example.baddit.domain.repository.ChatRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val badditAPI: BadditAPI,
) : ChatRepository {
    override var channelListCache: SnapshotStateList<MutableChannelResponseDTOItem> =
        mutableStateListOf()
    override var channelMessageCache: SnapshotStateList<MutableMessageResponseDTOItem> =
        mutableStateListOf()

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

    override suspend fun uploadChatImages(
        channelId: String,
        imageFiles: List<File>
    ): Result<List<String>, DataError.NetworkError> {
        val channelIdBody = channelId.toRequestBody("text/plain".toMediaTypeOrNull())
        val imageParts = imageFiles.map { file ->
            MultipartBody.Part.createFormData(
                "files",
                file.name,
                file.asRequestBody("image/*".toMediaTypeOrNull())
            )
        }
        return safeApiCall {
            badditAPI.uploadChatImages(channelIdBody, imageParts)
        }
    }

    override suspend fun createChatChannel(
        channelName: String,
        memberIds: List<String>
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.createChatChannel(
                CreateChannelBody(
                    channelName,
                    memberIds
                )
            )
        }
    }

    override suspend fun updateChatChannelName(
        channelId: String,
        name: String
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.updateChatChannelName(
                UpdateChannelNameBody(
                    channelId,
                    name
                )
            )
        }
    }

    override suspend fun updateChatChannelAvatar(
        channelId: String,
        file: File
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        val channelIdBody = channelId.toRequestBody("text/plain".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData(
            "file",
            file.name,
            file.asRequestBody("image/*".toMediaTypeOrNull())
        )
        return safeApiCall { badditAPI.updateChatChannelAvatar(channelIdBody, imagePart) }
    }

    override suspend fun addModeratorsToChannel(
        channelId: String,
        moderatorIds: List<String>
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall {
            badditAPI.addModeratorsToChat(
                AddModeratorsBody(
                    channelId,
                    moderatorIds
                )
            )
        }
    }

    override suspend fun addMembersToChannel(
        channelId: String,
        memberIds: List<String>
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall { badditAPI.addMembersToChat(AddMembersBody(channelId, memberIds)) }
    }

    override suspend fun deleteChannel(channelId: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.deleteChatChannel(channelId) }
    }

    override suspend fun deleteMessage(messageId: String): Result<Unit, DataError.NetworkError> {
        return safeApiCall { badditAPI.deleteMessage(messageId) }
    }

    override suspend fun getChatChannel(channelId: String): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall { badditAPI.getChatChannel(channelId) }
    }

    override suspend fun removeMembers(
        channelId: String,
        memberIds: List<String>
    ): Result<ChannelResponseDTOItem, DataError.NetworkError> {
        return safeApiCall { badditAPI.removeMembers(RemoveMembersBody(channelId, memberIds)) }
    }
}