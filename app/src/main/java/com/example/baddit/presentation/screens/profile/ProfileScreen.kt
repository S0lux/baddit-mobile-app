package com.example.baddit.presentation.screens.profile

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.auth.GetMeResponseDTO
import com.example.baddit.domain.model.auth.GetOtherResponseDTO
import com.example.baddit.domain.model.profile.UserProfile
import com.example.baddit.presentation.styles.gradientBackGroundBrush
import com.example.baddit.presentation.utils.Home

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val loggedIn by viewModel.loggedIn

    LaunchedEffect(username) {
        viewModel.fetchUserProfile(username)
    }



    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        TopAppBar(
            title = {
                val titleText = if (viewModel.loggedIn.value) {
                    viewModel.user.value?.username?.let {
                        "u/$it"
                    } ?: "u/UnknownUser"
                } else {
                    "u/UnknownUser"
                }
                Text(
                    text = titleText,
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    )
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(Home) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            actions = {},
        )
        ProfileHeader(loggedIn = loggedIn, currentUser = viewModel.user.value, isGetMe = true)
        ProfileContent()
    }
}


@Composable
fun ProfileHeader(
    loggedIn: Boolean,
    currentUser: GetOtherResponseDTO?,
    isGetMe: Boolean
) {
    val gradientList = listOf(
        Color(0xFF2193b0),
        Color(0xFF6dd5ed)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(100.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(100.dp)
                .background(
                    gradientBackGroundBrush(
                        isVerticalGradient = true,
                        colors = gradientList
                    )
                ),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (loggedIn) {
                currentUser?.let { currentUser ->
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
                        .height(130.dp)
                        .aspectRatio(1.0f)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color.Transparent)

            ) {

            }
        }
    }
}

@Composable
fun ProfileContent() {
    //tabs
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(125.dp)
        ) {
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    text = "Posts",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                )
            }
            TextButton(onClick = { /*TODO*/ }) {
                Text(
                    text = "Comments",
                    style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                )
            }

        }
    }
    //this is for scrollable content and also refresh
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.Yellow)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Black)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Red)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .background(Color.Green)
        )
    }
}