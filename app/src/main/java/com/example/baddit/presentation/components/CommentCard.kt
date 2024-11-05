package com.example.baddit.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baddit.R
import com.example.baddit.domain.error.DataError
import com.example.baddit.domain.error.Result
import com.example.baddit.domain.model.comment.Author
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.ui.theme.BadditTheme
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.appOrange
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import getTimeAgoFromUtcString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun CommentCard(
    details: CommentResponseDTOItem,
    level: Int = 1,
    navigateLogin: () -> Unit,
    navigateReply: (String, String) -> Unit,
    voteFn: suspend (String, String) -> Result<Unit, DataError.NetworkError>,
    isLoggedIn: Boolean = false,
    onComponenClick:()->Unit
) {
    val commentHoldDuration = 400L
    val colorUpvote = MaterialTheme.colorScheme.appOrange
    val colorDownvote = MaterialTheme.colorScheme.appBlue

    var voteState by remember { mutableStateOf(details.voteState) }
    var scoreState by remember { mutableStateOf(details.score) }
    var collapsedState by remember { mutableStateOf(false) }
    var actionMenuState by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }

    fun upVote() = handleVote(
        isLoggedIn = isLoggedIn,
        showLoginPrompt = { showLoginDialog = true },
        newVoteState = "UPVOTE",
        currentCommentId = details.id,
        currentVoteState = voteState,
        currentScoreState = scoreState,
        setCommentVoteState = { state: String? -> voteState = state },
        setCommentScoreState = { score: Int -> scoreState = score },
        voteFn = voteFn,
        coroutineScope = coroutineScope
    )

    fun downVote() = handleVote(
        isLoggedIn = isLoggedIn,
        showLoginPrompt = { showLoginDialog = true },
        newVoteState = "DOWNVOTE",
        currentCommentId = details.id,
        currentVoteState = voteState,
        currentScoreState = scoreState,
        setCommentVoteState = { state: String? -> voteState = state },
        setCommentScoreState = { score: Int -> scoreState = score },
        voteFn = voteFn,
        coroutineScope = coroutineScope
    )

    val upvoteSwipe = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_upvote),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .offset(20.dp)
            )
        },
        background = colorUpvote,
        onSwipe = { upVote() },
        weight = 1.0,
    )

    val downvoteSwipe = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.arrow_downvote),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(24.dp)
                    .offset(20.dp)
            )
        },
        background = colorDownvote,
        onSwipe = { downVote() },
        weight = 1.0,
    )

    val replySwipe = SwipeAction(
        icon = {
            Icon(
                painter = painterResource(id = R.drawable.reply),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .offset(20.dp)
            )
        },
        background = Color(0xFF60B626),
        onSwipe = { if (!isLoggedIn) showLoginDialog = true else navigateReply(details.id, details.content) },
        weight = 3.0,
    )

    if (showLoginDialog) {
        LoginDialog(navigateLogin = navigateLogin, onDismiss = { showLoginDialog = false })
    }

    Column {
        SwipeableActionsBox(endActions = listOf(upvoteSwipe, downvoteSwipe, replySwipe),
            swipeThreshold = 40.dp,
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(onPress = {
                    onComponenClick()
                    val holdStartTime = System.currentTimeMillis()
                    val job = coroutineScope.launch {
                        delay(commentHoldDuration)
                        if (System.currentTimeMillis() - holdStartTime >= commentHoldDuration) {
                            collapsedState = !collapsedState
                        }
                    }
                    try {
                        awaitRelease()
                        job.cancel()
                        if (System.currentTimeMillis() - holdStartTime < commentHoldDuration) {
                            actionMenuState = !actionMenuState
                        }
                    } catch (canceled: CancellationException) {
                        job.cancel()
                    }
                })
            }) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.cardBackground)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    CommentHierarchyIndicator(level = level)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 0.dp, top = 8.dp, bottom = 8.dp, end = 5.dp)
                    ) {
                        CommentMeta(
                            authorName = details.author.username,
                            score = scoreState,
                            creationDate = details.createdAt,
                            voteState = voteState.toString(),
                            collapsed = collapsedState
                        )

                        CommentTextContent(content = details.content, collapsedState)
                    }
                }

                AnimatedVisibility(actionMenuState) {
                    CommentActions(
                        voteState = voteState,
                        upVote = { upVote() },
                        downVote = { downVote() },
                        commentId = details.id,
                        commentContent = details.content,
                        replyFn = navigateReply,
                        showLoginPrompt = { showLoginDialog = true },
                        isLoggedIn = isLoggedIn)
                }
            }
        }

        details.children.forEach { child ->
            AnimatedVisibility(visible = !collapsedState) {
                CommentCard(
                    details = child,
                    level = level + 1,
                    voteFn = voteFn,
                    navigateLogin = navigateLogin,
                    isLoggedIn = isLoggedIn,
                    navigateReply = navigateReply,
                    onComponenClick = onComponenClick
                )
            }
        }
    }
}

