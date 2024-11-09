package com.example.baddit.presentation.components

import android.os.Build.VERSION.SDK_INT
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.posts.MutablePostResponseDTOItem
import com.example.baddit.presentation.utils.CommunityDetail
import com.example.baddit.presentation.utils.InvalidatingPlacementModifierElement
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.appOrange
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.cardForeground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import getTimeAgoFromUtcString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun PostCard(
    postDetails: MutablePostResponseDTOItem,
    loggedIn: Boolean = false,
    isExpanded: Boolean = false,
    navigateLogin: () -> Unit,
    votePostFn: suspend (voteState: String) -> Result<Unit, DataError.NetworkError>,
    navigatePost: (String) -> Unit,
    setVoteState: (String?) -> Unit,
    setPostScore: (Int) -> Unit,
    loggedInUser: GetMeResponseDTO?,
    deletePostFn: suspend (String) -> Unit,
    navigateEdit: (String) -> Unit,
    navigateReply: (String) -> Unit,
    onComponentClick:()->Unit,
    navController: NavController
) {
    val colorUpvote = MaterialTheme.colorScheme.appOrange
    val colorDownvote = MaterialTheme.colorScheme.appBlue

    val voteInteractionSource = remember { MutableInteractionSource() }
    var voteElementSize by remember { mutableStateOf(IntSize.Zero) }
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    var hasUserInteracted by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showLoginDialog) {
        LoginDialog(navigateLogin = { navigateLogin() }, onDismiss = { showLoginDialog = false })
    }

    LaunchedEffect(postDetails.voteState.value) {
        if (hasUserInteracted && postDetails.voteState.value != null) {
            val pressPosition = Offset(
                x = voteElementSize.width / if (postDetails.voteState.value == "UPVOTE") 6f else 1f,
                y = voteElementSize.height / 2f
            )
            val press = PressInteraction.Press(pressPosition)
            voteInteractionSource.emit(press)
            delay(300)
            voteInteractionSource.emit(PressInteraction.Release(press))
        }
    }

    fun onUpvote() {
        hasUserInteracted = true
        if (!loggedIn) {
            showLoginDialog = true
            return
        }
        when (postDetails.voteState.value) {
            "UPVOTE" -> {
                setVoteState(null)
                setPostScore(postDetails.score.value - 1)
                handleVote(
                    voteState = "UPVOTE",
                    onError = { setPostScore(postDetails.score.value + 1); setVoteState("UPVOTE") },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            "DOWNVOTE" -> {
                setVoteState("UPVOTE")
                setPostScore(postDetails.score.value + 2)
                handleVote(
                    voteState = "UPVOTE",
                    onError = { setPostScore(postDetails.score.value - 2); setVoteState("DOWNVOTE") },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            else -> {
                setVoteState("UPVOTE")
                setPostScore(postDetails.score.value + 1)
                handleVote(
                    voteState = "UPVOTE",
                    onError = { setPostScore(postDetails.score.value - 1); setVoteState(null) },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }
        }
    }

    fun onDownvote() {
        hasUserInteracted = true
        if (!loggedIn) {
            showLoginDialog = true
            return
        }
        when (postDetails.voteState.value) {
            "UPVOTE" -> {
                setVoteState("DOWNVOTE")
                setPostScore(postDetails.score.value - 2)
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { setPostScore(postDetails.score.value + 2); setVoteState("UPVOTE") },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            "DOWNVOTE" -> {
                setVoteState(null)
                setPostScore(postDetails.score.value + 1)
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { setPostScore(postDetails.score.value - 1); setVoteState("DOWNVOTE") },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            else -> {
                setVoteState("DOWNVOTE")
                setPostScore(postDetails.score.value - 1)
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { setPostScore(postDetails.score.value + 1); setVoteState(null) },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }
        }
    }

    val upvoteSwipe = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_upvote),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .offset(20.dp)
            )
        },
        background = colorUpvote,
        onSwipe = { onUpvote() },
        weight = 1.0,
    )

    val downvoteSwipe = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_downvote),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(32.dp)
                    .offset(20.dp)
            )
        },
        background = colorDownvote,
        onSwipe = { onDownvote() },
        weight = 3.0,
    )

    SwipeableActionsBox(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.cardBackground)
            .fillMaxWidth()
            .clickable { onComponentClick(); navigatePost(postDetails.id) },
        endActions = listOf(upvoteSwipe, downvoteSwipe),
        swipeThreshold = 40.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(15.dp)
        ) {
            PostHeader(postDetails = postDetails, navController)

            PostTitle(title = postDetails.title)

            if (postDetails.type == "TEXT") {
                PostTextContent(content = postDetails.content.value, isExpanded)
            }

            if (postDetails.type == "MEDIA") {
                PostMediaContent(mediaUrls = postDetails.mediaUrls)
            }

            PostActions(
                voteState = postDetails.voteState.value,
                postScore = postDetails.score.value,
                postId = postDetails.id,
                voteInteractionSource = voteInteractionSource,
                colorUpvote = colorUpvote,
                colorDownvote = colorDownvote,
                onUpvote = { onUpvote() },
                onDownvote = { onDownvote() },
                commentCount = postDetails.commentCount.value,
                onGloballyPositioned = { cords -> voteElementSize = cords.size },
                loggedInUser = loggedInUser,
                showLoginDialog = { showLoginDialog = true },
                postAuthor = postDetails.author.username,
                postCommunity = postDetails.community?.name,
                navigateEdit = navigateEdit,
                navigateReply = navigateReply,
                deletePostFn = deletePostFn,
                coroutineScope = coroutineScope,
                postType = postDetails.type
            )
        }
    }
}

