package com.example.baddit.presentation.components

import android.content.res.Configuration
import android.os.Build.VERSION.SDK_INT
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.posts.Author
import com.example.baddit.domain.model.posts.Community
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.ui.theme.BadditTheme
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
    postDetails: PostResponseDTOItem,
    loggedIn: Boolean = false,
    isExpanded: Boolean = false,
    navigateLogin: () -> Unit,
    votePostFn: suspend (voteState: String) -> Result<Unit, DataError.NetworkError>,
    navigatePost: (PostResponseDTOItem) -> Unit,
) {
    val colorUpvote = MaterialTheme.colorScheme.appOrange
    val colorDownvote = MaterialTheme.colorScheme.appBlue

    val voteInteractionSource = remember { MutableInteractionSource() }
    var voteState by rememberSaveable { mutableStateOf(postDetails.voteState) }
    var postScore by rememberSaveable { mutableIntStateOf(postDetails.score) }
    var voteElementSize by remember { mutableStateOf(IntSize.Zero) }
    var showLoginDialog by rememberSaveable { mutableStateOf(false) }
    var hasUserInteracted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    if (showLoginDialog) {
        LoginDialog(navigateLogin = { navigateLogin() }, onDismiss = { showLoginDialog = false })
    }

    LaunchedEffect(voteState) {
        if (hasUserInteracted && voteState != null) {
            val pressPosition = Offset(
                x = voteElementSize.width / if (voteState == "UPVOTE") 6f else 1f,
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
        when (voteState) {
            "UPVOTE" -> {
                voteState = null
                postScore--
                handleVote(
                    voteState = "UPVOTE",
                    onError = { postScore++; voteState = "UPVOTE" },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            "DOWNVOTE" -> {
                voteState = "UPVOTE"
                postScore += 2
                handleVote(
                    voteState = "UPVOTE",
                    onError = { postScore -= 2; voteState = "DOWNVOTE" },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            else -> {
                voteState = "UPVOTE"
                postScore++
                handleVote(
                    voteState = "UPVOTE",
                    onError = { postScore--; voteState = null },
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
        when (voteState) {
            "UPVOTE" -> {
                voteState = "DOWNVOTE"
                postScore -= 2
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { postScore += 2; voteState = "UPVOTE" },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            "DOWNVOTE" -> {
                voteState = null
                postScore++
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { postScore--; voteState = "DOWNVOTE" },
                    coroutineScope = coroutineScope,
                    voteFn = votePostFn
                )
            }

            else -> {
                voteState = "DOWNVOTE"
                postScore--
                handleVote(
                    voteState = "DOWNVOTE",
                    onError = { postScore++; voteState = null },
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
                modifier = Modifier.size(32.dp).offset(20.dp)
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
                modifier = Modifier.size(32.dp).offset(20.dp)
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
            .clickable { navigatePost(postDetails) },
        endActions = listOf(upvoteSwipe, downvoteSwipe),
        swipeThreshold = 40.dp
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .padding(15.dp)
        ) {
            PostHeader(postDetails = postDetails)

            PostTitle(title = postDetails.title)

            if (postDetails.type == "TEXT") {
                PostTextContent(content = postDetails.content, isExpanded)
            }

            if (postDetails.type == "MEDIA") {
                PostMediaContent(mediaUrls = postDetails.mediaUrls)
            }

            PostActions(
                voteState = voteState?.toString(),
                postScore = postScore,
                voteInteractionSource = voteInteractionSource,
                colorUpvote = colorUpvote,
                colorDownvote = colorDownvote,
                onUpvote = { onUpvote() },
                onDownvote = { onDownvote() },
                commentCount = postDetails.commentCount,
                onGloballyPositioned = { cords -> voteElementSize = cords.size },
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
fun PostHeader(postDetails: PostResponseDTOItem) {
    val communityName = postDetails.community?.name.orEmpty()
    val communityLogo = postDetails.community?.logoUrl.orEmpty()
    val authorName = postDetails.author.username
    val authorUrl = postDetails.author.avatarUrl
    val titleText = if (communityName.isEmpty()) {
        authorName
    } else {
        communityName
    }
    val subReddit = if (communityName.isEmpty()) {
        "u/"
    } else {
        "r/"
    }
    val logo = if (communityLogo.isEmpty()) {
        authorUrl
    } else {
        communityLogo
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(logo).build(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .height(36.dp)
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
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
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun PostActions(
    voteState: String?,
    postScore: Int,
    voteInteractionSource: MutableInteractionSource,
    colorUpvote: Color,
    colorDownvote: Color,
    onUpvote: () -> Unit,
    onDownvote: () -> Unit,
    commentCount: Int,
    onGloballyPositioned: (LayoutCoordinates) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .clip(RoundedCornerShape(10))
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

@Preview(showBackground = true)
@Composable
fun PostCardPreview() {
    val details = PostResponseDTOItem(
        id = "992e0a44-6682-4d13-b75e-834494679b65",
        type = "TEXT",
        title = "How the hell do I use this app? The mobile design absolutely sucks!!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        score = 0,
        voteState = null,
        commentCount = 0,
        author = Author(
            id = "b68eccfa-aa50-44fb-bffd-68fbd719d561",
            username = "trungkhang1",
            avatarUrl = "https://placehold.co/400.png"
        ),
        community = Community(
            name = "pesocommunity", logoUrl = "https://placehold.co/400.png"
        ),
        createdAt = "2024-05-13T05:57:03.877Z",
        updatedAt = "2024-05-13T05:57:03.877Z",
        mediaUrls = ArrayList(),
    )
    BadditTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                PostCard(
                    details,
                    navigateLogin = { },
                    votePostFn = { Result.Success(Unit) },
                    navigatePost = { })
            }
        }
    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun PostCardDarkPreview() {
    val details = PostResponseDTOItem(
        id = "992e0a44-6682-4d13-b75e-834494679b65",
        type = "TEXT",
        title = "How the hell do I use this app? The mobile design absolutely sucks!!",
        content = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.",
        score = 0,
        voteState = null,
        commentCount = 0,
        author = Author(
            id = "b68eccfa-aa50-44fb-bffd-68fbd719d561",
            username = "trungkhang1",
            avatarUrl = "https://placehold.co/400.png"
        ),
        community = Community(
            name = "pesocommunity", logoUrl = "https://placehold.co/400.png"
        ),
        createdAt = "2024-05-13T05:57:03.877Z",
        updatedAt = "2024-05-13T05:57:03.877Z",
        mediaUrls = ArrayList(),
    )
    BadditTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                PostCard(
                    details,
                    navigateLogin = { },
                    votePostFn = { Result.Success(Unit) },
                    navigatePost = { })
            }
        }
    }
}

@Preview
@Composable
fun DialogPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                LoginDialog(navigateLogin = { /*TODO*/ }) {

                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DialogDarkPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(contentAlignment = Alignment.Center) {
                LoginDialog(navigateLogin = { /*TODO*/ }) {

                }
            }
        }
    }
}
