package com.example.baddit.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.presentation.utils.Profile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(

    viewModel: ProfileViewModel = hiltViewModel()
) {
    val currentUser = viewModel.currentUser
    val loggedIn by viewModel.loggedIn
    val listActions = listOf(
        {},
        {}
    )


    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(100.dp)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        if (loggedIn) {
            viewModel.currentUser.value?.let { currentUser ->
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(currentUser.avatarUrl)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(10.dp)
                        .height(100.dp)
                        .aspectRatio(1f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        } else {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("https://i.imgur.com/mJQpR31.png")
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .padding(10.dp)
                    .height(100.dp)
                    .aspectRatio(1.0f)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
    }
}
