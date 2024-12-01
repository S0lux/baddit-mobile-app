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

    // StateFlow to emit real-time messages
    private val _messages = MutableStateFlow<List<MutableMessageResponseDTOItem>>(emptyList())
    val messages = _messages.asStateFlow()

    // StateFlow to track connection status
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus = _connectionStatus.asStateFlow()

    fun connect(channelId: String) {
        try {
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
                }

                // Listen for new messages
                on("new_message") { args ->
                    Log.d("SocketIO","Receive new message")
                    args?.let {
                        if (it.isNotEmpty()) {
                            val messageJson = it[0] as JSONObject
                            val newMessage = parseMessageFromJson(messageJson)

                            val mutableMessage = newMessage.toMutableMessageResponseDTOItem()
                            // Update local cache and StateFlow
//                            chatRepository.channelMessageCache.add(mutableMessage)
                            _messages.value += newMessage.toMutableMessageResponseDTOItem()
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

    fun sendMessage(channelId: String, content: String, sender: Sender) {
        socket?.emit("send_message", JSONObject().apply {
            put("channelId", channelId)
            put("content", content)
            put("sender",JSONObject().apply {
                put("id",sender.id )
                put("username",sender.username )
                put("avatarUrl",sender.avatarUrl )
            })
        })
    }

    fun disconnect() {
        socket?.disconnect()
        socket = null
    }

    // Helper method to parse message from JSON
    private fun parseMessageFromJson(json: JSONObject): MessageResponseDTOItem {
        return MessageResponseDTOItem(
            id = json.getString("id"),
            sender = Sender(
                id = json.getJSONObject("sender").getString("id"),
                username = json.getJSONObject("sender").getString("username"),
                avatarUrl = json.getJSONObject("sender").getString("avatarUrl")
            ),
            content = json.getString("content"),
            type = json.getString("type"),
            createdAt = json.getString("createdAt"),
        )
    }
}