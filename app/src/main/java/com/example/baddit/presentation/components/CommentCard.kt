package com.example.baddit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.baddit.domain.model.comment.Author
import com.example.baddit.domain.model.comment.CommentResponseDTOItem
import com.example.baddit.ui.theme.BadditTheme
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.appOrange
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import getTimeAgoFromUtcString
import me.saket.swipe.SwipeableActionsBox

@Composable
fun CommentCard(details: CommentResponseDTOItem, collapsed: Boolean = true, level: Int = 1) {
    var voteState by remember { mutableStateOf(details.voteState) }

    SwipeableActionsBox() {
        Column (modifier = Modifier.background(MaterialTheme.colorScheme.cardBackground)) {
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
                        score = details.score,
                        creationDate = details.createdAt,
                        voteState = voteState.toString()
                    )

                    CommentTextContent(content = details.content, collapsed = collapsed)
                }
            }

            details.children.forEach { child ->
                CommentCard(details = child, collapsed = collapsed, level = level + 1)
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
            if (it % 2 == 0)
                Spacer(
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
fun CommentMeta(authorName: String, score: Int, creationDate: String, voteState: String? = null) {
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
            color = scoreColor,
            style = TextStyle(platformStyle = PlatformTextStyle(includeFontPadding = false))
        )
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
        children = listOf(CommentResponseDTOItem(
            id = "4767f815-4c05-4b3b-8bb9-690805de8472",
            content = "Looks good!",
            authorId = "50e46347-8fb1-49c1-9323-3c5589e64e1f",
            parentId = null,
            postId = "26549544-6c90-41c6-9085-1216ad04c7fd",
            deleted = false,
            updatedAt = "2024-05-22T05:02:13.698Z",
            createdAt = "2024-05-22T05:02:13.698Z",
            score = 0,
            children = listOf(CommentResponseDTOItem(
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
                    avatarUrl = "https://placehold.co/400.png",
                    username = "tranloc"
                ),
                voteState = null
            )),
            author = Author(
                avatarUrl = "https://placehold.co/400.png",
                username = "tranloc"
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
                avatarUrl = "https://placehold.co/400.png",
                username = "tranloc"
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
                avatarUrl = "https://placehold.co/400.png",
                username = "tranloc"
            ),
            voteState = null
        )),
        author = Author(
            avatarUrl = "https://placehold.co/400.png",
            username = "tranloc"
        ),
        voteState = null
    )

    BadditTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            CommentCard(details)
        }
    }
}