@Composable
fun CommentHierarchyIndicator(level: Int) {
    Row(modifier = Modifier.fillMaxHeight()) {
        if (level == 1) {
            Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
            )
        }

        repeat(level - 1) { it ->
            if (it % 2 == 0) Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
            )

            VerticalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.neutralGray)

            Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
fun CommentMeta(
    authorName: String,
    score: Int,
    creationDate: String,
    voteState: String? = null,
    collapsed: Boolean
) {
    val scoreColor = when (voteState) {
        "UPVOTE" -> MaterialTheme.colorScheme.appOrange
        "DOWNVOTE" -> MaterialTheme.colorScheme.appBlue
        else -> MaterialTheme.colorScheme.textSecondary
    }

    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = authorName,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.textSecondary,
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
            text = "$score points",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = scoreColor,
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
            text = getTimeAgoFromUtcString(creationDate),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.textSecondary,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
            modifier = Modifier.weight(1f)
        )
        AnimatedVisibility(visible = collapsed) {
            Text(
                text = " C ",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.textSecondary,
                style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(MaterialTheme.colorScheme.neutralGray)
            )
        }
    }
}

@Composable
fun CommentTextContent(content: String, collapsed: Boolean) {
    Text(
        text = content,
        color = MaterialTheme.colorScheme.textPrimary,
        fontSize = 12.sp,
        maxLines = if (collapsed) 3 else 100,
        overflow = TextOverflow.Ellipsis,
        style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
    )
}

suspend fun safeVote(
    fromVoteState: String?,
    toVoteState: String,
    currentScore: Int,
    setCommentVoteState: (String?) -> Unit,
    setCommentScoreState: (Int) -> Unit,
    voteFn: suspend (String) -> Result<Unit, DataError.NetworkError>
) {
    val result = voteFn(toVoteState)

    if (result is Result.Error) {
        when (fromVoteState) {
            "UPVOTE" -> {
                setCommentVoteState("UPVOTE")
                when (toVoteState) {
                    "UPVOTE" -> setCommentScoreState(currentScore + 1)
                    "DOWNVOTE" -> setCommentScoreState(currentScore + 2)
                }
            }

            "DOWNVOTE" -> {
                setCommentVoteState("DOWNVOTE")
                when (toVoteState) {
                    "UPVOTE" -> setCommentScoreState(currentScore - 2)
                    "DOWNVOTE" -> setCommentScoreState(currentScore - 1)
                }
            }

            null -> {
                setCommentVoteState(null)
                when (toVoteState) {
                    "UPVOTE" -> setCommentScoreState(currentScore - 1)
                    "DOWNVOTE" -> setCommentScoreState(currentScore + 1)
                }
            }
        }
    }
}

fun handleVote(
    isLoggedIn: Boolean,
    showLoginPrompt: () -> Unit,
    newVoteState: String,
    currentCommentId: String,
    currentVoteState: String?,
    currentScoreState: Int,
    setCommentVoteState: (String?) -> Unit,
    setCommentScoreState: (Int) -> Unit,
    voteFn: suspend (String, String) -> Result<Unit, DataError.NetworkError>,
    coroutineScope: CoroutineScope
) {
    var optimisticScore = currentScoreState

    if (!isLoggedIn) {
        showLoginPrompt()
        return
    }

    coroutineScope.launch {
        if (newVoteState == "UPVOTE") {
            when (currentVoteState) {
                "UPVOTE" -> {
                    optimisticScore = currentScoreState - 1
                    setCommentVoteState(null)
                    setCommentScoreState(optimisticScore)
                }

                "DOWNVOTE" -> {
                    optimisticScore = currentScoreState + 2
                    setCommentVoteState("UPVOTE")
                    setCommentScoreState(optimisticScore)
                }

                null -> {
                    optimisticScore = currentScoreState + 1
                    setCommentVoteState("UPVOTE")
                    setCommentScoreState(optimisticScore)
                }
            }

        }

        if (newVoteState == "DOWNVOTE") {
            when (currentVoteState) {
                "UPVOTE" -> {
                    optimisticScore = currentScoreState - 2
                    setCommentVoteState("DOWNVOTE")
                    setCommentScoreState(optimisticScore)
                }

                "DOWNVOTE" -> {
                    optimisticScore = currentScoreState + 1
                    setCommentVoteState(null)
                    setCommentScoreState(optimisticScore)
                }

                null -> {
                    optimisticScore = currentScoreState - 1
                    setCommentVoteState("DOWNVOTE")
                    setCommentScoreState(optimisticScore)
                }
            }
        }

        safeVote(
            fromVoteState = currentVoteState,
            toVoteState = newVoteState,
            currentScore = optimisticScore,
            setCommentVoteState = setCommentVoteState,
            setCommentScoreState = setCommentScoreState,
            voteFn = { state: String -> voteFn(currentCommentId, state) }
        )
    }
}

