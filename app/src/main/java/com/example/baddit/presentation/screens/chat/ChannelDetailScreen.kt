package com.example.baddit.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.Sender
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    channelId: String,
    channelName: String,
    channelAvatar: String,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val currentUserId = viewModel.me.value?.id ?: "unknown"
    val socketMessages by remember { derivedStateOf { viewModel.socketMessages } }
    val messageScrollState = rememberLazyListState()
    var newMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Connect to channel on screen launch
    LaunchedEffect(channelId) {
        viewModel.connectToChannel(channelId)
        viewModel.fetchChannelDetail(channelId)
    }

    // Automatically scroll to bottom when new messages arrive
    LaunchedEffect(socketMessages.size) {
        if (socketMessages.isNotEmpty()) {
            coroutineScope.launch {
                messageScrollState.scrollToItem(0)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // TopAppBar with channel avatar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    AsyncImage(
                        model = channelAvatar,
                        contentDescription = "$channelName avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(channelName)
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    viewModel.disconnectFromChannel()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        // Message List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            state = messageScrollState,
            reverseLayout = true
        ) {
            // Combine cached messages and socket messages
            val allMessages =
                (viewModel.chatRepository.channelMessageCache + viewModel.socketMessages)
                    .map { it as MutableMessageResponseDTOItem }
                    .sortedByDescending { it.createdAt }

            var previousSenderId: String? = null
            items(allMessages) { message ->
                val showAvatar = previousSenderId != message.sender.id
                MessageItem(
                    message = message,
                    isMyMessage = message.sender.id == currentUserId,
                    showAvatar = showAvatar
                )
                previousSenderId = message.sender.id
            }
        }

        // Message Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message") },
                singleLine = false,
                maxLines = 4
            )

            IconButton(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        val me = viewModel.me.value
                        if (me != null) {
                            viewModel.sendMessageToChannel(channelId, newMessage, Sender(me.id,me.username,me.avatarUrl))
                            newMessage = ""
                        }
                    }
                },
                enabled = newMessage.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message"
                )
            }
        }
    }
}

@Composable
fun MessageItem(
    message: MutableMessageResponseDTOItem,
    isMyMessage: Boolean,
    showAvatar: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .widthIn(max = 350.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start
        ) {
            // Show avatar only if required
            if (!isMyMessage && showAvatar) {
                AsyncImage(
                    model = message.sender.avatarUrl,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else if (!isMyMessage) {
                // Spacer for alignment when avatar is not shown
                Spacer(modifier = Modifier.width(40.dp))
            }

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isMyMessage) 16.dp else 0.dp,
                            bottomEnd = if (isMyMessage) 0.dp else 16.dp
                        )
                    )
                    .background(
                        if (isMyMessage) Color.Blue.copy(alpha = 0.2f)
                        else Color.Gray.copy(alpha = 0.2f)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = message.content,
                    color = if (isMyMessage) Color.White else MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                Text(
                    text = formatMessageTimestamp(message.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }
        }
    }
}


// Helper function to format timestamp
fun formatMessageTimestamp(timestamp: String): String {
    return try {
        val parsedDateTime = LocalDateTime.parse(timestamp,DateTimeFormatter.ISO_DATE_TIME) // Parse the timestamp
        val now = LocalDateTime.now(ZoneId.systemDefault()) // Current date & time

        when {
            // Check if the message is from today
            parsedDateTime.toLocalDate() == now.toLocalDate() -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
            }
            // Check if the message is from yesterday
            parsedDateTime.toLocalDate() == now.minus(1, ChronoUnit.DAYS).toLocalDate() -> {
                "Yesterday"
            }
            // For older messages, show `MMM d, yyyy` if the year differs
            parsedDateTime.year != now.year -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.getDefault()))
            }
            else -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
            }
        }
    } catch (e: Exception) {
        timestamp // Fallback to original timestamp if parsing fails
    }
}