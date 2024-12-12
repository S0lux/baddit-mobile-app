package com.example.baddit.data.socket

import android.util.Log
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.Sender
import com.example.baddit.domain.model.chat.chatMessage.toMutableMessageResponseDTOItem
import com.example.baddit.domain.repository.ChatRepository
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.net.URISyntaxException
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor(
    private val chatRepository: ChatRepository
) {
    private var socket: Socket? = null
    private var currentChannelId: String? = null
    // StateFlow to emit real-time messages
    private val _messages = MutableStateFlow<List<MutableMessageResponseDTOItem>>(emptyList())
    val messages = _messages.asStateFlow()

    // StateFlow to track connection status
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus = _connectionStatus.asStateFlow()

    fun connect(channelId: String) {
        try {
            disconnect()
            // Configure Socket.IO connection options
            val options = IO.Options().apply {
                transports = arrayOf("websocket")
                forceNew = true
            }

            // Replace with your actual socket server URL
            socket = IO.socket("http://10.0.2.2:3001", options)

            socket?.apply {
                // Connection listeners
                on(Socket.EVENT_CONNECT) {
                    _connectionStatus.value = true
                    Log.d("SocketIO", "Connected to socket server")

                    // Join the specific channel
                    emit("join_channel", JSONObject().apply {
                        put("channelId", channelId)
                    })
                    currentChannelId = channelId
                }

                // Listen for new messages
                on("new_message") { args ->
                    Log.d("SocketIO","Receive new message")
                    args?.let {
                        if (it.isNotEmpty()) {
                            val messageJson = it[0] as JSONObject
                            val newMessage = parseMessageFromJson(messageJson)

                            if (newMessage.channelId == currentChannelId) {
                                _messages.value += newMessage.toMutableMessageResponseDTOItem()
                            }
                        }
                    }
                }

                // Error handling
                on(Socket.EVENT_CONNECT_ERROR) { args ->
                    Log.e("SocketIO", "Connection error: ${args?.contentToString()}")
                }

                on(Socket.EVENT_DISCONNECT) {
                    _connectionStatus.value = false
                    Log.d("SocketIO", "Disconnected from socket server")
                }

                // Connect to the socket
                connect()
            }
        } catch (e: URISyntaxException) {
            Log.e("SocketIO", "Invalid socket server URL", e)
        }
    }

    fun sendMessage(channelId: String, content: String, sender: Sender, mediaUrls: List<String> = emptyList()) {
        socket?.emit("send_message", JSONObject().apply {
            put("channelId", channelId)
            put("content", content)
            put("type", if (mediaUrls.isNotEmpty()) "IMAGE" else "TEXT")
            put("mediaUrls", JSONArray(mediaUrls))
            put("sender", JSONObject().apply {
                put("id", sender.id)
                put("username", sender.username)
                put("avatarUrl", sender.avatarUrl)
            })
        })
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    // Helper method to parse message from JSON
    private fun parseMessageFromJson(json: JSONObject): MessageResponseDTOItem {
        // Parse mediaUrls
        val mediaUrlsArray = json.optJSONArray("mediaUrls")
        val mediaUrls = (0 until (mediaUrlsArray?.length() ?: 0)).map {
            mediaUrlsArray?.getString(it) ?: ""
        }

        return MessageResponseDTOItem(
            id = json.getString("id"),
            sender = Sender(
                id = json.getJSONObject("sender").getString("id"),
                username = json.getJSONObject("sender").getString("username"),
                avatarUrl = json.getJSONObject("sender").getString("avatarUrl")
            ),
            content = json.getString("content"),
            type = json.getString("type"),
            mediaUrls = mediaUrls,
            createdAt = json.getString("createdAt"),
            isDeleted = json.optBoolean("isDeleted",false),
            channelId = json.getString("channelId")
        )
    }
}