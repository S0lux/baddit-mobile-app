package com.example.baddit.presentation.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.SlideVertically
import com.example.baddit.presentation.screens.login.LoginViewModel
import com.example.baddit.presentation.screens.profile.ProfileViewModel
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.LeftSideBar
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.utils.Search
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    navController: NavHostController,
    barState: MutableState<Boolean>,
    userTopBarState: MutableState<Boolean>,
<<<<<<< HEAD
<<<<<<< HEAD
    viewModel: ProfileViewModel = hiltViewModel()
=======
    viewModel: LoginViewModel = hiltViewModel(),
    showAvatarMenu: MutableState<Boolean>
>>>>>>> origin/master
=======
    viewModel: LoginViewModel = hiltViewModel(),
    showAvatarMenu: MutableState<Boolean>
>>>>>>> origin/master
) {
    val navItems = listOf(
        TopNavigationItem(icon = R.drawable.baseline_menu_24, value = LeftSideBar),
        TopNavigationItem(icon = R.drawable.baseline_search_24, value = Search),
    )

    val loggedIn by viewModel.loggedIn

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }

    if (showLoginDialog) {
        com.example.baddit.presentation.screens.home.LoginDialog(
            navigateLogin = { navController.navigate(Login) },
            onDismiss = { showLoginDialog = false })
    }
    AnimatedVisibility(
        visible = barState.value && !userTopBarState.value,
        exit = slideOutVertically(),
        enter = slideInVertically()
    ) {
        SlideVertically {
            TopAppBar(

<<<<<<< HEAD
                title = { },
                navigationIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            painter = painterResource(id = navItems[0].icon),
                            contentDescription = null
=======
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { /*TODO*/ }) {
                            Icon(
                                painter = painterResource(id = navItems[0].icon),
                                contentDescription = null
                            )
                        }
                    },
                    actions = @androidx.compose.runtime.Composable {
                        IconButton(onClick = { }) {
                            Icon(
                                painter = painterResource(id = navItems[1].icon),
                                contentDescription = null
                            )
                        }
                        if (loggedIn) {
                            viewModel.currentUser.value?.let { currentUser ->
                                IconButton(onClick = {
//                                    navController.navigate(
//                                        Profile(
//                                            viewModel.currentUser.value!!.username
//                                        )
//                                    )
                                    showAvatarMenu.value = true;
                                }) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(currentUser.avatarUrl)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(33.dp)
                                            .aspectRatio(1f)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }
                        } else {
                            IconButton(onClick = { showLoginDialog = true }) {
                                TopAppBar(
                                    colors = TopAppBarColors(
                                        containerColor = MaterialTheme.colorScheme.scaffoldBackground,
                                        navigationIconContentColor = MaterialTheme.colorScheme.textPrimary,
                                        actionIconContentColor = MaterialTheme.colorScheme.textPrimary,
                                        scrolledContainerColor = MaterialTheme.colorScheme.textPrimary,
                                        titleContentColor = MaterialTheme.colorScheme.textPrimary
                                    ),
                                    title = { },
                                    navigationIcon = {
                                        IconButton(onClick = { /*TODO*/ }) {
                                            Icon(
                                                painter = painterResource(id = navItems[0].icon),
                                                contentDescription = null
                                            )
                                        }
                                    },
                                    actions = {
                                        IconButton(onClick = { }) {
                                            Icon(
                                                painter = painterResource(id = navItems[1].icon),
                                                contentDescription = null
                                            )
                                        }
                                        if (loggedIn) {
                                            viewModel.currentUser.value?.let { currentUser ->
                                                IconButton(onClick = {
                                                    navController.navigate(
                                                        Profile
                                                    )
                                                }) {
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(LocalContext.current)
                                                            .data("https://i.imgur.com/mJQpR31.png")
                                                            .build(),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .height(33.dp)
                                                            .aspectRatio(1f)
                                                            .clip(CircleShape),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                }
                                            }
                                        }
                                    })
                            }

                        }
                    })
            }
        }
        else{
            SlideVertically {
                TopAppBar(
                    title = {
                        val titleText = if (viewModel.loggedIn.value) {
                            ("u/" + viewModel.currentUser.value?.username)
                                ?: "u/UnknownUser"
                        } else {
                            "u/UnknownUser"
                        }
                        Text(
                            text = titleText,
                            style = TextStyle(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 20.sp
                            )
>>>>>>> origin/master
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painter = painterResource(id = navItems[1].icon),
                            contentDescription = null
                        )
                    }
                    if (loggedIn) {
                        viewModel.me.value?.let {
                            IconButton(onClick = {
                                navController.navigate(
                                    Profile(
                                        viewModel.me.value!!.username
                                    )
                                )
                            }) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(viewModel.me.value!!.avatarUrl)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .height(33.dp)
                                        .aspectRatio(1f)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    } else {
                        IconButton(onClick = { showLoginDialog = true }) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data("https://i.imgur.com/mJQpR31.png")
                                    .build(),
                                contentDescription = null,
                                modifier = Modifier
                                    .height(33.dp)
                                    .aspectRatio(1f)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )

                        }

                    }
                })
        }
    }
}

data class TopNavigationItem(
    @DrawableRes val icon: Int,
    val value: Any
)
