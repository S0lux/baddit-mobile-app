package com.example.baddit.presentation.screens.chat

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baddit.R
import com.example.baddit.domain.model.chat.chatMessage.MessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.MutableMessageResponseDTOItem
import com.example.baddit.domain.model.chat.chatMessage.Sender
import com.example.baddit.presentation.utils.ChannelInfo
import kotlinx.coroutines.launch
import java.io.File
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


    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        val imageFiles = uris.mapNotNull { uri ->
            try {
                // Convert URI to File
                val file = File(context.cacheDir, "uploaded_image_${System.currentTimeMillis()}")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                file
            } catch (e: Exception) {
                null
            }
        }

        viewModel.uploadChatImages(channelId, imageFiles)
    }

    val canSendMessage = !viewModel.isUploading &&
            (newMessage.isNotBlank() || viewModel.uploadedImageUrls.isNotEmpty())

    LaunchedEffect(channelId) {
        viewModel.clearPreviousMessages()

        viewModel.chatRepository.channelMessageCache.clear()

        viewModel.connectToChannel(channelId)

        viewModel.fetchChannelDetail(channelId)
    }

    LaunchedEffect(socketMessages.size) {
        if (socketMessages.isNotEmpty()) {
            coroutineScope.launch {
                messageScrollState.scrollToItem(0)
            }
        }
    }

    val filteredMessages =
        (viewModel.chatRepository.channelMessageCache + viewModel.socketMessages)
            .filter { message ->
                message.channelId == channelId
            }
            .distinctBy { it.id }
            .map { it as MutableMessageResponseDTOItem }
            .sortedByDescending { it.createdAt }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val channel =
            viewModel.chatRepository.channelListCache.find { it.id == channelId }!!
        val avatar = channel.avatarUrl
        // TopAppBar
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (channel.type == "DIRECT") {
                        AsyncImage(
                            model = channelAvatar,
                            contentDescription = "$channelName avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        if (avatar != "https://placehold.co/400.png") {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "$channelName avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val currentChatChannel =
                                viewModel.chatRepository.channelListCache.find { it.id == channelId }!!
                            val members = currentChatChannel.members
                            DiagonalOverlappingAvatars(
                                avatars = listOf(members[0].avatarUrl, members[1].avatarUrl),
                                modifier = Modifier.size(50.dp),
                                overlap = 6.dp,
                                size = 50.dp
                            )
                        }
                    }
                    if (channel.type == "GROUP") {
                        Text(channel.name)
                    } else {
                        Text(channelName)
                    }
                }
            },
            navigationIcon = {
                IconButton(onClick = {
                    navController.navigateUp()
                    viewModel.disconnectFromChannel()
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(
                    onClick = {
                        navController.navigate(
                            ChannelInfo(
                                channelId = channelId,
                                channelName = channelName,
                                channelAvatar = channelAvatar
                            )
                        )
                    }
                ) {
                    Icon(Icons.Default.Info, contentDescription = "Channel Info")
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


            var previousSenderId: String? = null
            items(filteredMessages) { message ->
                val showAvatar = previousSenderId != message.sender.id
                MessageItem(
                    message = message,
                    isMyMessage = message.sender.id == currentUserId,
                    showAvatar = showAvatar
                )
                previousSenderId = message.sender.id
            }
        }

        if (viewModel.uploadedImageUrls.isNotEmpty()) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.uploadedImageUrls) { imageUrl ->
                    Box(modifier = Modifier.size(80.dp)) {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "Uploaded image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = {
                                viewModel.uploadedImageUrls =
                                    viewModel.uploadedImageUrls.filter { it != imageUrl }
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Picker Button
            IconButton(
                onClick = { launcher.launch("image/*") }
            ) {
                Icon(Icons.Default.AddCircle, contentDescription = "Pick Images")
            }

            TextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                placeholder = { Text("Type a message") },
                singleLine = false,
                maxLines = 4
            )

            IconButton(
                onClick = {
                    val me = viewModel.me.value
                    if (me != null) {
                        val messageContent = buildString {
                            if (newMessage.isNotBlank()) append(newMessage)
                        }

                        viewModel.sendMessageToChannel(
                            channelId,
                            messageContent,
                            Sender(me.id, me.username, me.avatarUrl)
                        )

                        newMessage = ""
                        viewModel.uploadedImageUrls = emptyList()
                    }
                },
                enabled = canSendMessage
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
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = if (isMyMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.widthIn(max = 350.dp),
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
                // Image gallery for image messages
                if (message.mediaUrls.isNotEmpty()) {
                    ImageGallery(
                        imageUrls = message.mediaUrls,
                        onImageClick = { url -> selectedImageUrl = url }
                    )
                }

                // Text content (if not empty)
                if (message.content.isNotBlank() &&
                    (message.type == "TEXT" || (message.type == "IMAGE" && message.mediaUrls.isEmpty()))
                ) {
                    Text(
                        text = message.content,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 16.sp
                    )
                }

                // Timestamp
                Text(
                    text = formatMessageTimestamp(message.createdAt),
                    color = Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        // Image Viewer Dialog
        selectedImageUrl?.let { url ->
            ImageViewerDialog(
                imageUrl = url,
                onDismiss = { selectedImageUrl = null }
            )
        }
    }
}


// Helper function to format timestamp
fun formatMessageTimestamp(timestamp: String): String {
    return try {
        val parsedDateTime =
            LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME) // Parse the timestamp
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
                parsedDateTime.format(
                    DateTimeFormatter.ofPattern(
                        "MMM d, yyyy",
                        Locale.getDefault()
                    )
                )
            }

            else -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
            }
        }
    } catch (e: Exception) {
        timestamp // Fallback to original timestamp if parsing fails
    }
}