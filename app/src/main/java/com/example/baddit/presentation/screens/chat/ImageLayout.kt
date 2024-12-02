package com.example.baddit.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage

@Composable
fun ImageGallery(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    when (imageUrls.size) {
        1 -> SingleImageLayout(imageUrls[0], onImageClick)
        2 -> TwoImagesLayout(imageUrls, onImageClick)
        3 -> ThreeImagesLayout(imageUrls, onImageClick)
        4 -> FourImagesLayout(imageUrls, onImageClick)
        else -> MoreThanFourImagesLayout(imageUrls, onImageClick)
    }
}

@Composable
fun SingleImageLayout(
    imageUrl: String,
    onImageClick: (String) -> Unit
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "Message image",
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onImageClick(imageUrl) },
        contentScale = ContentScale.Crop
    )
}

@Composable
fun TwoImagesLayout(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        imageUrls.forEach { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Message image",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(imageUrl) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun ThreeImagesLayout(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = imageUrls[0],
            contentDescription = "First message image",
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 250.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable { onImageClick(imageUrls[0]) },
            contentScale = ContentScale.Crop
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            imageUrls.slice(1..2).forEach { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Message image",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(imageUrl) },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun FourImagesLayout(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = imageUrls[0],
                contentDescription = "First message image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(imageUrls[0]) },
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = imageUrls[1],
                contentDescription = "Second message image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(imageUrls[1]) },
                contentScale = ContentScale.Crop
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            AsyncImage(
                model = imageUrls[2],
                contentDescription = "Third message image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(imageUrls[2]) },
                contentScale = ContentScale.Crop
            )
            AsyncImage(
                model = imageUrls[3],
                contentDescription = "Fourth message image",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onImageClick(imageUrls[3]) },
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun MoreThanFourImagesLayout(
    imageUrls: List<String>,
    onImageClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            imageUrls.take(3).forEach { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Message image",
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick(imageUrl) },
                    contentScale = ContentScale.Crop
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f)
                .clip(RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = imageUrls[3],
                contentDescription = "Fourth message image",
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onImageClick(imageUrls[3]) },
                contentScale = ContentScale.Crop
            )

            if (imageUrls.size > 4) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "+${imageUrls.size - 4}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun ImageViewerDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .clickable { onDismiss() }
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Full screen image",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(Color.Gray.copy(alpha = 0.5f), shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close image viewer",
                    tint = Color.White
                )
            }
        }
    }
}
