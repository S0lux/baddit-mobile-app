import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baddit.R
import com.example.baddit.domain.model.friend.BaseFriendUser
import com.example.baddit.presentation.components.BaseTopNavigationBar
import com.example.baddit.presentation.screens.chat.ChannelListScreen
import com.example.baddit.presentation.screens.chat.ChatViewModel
import com.example.baddit.presentation.screens.chat.DiagonalOverlappingAvatars
import com.example.baddit.presentation.utils.ChannelList
import com.example.baddit.ui.theme.CustomTheme.cardBackground
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
//    var localChannelAvatar by remember { mutableStateOf(channelAvatar) }
    var isEditingName by remember { mutableStateOf(false) }

    var showAddMembersDialog by remember { mutableStateOf(false) }
    var showAddModeratorsDialog by remember { mutableStateOf(false) }
    var showRemoveMembersDialog by remember { mutableStateOf(false) }

    // Fetch channel details
    LaunchedEffect(channelId) {
        viewModel.fetchChannelDetail(channelId)
        viewModel.fetchAvailableFriends()
    }

    // Get current channel from cache
    val currentChannel = remember(viewModel.chatRepository.channelListCache) {
        viewModel.chatRepository.channelListCache.find { it.id == channelId }
    }

    // Check if current user is moderator
    val isCurrentUserModerator by remember {
        derivedStateOf { viewModel.isUserModerator(channelId) }
    }

    fun refreshData() {
        viewModel.fetchChannelDetail(channelId)
        viewModel.fetchAvailableFriends()
    }

    val context = LocalContext.current

    // Image upload launcher
    val avatarLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val file =
                    File(context.cacheDir, "channel_avatar_${System.currentTimeMillis()}.jpg")
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


    if (showRemoveMembersDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveMembersDialog = false },
            title = { Text("Remove Members") },
            text = {
                LazyColumn {
                    items(currentChannel?.members.orEmpty().filter { member ->
                        // Filter out:
                        // 1. Current user
                        // 2. Any user who is a moderator
                        member.id != viewModel.me.value!!.id &&
                                !currentChannel!!.moderators.any { mod -> mod.id == member.id }
                    }) { member ->
                        // Calculate remaining total members after potential removal
                        val totalRemainingMembers = currentChannel?.members?.size?.minus(
                            viewModel.selectedFriendsForChannel.size
                        ) ?: 0

                        // Can select if:
                        // 1. Removing this member would still leave at least 2 total members, or
                        // 2. This member is already selected
                        val canSelectMore = totalRemainingMembers > 2 ||
                                viewModel.selectedFriendsForChannel.any { it.id == member.id }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(enabled = canSelectMore) {
                                    viewModel.toggleFriendSelection(
                                        BaseFriendUser(
                                            id = member.id,
                                            username = member.username,
                                            avatarUrl = member.avatarUrl,
                                            status = "ACTIVE",
                                        )
                                    )
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = viewModel.selectedFriendsForChannel.contains(
                                    BaseFriendUser(
                                        id = member.id,
                                        username = member.username,
                                        avatarUrl = member.avatarUrl,
                                        status = "ACTIVE",
                                    )
                                ),
                                onCheckedChange = {
                                    if (canSelectMore) {
                                        viewModel.toggleFriendSelection(
                                            BaseFriendUser(
                                                id = member.id,
                                                username = member.username,
                                                avatarUrl = member.avatarUrl,
                                                status = "ACTIVE",
                                            )
                                        )
                                    }
                                },
                                enabled = canSelectMore
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = member.avatarUrl,
                                contentDescription = "Member Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = member.username)
                                if (!canSelectMore && !viewModel.selectedFriendsForChannel.any { it.id == member.id }) {
                                    Text(
                                        text = "Cannot remove - minimum 2 total members required",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                val totalRemainingMembers = currentChannel?.members?.size?.minus(
                    viewModel.selectedFriendsForChannel.size
                ) ?: 0

                TextButton(
                    onClick = {
                        if (totalRemainingMembers >= 2) {
                            val selectedMemberIds =
                                viewModel.selectedFriendsForChannel.map { it.id }
                            viewModel.removeMembersFromChannel(channelId, selectedMemberIds)
                            refreshData()
                            showRemoveMembersDialog = false
                        }
                    },
                    enabled = viewModel.selectedFriendsForChannel.isNotEmpty() && totalRemainingMembers >= 2
                ) {
                    Text("Remove")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRemoveMembersDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    if (showAddMembersDialog) {
        AlertDialog(
            onDismissRequest = { showAddMembersDialog = false },
            title = { Text("Add Members") },
            text = {
                // Add currentChannel?.members as a dependency to remember
                val availableFriendsFiltered = remember(
                    viewModel.availableFriends,
                    currentChannel?.members // Add this dependency
                ) {
                    viewModel.availableFriends.filter { friend ->
                        // Filter out existing members
                        currentChannel?.members?.none { it.id == friend.id } ?: true
                    }
                }

                LazyColumn {
                    items(availableFriendsFiltered) { friend ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.toggleFriendSelection(friend)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = viewModel.selectedFriendsForChannel.contains(friend),
                                onCheckedChange = {
                                    viewModel.toggleFriendSelection(friend)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = "Friend Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = friend.username)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Add selected friends as members
                        val selectedFriendIds = viewModel.selectedFriendsForChannel.map { it.id }
                        viewModel.addMembersToChannel(channelId, selectedFriendIds)
                        showAddMembersDialog = false
                        refreshData()
                    },
                    enabled = viewModel.selectedFriendsForChannel.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMembersDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
    // Add Moderators Dialog
    if (showAddModeratorsDialog) {
        AlertDialog(
            onDismissRequest = { showAddModeratorsDialog = false },
            title = { Text("Add Moderators") },
            text = {
                LazyColumn {
                    items(viewModel.availableFriends.filter { friend ->
                        // Filter out existing members and existing moderators
                        currentChannel?.moderators?.none { it.id == friend.id } ?: true
                    }) { friend ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.toggleFriendSelection(friend)
                                }
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = viewModel.selectedFriendsForChannel.contains(friend),
                                onCheckedChange = {
                                    viewModel.toggleFriendSelection(friend)
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            AsyncImage(
                                model = friend.avatarUrl,
                                contentDescription = "Friend Avatar",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = friend.username)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Add selected friends as moderators
                        val selectedFriendIds = viewModel.selectedFriendsForChannel.map { it.id }
                        viewModel.addModeratorsToChannel(channelId, selectedFriendIds)
                        showAddModeratorsDialog = false
                        refreshData()
                    },
                    enabled = viewModel.selectedFriendsForChannel.isNotEmpty()
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddModeratorsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.cardBackground), // Ensure full size constraint
    ) { paddingValues ->

        // Use Box to manage the dialog overlay
        Box(modifier = Modifier.fillMaxSize()) {
            // Main content
            Column {
                BaseTopNavigationBar(
                    title = "Channel Info",
                    leftIcon = R.drawable.baseline_arrow_back_24,
                    onLeftIconClick = { navController.popBackStack() })

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val channel =
                        viewModel.chatRepository.channelListCache.find { it.id == channelId }!!
                    val avatar = channel.avatarUrl
                    // Channel Avatar and Name
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (viewModel.isUploading) {
                                    // Show loading indicator when uploading
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .align(Alignment.Center)
                                    )
                                } else {
                                    if (channel.type == "DIRECT") {
                                        AsyncImage(
                                            model = channelAvatar,
                                            contentDescription = "$channelName avatar",
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
                                    } else {

                                        if (avatar == "https://placehold.co/400.png") {
                                            val currentChatChannel =
                                                viewModel.chatRepository.channelListCache.find { it.id == channelId }!!
                                            val members = currentChatChannel.members
                                            DiagonalOverlappingAvatars(
                                                avatars = listOf(
                                                    members[0].avatarUrl,
                                                    members[1].avatarUrl
                                                ),
                                                modifier = Modifier
                                                    .size(100.dp)
                                                    .clip(CircleShape)
                                                    .clickable {
                                                        if (isCurrentUserModerator) {
                                                            avatarLauncher.launch("image/*")
                                                        }
                                                    },
                                                overlap = 8.dp,
                                                size = 100.dp
                                            )
                                        } else {

                                            AsyncImage(
                                                model = avatar,
                                                contentDescription = "$channelName avatar",
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
                                        }
                                    }
                                }
                            }
                            Column {
                                if (isEditingName && isCurrentUserModerator) {
                                    TextField(
                                        value = localChannelName,
                                        onValueChange = { localChannelName = it },
                                        label = { Text("Channel Name") },
                                        trailingIcon = {
                                            Row {
                                                TextButton(onClick = {
                                                    viewModel.updateChannelName(
                                                        channelId,
                                                        localChannelName
                                                    )
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Members (${currentChannel?.members?.size ?: 0})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            // Add Members Button (Only for Moderators)
                            if (isCurrentUserModerator) {
                                IconButton(
                                    onClick = {
                                        // Reset selected friends before opening dialog
                                        viewModel.selectedFriendsForChannel = emptyList()
                                        showAddMembersDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Members")
                                }
                            }
                        }
                    }
                    items(channel.members) { member ->
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

                            // Add remove member button for moderators
                            if (isCurrentUserModerator && member.id != viewModel.me.value!!.id
                                && !channel.moderators.map { it.id }.contains(member.id)
                            ) {
                                Spacer(modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = {
                                        // Reset selected friends and open remove members dialog
                                        viewModel.selectedFriendsForChannel = listOf(
                                            BaseFriendUser(
                                                id = member.id,
                                                username = member.username,
                                                avatarUrl = member.avatarUrl,
                                                status = "ACTIVE",
                                            )
                                        )
                                        showRemoveMembersDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove Member")
                                }
                            }
                        }
                    }

                    // Moderators Section
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Moderators (${currentChannel?.moderators?.size ?: 0})",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            // Add Moderators Button (Only for Moderators)
                            if (isCurrentUserModerator) {
                                IconButton(
                                    onClick = {
                                        // Reset selected friends before opening dialog
                                        viewModel.selectedFriendsForChannel = emptyList()
                                        showAddModeratorsDialog = true
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Add Moderators")
                                }
                            }
                        }
                    }
                    items(channel.moderators) { moderator ->
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
                                    navController.navigate(ChannelList)
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
}