fun handleVote(
    voteState: String,
    onError: () -> Unit,
    coroutineScope: CoroutineScope,
    voteFn: suspend (voteState: String) -> Result<Unit, DataError.NetworkError>
) {
    coroutineScope.launch {
        val result = voteFn(voteState)
        if (result is Result.Error) {
            onError()
        }
    }
}

@Composable
fun PostHeader(postDetails: MutablePostResponseDTOItem, navController: NavController) {
    val communityName = postDetails.community?.name.orEmpty()
    val communityLogo = postDetails.community?.logoUrl.orEmpty()
    val authorName = postDetails.author.username
    val authorUrl = postDetails.author.avatarUrl
    val titleText = communityName.ifEmpty {
        authorName
    }
    val subReddit = if (communityName.isEmpty()) {
        "u/"
    } else {
        "r/"
    }
    val logo = communityLogo.ifEmpty {
        authorUrl
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable {
            if(communityName.isNotEmpty()){
                navController.navigate(CommunityDetail(communityName))
            }
            else{
                navController.navigate(Profile(authorName))
            }
        }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(logo).build(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .height(36.dp)
                .aspectRatio(1f),
            contentScale = ContentScale.Fit
        )

        Column {
            Row {
                Text(
                    subReddit,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )

                Text(
                    titleText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2196F3),
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(
                    postDetails.author.username,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    modifier = Modifier.padding(0.dp),
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Text(
                    "•",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
                Text(
                    getTimeAgoFromUtcString(postDetails.createdAt),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.textSecondary,
                    style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
                )
            }
        }
    }
}

@Composable
fun PostTitle(title: String) {
    Text(
        title, color = MaterialTheme.colorScheme.textPrimary, fontSize = 17.sp, lineHeight = 20.sp
    )
}

@Composable
fun PostTextContent(content: String, isExpanded: Boolean) {
    Text(
        content,
        color = MaterialTheme.colorScheme.textSecondary,
        fontSize = 12.sp,
        lineHeight = 14.sp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10))
            .then(InvalidatingPlacementModifierElement())
            .background(MaterialTheme.colorScheme.cardForeground)
            .padding(5.dp),
        maxLines = if (!isExpanded) 3 else 100,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun PostMediaContent(mediaUrls: List<String>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .then(InvalidatingPlacementModifierElement())
            .background(MaterialTheme.colorScheme.cardForeground)
            .fillMaxWidth()
            .heightIn(50.dp, 400.dp), contentAlignment = Alignment.Center
    ) {
        val context = LocalContext.current
        val imageLoader = ImageLoader.Builder(context).components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(mediaUrls.first()).build(),
            imageLoader = imageLoader,
            contentDescription = null,
            modifier = Modifier
                .heightIn(50.dp, 450.dp),
            contentScale = ContentScale.Fit
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostActions(
    voteState: String?,
    postScore: Int,
    postAuthor: String,
    postCommunity: String?,
    postType: String,
    postId: String,
    voteInteractionSource: MutableInteractionSource,
    colorUpvote: Color,
    colorDownvote: Color,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit,
    commentCount: Int,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
    loggedInUser: GetMeResponseDTO?,
    showLoginDialog: () -> Unit,
    deletePostFn: suspend (String) -> Unit,
    navigateEdit: (String) -> Unit,
    navigateReply: (String) -> Unit,
    coroutineScope: CoroutineScope
) {
    var showModal by remember { mutableStateOf(false) }

    if (showModal) {
        ModalBottomSheet(onDismissRequest = { showModal = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Post options",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.textPrimary),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = {
                        if (loggedInUser == null) {
                            showLoginDialog()
                            return@Button
                        }

                        navigateReply(postId)
                        showModal = false
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.reply),
                            contentDescription = null,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "Reply")
                    }

                }
                if (loggedInUser?.username == postAuthor && postType != "MEDIA") {
                    Button(
                        onClick = {
                            navigateEdit(postId)
                            showModal = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.textPrimary,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Edit")
                        }

                    }
                }

                if (loggedInUser?.username == postAuthor ||
                    loggedInUser?.communities?.find { it.name == postCommunity }?.role == "MODERATOR" ||
                    loggedInUser?.communities?.find { it.name == postCommunity }?.role == "ADMIN" ||
                    loggedInUser?.role == "ADMIN") {
                    Button(
                        onClick = {
                            coroutineScope.launch { deletePostFn(postId) }
                            showModal = false
                        },
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = ButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.textPrimary,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.trash),
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = "Delete")
                        }

                    }
                }
            }
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .then(InvalidatingPlacementModifierElement())
                .onGloballyPositioned(onGloballyPositioned)
                .clickable(
                    onClick = {}, interactionSource = voteInteractionSource, indication = ripple(
                        bounded = true,
                        color = if (voteState == "UPVOTE") colorUpvote else colorDownvote
                    )
                )
                .background(MaterialTheme.colorScheme.cardForeground)
                .padding(4.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_upvote),
                contentDescription = null,
                tint = if (voteState == "UPVOTE") colorUpvote else MaterialTheme.colorScheme.textSecondary,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onUpvote
                )
            )
            Text(
                postScore.toString(),
                color = MaterialTheme.colorScheme.textPrimary,
                fontSize = 12.sp,
            )
            Icon(
                painter = painterResource(id = R.drawable.arrow_downvote),
                contentDescription = null,
                tint = if (voteState == "DOWNVOTE") colorDownvote else MaterialTheme.colorScheme.textSecondary,
                modifier = Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = onDownvote
                )
            )
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(10))
                .then(InvalidatingPlacementModifierElement())
                .background(MaterialTheme.colorScheme.cardForeground)
                .padding(bottom = 4.dp, top = 4.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.comment),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.textSecondary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                commentCount.toString(),
                color = MaterialTheme.colorScheme.textPrimary,
                fontSize = 12.sp,
            )
        }
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
            Icon(
                painter = painterResource(id = R.drawable.vertical_dots),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.textSecondary,
                modifier = Modifier.clickable { showModal = true }
            )
        }
    }
}

@Composable
fun LoginDialog(navigateLogin: () -> Unit, onDismiss: () -> Unit) {
    BadditDialog(title = "Login required",
        text = "You need to login to perform this action.",
        confirmText = "Login",
        dismissText = "Cancel",
        onConfirm = { navigateLogin() },
        onDismiss = { onDismiss() })
}