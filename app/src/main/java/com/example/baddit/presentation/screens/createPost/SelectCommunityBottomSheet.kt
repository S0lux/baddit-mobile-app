package com.example.baddit.presentation.screens.createPost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.CommunityDTO
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary


@Composable
fun CommunitySelector(onClick: () -> Unit, viewmodel: CreatePostViewodel) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        TextField(
            value = viewmodel.selectedCommunity.value,
            onValueChange = {},
            colors = textFieldColors(),
            isError = viewmodel.selectedCommunity.error.isNotEmpty(),
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth(),
            placeholder = { Text(text = "subreddit") },
            prefix = { Text(text = "r/ ") },
            supportingText = {Text(text = viewmodel.selectedCommunity.error)},
            leadingIcon = {
                if (!viewmodel.selectedCommunity.value.isEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(viewmodel.selectedCommunityLogo)
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .height(25.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )

                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://i.imgur.com/mJQpR31.png")
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .height(25.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            trailingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_downvote),
                    contentDescription = null
                )
            }
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp)
                .clickable(onClick = onClick, enabled = true)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCommunityBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    viewmodel: CreatePostViewodel = hiltViewModel(),
) {
    val communities = viewmodel.communities
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.align(Alignment.CenterStart),
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = null,
                    )
                }
                Text(
                    text = "Post to",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            items(items = communities) { it ->
                Community(
                    community = it,
                    onSelected = {
                        viewmodel.selectedCommunity = viewmodel.selectedCommunity.copy(value = it.name, error = "")
                        viewmodel.selectedCommunityLogo = it.logoUrl
                        onDismissRequest()
                    })
            }

        }

    }
}

@Composable
private fun Community(community: Community, onSelected: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .padding(5.dp)
            .clickable { onSelected() }
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(community.logoUrl ?: "https://i.imgur.com/mJQpR31.png")
                .build(),
            contentDescription = null,
            modifier = Modifier
                .height(33.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = "r/ ${community.name ?: "N/A"}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${community.memberCount ?: 0} members",
                style = MaterialTheme.typography.titleSmall.copy(MaterialTheme.colorScheme.textSecondary),
            )
        }

    }
}
