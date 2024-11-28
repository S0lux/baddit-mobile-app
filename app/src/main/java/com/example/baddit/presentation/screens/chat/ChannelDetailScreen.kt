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
import androidx.compose.material.icons.filled.Send
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
import androidx.navigation.NavController
import coil.compose.AsyncImage
import java.time.LocalDateTime
import kotlin.random.Random

// Update ChatMessage to include user avatar
data class ChatMessage(
    val id: String,
    val senderId: String,
    val senderAvatar: String,
    val content: String,
    val timestamp: LocalDateTime
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelDetailScreen(
    channelId: String,
    channelName: String,
    channelAvatar: String,
    navController: NavController
) {
    val currentUserId = "user_123"
    val fakeMessages = generateFakeMessages(50)
    val messageScrollState = rememberLazyListState()
    var newMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Updated TopAppBar with channel avatar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
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
                IconButton(onClick = { navController.navigateUp() }) {
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
            items(fakeMessages.reversed()) { message ->
                MessageItem(
                    message = message,
                    isMyMessage = message.senderId == currentUserId
                )
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

            IconButton(onClick = { /* Send message logic */ }) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message"
                )
            }
        }
    }
}

@Composable
fun MessageItem(message: ChatMessage, isMyMessage: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 350.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Show avatar for non-user messages
            if (!isMyMessage) {
                AsyncImage(
                    model = message.senderAvatar,
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Column(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (!isMyMessage) 0.dp else 16.dp,
                            bottomEnd = if (isMyMessage) 0.dp else 16.dp
                        )
                    )
                    .background(if (isMyMessage) Color.Blue.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f))
                    .padding(12.dp)
                    .widthIn(max = 300.dp)
            ) {
                Text(
                    text = message.content,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                Text(
                    text = formatTimestamp(message.timestamp),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light
                )
            }

            // Spacer for my messages to align right
            if (isMyMessage) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// Updated fake data generation functions
fun generateFakeMessages(count: Int): List<ChatMessage> {
    val users = listOf(
        UserData("user_123", "https://example.com/avatar1.jpg"),
        UserData("user_456", "https://example.com/avatar2.jpg"),
        UserData("user_789", "https://example.com/avatar3.jpg")
    )
    val messageTemplates = listOf(
        "Hey, how are you?",
        "Just checking in",
        "What's up?",
        "Long time no see!",
        "Did you hear about the latest news?",
        "I'm working on a new project",
        "Want to grab coffee sometime?",
        "This is an interesting conversation",
        "Tell me more about that",
        "Great idea!"
    )

    return (1..count).map { index ->
        val randomUser = users.random()
        ChatMessage(
            id = "msg_$index",
            senderId = randomUser.id,
            senderAvatar = randomUser.avatar,
            content = generateFakeMessageContent(messageTemplates),
            timestamp = LocalDateTime.now().minusMinutes(Random.nextLong(1, 1440))
        )
    }
}

// User data class to hold user information
data class UserData(
    val id: String,
    val avatar: String
)

fun generateFakeMessageContent(templates: List<String>): String {
    val baseMessage = templates.random()
    return if (Random.nextBoolean()) {
        baseMessage
    } else {
        "$baseMessage ${Lorem.words(Random.nextInt(1, 10))}"
    }
}

// Existing Lorem and timestamp formatter remain the same
object Lorem {
    private val wordList = listOf(
        "lorem", "ipsum", "dolor", "sit", "amet", "consectetur",
        "adipiscing", "elit", "sed", "do", "eiusmod", "tempor",
        "incididunt", "ut", "labore", "et", "dolore", "magna", "aliqua"
    )

    fun words(count: Int): String {
        return (1..count).map { wordList.random() }.joinToString(" ")
    }
}

fun formatTimestamp(timestamp: LocalDateTime): String {
    val hours = timestamp.hour.toString().padStart(2, '0')
    val minutes = timestamp.minute.toString().padStart(2, '0')
    return "$hours:$minutes"
}