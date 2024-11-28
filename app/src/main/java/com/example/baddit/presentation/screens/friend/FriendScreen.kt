import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.baddit.R
import com.example.baddit.domain.model.friend.BaseFriendUser
import com.example.baddit.presentation.components.AnimatedLogo
import com.example.baddit.presentation.components.BaseTopNavigationBar
import com.example.baddit.presentation.screens.friend.FriendViewModel
import com.example.baddit.presentation.screens.profile.IconMenuItem
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    navController: NavController,
    viewModel: FriendViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedFriend: BaseFriendUser? = null
    var showFriendModal by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        coroutineScope.launch {
            viewModel.updateFriendsInfo()
        }
    }

    Scaffold(
        modifier = Modifier
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.cardBackground)
        ) {
            BaseTopNavigationBar(
                title = "Friends",
                leftIcon = R.drawable.baseline_arrow_back_24,
                onLeftIconClick = { navController.popBackStack() }
            )

            if (viewModel.currentFriends.isEmpty() && viewModel.incomingRequests.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(1F),
                    contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        AnimatedLogo(R.raw.sad, size = 125.dp, tintColor = MaterialTheme.colorScheme.textSecondary)
                        Text("You have no friend",
                            color = MaterialTheme.colorScheme.textSecondary,
                            fontWeight = FontWeight.Medium)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = padding.calculateStartPadding(LayoutDirection.Ltr),
                        end = padding.calculateStartPadding(LayoutDirection.Ltr))
            ) {
                // Pending Friend Requests Section
                if (viewModel.incomingRequests.isNotEmpty()) {
                    item {
                        Text(
                            text = "Pending",
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                    items(viewModel.incomingRequests) { request ->
                        PendingFriendRequestItem(
                            request = request.sender,
                            onAccept = { coroutineScope.launch { viewModel.acceptFriendRequest(request.sender.id) } },
                            onReject = { coroutineScope.launch { viewModel.rejectFriendRequest(request.sender.id) } },
                        )
                    }
                }

                // Current Friends Section
                if (viewModel.currentFriends.isNotEmpty()) {
                    item {
                        Text(
                            text = "Your Friends",
                            fontWeight = FontWeight.Medium,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(15.dp)
                        )
                    }
                    items(viewModel.currentFriends) { friend ->
                        FriendItem(
                            friend = friend,
                            onClick = {
                                selectedFriend = friend
                                showFriendModal = true
                            }
                        )
                    }
                }
            }

            // Friend Options Bottom Sheet
            if (showFriendModal && selectedFriend != null) {
                ModalBottomSheet(
                    onDismissRequest = { showFriendModal = false },
                    sheetState = rememberModalBottomSheetState(),
                    containerColor = MaterialTheme.colorScheme.cardBackground
                ) {
                    FriendOptionsBottomSheetContent(
                        friend = selectedFriend!!,
                        onUnfriend = {
                            coroutineScope.launch { viewModel.removeFriend(selectedFriend!!.id) }
                            showFriendModal = false
                        },
                        onBlock = {
                            TODO()
                            showFriendModal = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PendingFriendRequestItem(
    request: BaseFriendUser,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(request.avatarUrl),
            contentDescription = "${request.username}'s avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = request.username,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        IconButton(onClick = onAccept) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Accept Friend Request",
                tint = MaterialTheme.colorScheme.textPrimary
            )
        }

        IconButton(onClick = onReject) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Reject Friend Request",
                tint = MaterialTheme.colorScheme.textPrimary
            )
        }
    }
}

@Composable
fun FriendItem(
    friend: BaseFriendUser,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(friend.avatarUrl),
            contentDescription = "${friend.username}'s avatar",
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = friend.username,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun FriendOptionsBottomSheetContent(
    friend: BaseFriendUser,
    onUnfriend: () -> Unit,
    onBlock: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = friend.username,
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 6.dp),

            )
        }

        IconMenuItem(
            icon = R.drawable.remove_user,
            text = "Unfriend",
            tint = MaterialTheme.colorScheme.textPrimary,
            iconGap = 12.dp,
            onClick = onUnfriend
        )

        IconMenuItem(
            icon = R.drawable.block,
            text = "Block",
            tint = MaterialTheme.colorScheme.textPrimary,
            iconGap = 12.dp,
            onClick = onBlock
        )
    }
}