@Composable
fun CommentActions(
    voteState: String?,
    upVote: () -> Unit,
    downVote: () -> Unit,
    commentId: String,
    commentContent: String,
    replyFn: (String, String) -> Unit,
    isLoggedIn: Boolean,
    showLoginPrompt: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_upvote),
            contentDescription = null,
            tint = if (voteState == "UPVOTE") MaterialTheme.colorScheme.appOrange else MaterialTheme.colorScheme.textSecondary,
            modifier = Modifier
                .size(26.dp)
                .offset(20.dp)
                .clickable { upVote() }
        )

        Icon(
            painter = painterResource(id = R.drawable.arrow_downvote),
            contentDescription = null,
            tint = if (voteState == "DOWNVOTE") MaterialTheme.colorScheme.appBlue else MaterialTheme.colorScheme.textSecondary,
            modifier = Modifier
                .size(26.dp)
                .offset(20.dp)
                .clickable { downVote() }
        )

        Icon(
            painter = painterResource(id = R.drawable.reply),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.textSecondary,
            modifier = Modifier
                .size(20.dp)
                .offset(20.dp)
                .clickable {
                    if (!isLoggedIn) showLoginPrompt() else replyFn(commentId, commentContent)
                }
        )
    }
}

@Preview
@Composable
fun CommentCardPreview() {
    val details = CommentResponseDTOItem(
        id = "4767f815-4c05-4b3b-8bb9-690805de8472",
        content = "Looks good!",
        authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
        parentId = null,
        postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
        deleted = false,
        updatedAt = "2024-05-22T05:02:13.698Z",
        createdAt = "2024-05-22T05:02:13.698Z",
        score = 0,
        children = listOf(
            CommentResponseDTOItem(
                id = "4767f815-4c05-4b3b-8bb9-690805de8472",
                content = "Looks good!",
                authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
                parentId = null,
                postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
                deleted = false,
                updatedAt = "2024-05-22T05:02:13.698Z",
                createdAt = "2024-05-22T05:02:13.698Z",
                score = 0,
                children = listOf(
                    CommentResponseDTOItem(
                        id = "4767f815-4c05-4b3b-8bb9-690805de8472",
                        content = "Looks good!",
                        authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
                        parentId = null,
                        postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
                        deleted = false,
                        updatedAt = "2024-05-22T05:02:13.698Z",
                        createdAt = "2024-05-22T05:02:13.698Z",
                        score = 0,
                        children = emptyList(),
                        author = Author(
                            avatarUrl = "https://placehold.co/400.png", username = "tranloc"
                        ),
                        voteState = null
                    )
                ),
                author = Author(
                    avatarUrl = "https://placehold.co/400.png", username = "tranloc"
                ),
                voteState = null
            ), CommentResponseDTOItem(
                id = "4767f815-4c05-4b3b-8bb9-690805de8472",
                content = "Looks good!",
                authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
                parentId = null,
                postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
                deleted = false,
                updatedAt = "2024-05-22T05:02:13.698Z",
                createdAt = "2024-05-22T05:02:13.698Z",
                score = 0,
                children = emptyList(),
                author = Author(
                    avatarUrl = "https://placehold.co/400.png", username = "tranloc"
                ),
                voteState = null
            ), CommentResponseDTOItem(
                id = "4767f815-4c05-4b3b-8bb9-690805de8472",
                content = "Looks good!",
                authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
                parentId = null,
                postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
                deleted = false,
                updatedAt = "2024-05-22T05:02:13.698Z",
                createdAt = "2024-05-22T05:02:13.698Z",
                score = 0,
                children = emptyList(),
                author = Author(
                    avatarUrl = "https://placehold.co/400.png", username = "tranloc"
                ),
                voteState = null
            )
        ),
        author = Author(
            avatarUrl = "https://placehold.co/400.png", username = "tranloc"
        ),
        voteState = null
    )

    BadditTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            CommentCard(details,
                voteFn = { a: String, b: String -> Result.Error(DataError.NetworkError.INTERNAL_SERVER_ERROR) },
                navigateLogin = { },
                navigateReply = { a: String, b: String -> Unit })
                navigateReply = { a: String?, b: String?, c: String? -> Unit },
                onComponenClick = {})
        }
    }
}
