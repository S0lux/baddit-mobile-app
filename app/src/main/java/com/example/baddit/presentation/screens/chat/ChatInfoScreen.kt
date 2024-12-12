import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baddit.presentation.screens.chat.ChatViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChannelInfoScreen(
    channelId: String,
    channelName: String,
    channelAvatar: String,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // State for editing
    var localChannelName by remember { mutableStateOf(channelName) }
    var localChannelAvatar by remember { mutableStateOf(channelAvatar) }
    var isEditingName by remember { mutableStateOf(false) }

    // Fetch channel details
    LaunchedEffect(channelId) {
        viewModel.fetchChannelDetail(channelId)
    }

    // Get current channel from cache
    val currentChannel = remember(viewModel.chatRepository.channelListCache) {
        viewModel.chatRepository.channelListCache.find { it.id == channelId }
    }

    // Check if current user is moderator
    val isCurrentUserModerator by remember {
        derivedStateOf { viewModel.isUserModerator(channelId) }
    }

    val context = LocalContext.current

    // Image upload launcher
    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val file = File(context.cacheDir, "channel_avatar_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.uploadChannelAvatar(channelId, file)
            } catch (e: Exception) {
                // Handle upload error
                viewModel.error = "Failed to upload avatar: ${e.message}"
            }
        }
    }

    // Delete channel confirmation state
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(), // Ensure full size constraint
        topBar = {
            TopAppBar(
                title = { Text("Channel Info") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        // Use Box to manage the dialog overlay
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Channel Avatar and Name
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AsyncImage(
                            model = localChannelAvatar,
                            contentDescription = "Channel Avatar",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .clickable {
                                    if (isCurrentUserModerator) {
                                        avatarLauncher.launch("image/*")
                                    }
                                },
                            contentScale = ContentScale.Crop
                        )

                        Column {
                            if (isEditingName && isCurrentUserModerator) {
                                TextField(
                                    value = localChannelName,
                                    onValueChange = { localChannelName = it },
                                    label = { Text("Channel Name") },
                                    trailingIcon = {
                                        Row {
                                            TextButton(onClick = {
                                                viewModel.updateChannelName(channelId, localChannelName)
                                                isEditingName = false
                                            }) {
                                                Text("Save")
                                            }
                                            TextButton(onClick = {
                                                localChannelName = channelName
                                                isEditingName = false
                                            }) {
                                                Text("Cancel")
                                            }
                                        }
                                    }
                                )
                            } else {
                                Text(
                                    text = localChannelName,
                                    style = MaterialTheme.typography.headlineSmall,
                                    modifier = Modifier.clickable {
                                        if (isCurrentUserModerator) {
                                            isEditingName = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // Members Section
                item {
                    Text(
                        text = "Members (${currentChannel?.members?.size ?: 0})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(currentChannel?.members ?: emptyList()) { member ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = member.avatarUrl,
                            contentDescription = "Member Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = member.username)

                        // Add moderator button for current moderators
                        if (isCurrentUserModerator) {
                            Spacer(modifier = Modifier.weight(1f))
                            IconButton(
                                onClick = {
                                    // TODO: Implement add/remove moderator functionality
                                }
                            ) {
                                Icon(Icons.Default.Person, contentDescription = "Manage Moderator")
                            }
                        }
                    }
                }

                // Moderators Section
                item {
                    Text(
                        text = "Moderators (${currentChannel?.moderators?.size ?: 0})",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                items(currentChannel?.moderators ?: emptyList()) { moderator ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = moderator.avatarUrl,
                            contentDescription = "Moderator Avatar",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = moderator.username)
                    }
                }

                // Media Gallery
                item {
                    Text(
                        text = "Shared Media",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                item {
                    val mediaUrls = remember(viewModel.chatRepository.channelMessageCache) {
                        viewModel.chatRepository.channelMessageCache
                            .flatMap { it.mediaUrls }
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier.heightIn(max = 300.dp), // Limit height
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(mediaUrls) { mediaUrl ->
                            AsyncImage(
                                model = mediaUrl,
                                contentDescription = "Shared Media",
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                // Delete Channel Button (Only for Moderators)
                if (isCurrentUserModerator) {
                    item {
                        Button(
                            onClick = { showDeleteConfirmation = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Channel")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delete Channel")
                        }
                    }
                }
            }

            // Delete Channel Confirmation Dialog
            if (showDeleteConfirmation) {
                AlertDialog(
                    onDismissRequest = { showDeleteConfirmation = false },
                    title = { Text("Delete Channel") },
                    text = { Text("Are you sure you want to delete this channel? This action cannot be undone.") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteChannel(channelId)
                                navController.navigateUp()
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteConfirmation = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Error Handling
            viewModel.error.takeIf { it.isNotBlank() }?.let { errorMessage ->
                AlertDialog(
                    onDismissRequest = { viewModel.error = "" },
                    title = { Text("Error") },
                    text = { Text(errorMessage) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.error = "" }) {
                            Text("OK")
                        }
                    }
                )
            }
        }
    }
}