package com.example.baddit.presentation.components

import androidx.compose.animation.core.*
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.presentation.screens.home.HomeViewModel
import com.example.baddit.presentation.screens.login.LoginViewModel
import com.example.baddit.presentation.screens.profile.ProfileViewModel
import com.example.baddit.presentation.utils.LeftSideBar
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Search
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopNavigationBar(
    navController: NavController,
    barState: Boolean,
    userTopBarState: Boolean,
    showAvatarMenu: MutableState<Boolean>,
    onDrawerClicked: () -> Unit,
    profileViewModal: ProfileViewModel = hiltViewModel(),
    viewModel: LoginViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    hasNotification: Boolean = false
) {
    val navItems = listOf(
        TopNavigationItem(icon = R.drawable.baseline_menu_24, value = LeftSideBar),
        TopNavigationItem(icon = R.drawable.baseline_sort_24, value = Search),
    )

    var expanded by remember { mutableStateOf(false) }

    val loggedIn by viewModel.loggedIn

    var showLoginDialog by rememberSaveable { mutableStateOf(false) }

    if (showLoginDialog) {
        com.example.baddit.presentation.screens.home.LoginDialog(
            navigateLogin = { navController.navigate(Login) },
            onDismiss = { showLoginDialog = false })
    }

    AnimatedVisibility(
        visible = barState && !userTopBarState,
        exit = slideOutVertically(),
        enter = slideInVertically()
    ) {
        TopAppBar(
            title = { },
            modifier = Modifier.shadow(elevation = 1.dp).background(MaterialTheme.colorScheme.cardBackground).padding(top = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()),
            colors = TopAppBarColors(
                containerColor = MaterialTheme.colorScheme.cardBackground,
                navigationIconContentColor = MaterialTheme.colorScheme.textPrimary,
                actionIconContentColor = MaterialTheme.colorScheme.textPrimary,
                scrolledContainerColor = MaterialTheme.colorScheme.textPrimary,
                titleContentColor = MaterialTheme.colorScheme.textPrimary
            ),
            navigationIcon = {
                IconButton(onClick = onDrawerClicked) {
                    Icon(
                        painter = painterResource(id = navItems[0].icon),
                        contentDescription = null
                    )
                }
            },
            actions = {
                IconButton(onClick = {
                    expanded = true
                }) {
                    Icon(
                        painter = painterResource(id = navItems[1].icon),
                        contentDescription = null
                    )
                }
                if (loggedIn) {
                    profileViewModal.me.value?.let { currentUser ->
                        IconButton(onClick = {
                            showAvatarMenu.value = true;
                        }) {
                            Box {
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

                                if (hasNotification) {
                                    Box(
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .background(Color.Red)
                                            .size(10.dp)
                                            .align(Alignment.TopEnd)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("https://i.imgur.com/mJQpR31.png")
                            .build(),
                        contentDescription = null,
                        modifier = Modifier
                            .height(33.dp)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .clickable(
                                onClick = {
                                    showAvatarMenu.value = true;
                                }
                            ),
                        contentScale = ContentScale.Crop
                    )
                }

            },
        )
        Box(modifier = Modifier.fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd).padding(end = 50.dp))
        {
            DropdownMenu(
                modifier = Modifier.background(MaterialTheme.colorScheme.background),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Sort By Score") },
                    onClick = { homeViewModel.refreshPosts("true") }
                )
                DropdownMenuItem(
                    text = { Text("Sort By Date") },
                    onClick = { homeViewModel.refreshPosts() }
                )
            }
        }
    }
}

data class TopNavigationItem(
    @DrawableRes val icon: Int,
    val value: Any
)