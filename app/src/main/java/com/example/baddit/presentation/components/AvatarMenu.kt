package com.example.baddit.presentation.components


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.presentation.screens.login.LoginViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import com.example.baddit.presentation.screens.home.HomeViewModel
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.utils.Setting
import com.example.baddit.ui.theme.CustomTheme.textPrimary



@Composable
fun AvatarMenu(
    show: MutableState<Boolean>,
    viewModel: LoginViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController,
    switchTheme: suspend (Boolean) -> Unit,
    isDarkTheme: Boolean
) {
    var currentUser = viewModel.currentUser.value;

    val coroutineScope = rememberCoroutineScope();

    if (!show.value) {
        return
    } else {
        Popup(
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)) // Scrim effect
                        .clickable(
                            onClick = {
                                show.value = false
                            },
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ),
                ) {
                    Card(
                        modifier = Modifier
                            .wrapContentHeight()
                            .align(Alignment.TopCenter)
                            .clickable(
                                onClick = {},
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .offset(0.dp, 80.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
                        elevation = CardDefaults.cardElevation(8.dp),
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth(0.9f)
                                .wrapContentHeight()
                                .padding(start = 20.dp, end = 20.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .fillMaxWidth()
                                    .padding(top = 10.dp, bottom = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (currentUser != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(currentUser.avatarUrl)
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(50.dp)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                    )
                                } else {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://i.imgur.com/mJQpR31.png")
                                            .build(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .height(50.dp)
                                            .aspectRatio(1f)
                                            .clip(CircleShape)
                                    )
                                }
                                Column(
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(10.dp, 14.dp, 10.dp, 14.dp)
                                ) {
                                    Text(
                                        text = "u/ ${currentUser?.username ?: "Anonymous"}",
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier,
                                        color = MaterialTheme.colorScheme.textPrimary
                                    )
                                }

                                Spacer(modifier = Modifier.weight(1f))

                                if (isDarkTheme) {
                                    Icon(
                                        modifier = Modifier
                                            .size(27.dp)
                                            .clickable(
                                                onClick = {
                                                    coroutineScope.launch {
                                                        switchTheme(false);
                                                    }
                                                }
                                            ),
                                        painter = painterResource(id = R.drawable.baseline_dark_mode_24),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.textPrimary
                                    )
                                } else {
                                    Icon(
                                        modifier = Modifier
                                            .size(27.dp)
                                            .clickable(onClick = {
                                                coroutineScope.launch {
                                                    switchTheme(true);
                                                }
                                            }),
                                        painter = painterResource(id = R.drawable.baseline_light_mode_24),
                                        contentDescription = "",
                                        tint = MaterialTheme.colorScheme.textPrimary
                                    )
                                }

                            }

                            HorizontalDivider()

                            Column(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .padding(bottom = 14.dp, top = 14.dp)
                            ) {

                                if(currentUser != null){
                                    ProfileItem(
                                        painterResource(id = R.drawable.person),
                                        "Profile",
                                        onClick = {
                                            navController.navigate(
                                                Profile(
                                                    viewModel.currentUser.value!!.username
                                                )
                                            )
                                            show.value = false;
                                        })
                                    ProfileItem(
                                        painterResource(id = R.drawable.baseline_settings_24),
                                        "Setting", onClick = {
                                            navController.navigate(
                                                Setting
                                            )
                                            show.value = false;
                                        }
                                    )
                                    ProfileItem(
                                        painterResource(id = R.drawable.baseline_logout_24),
                                        "Logout", onClick = {
                                            coroutineScope.launch {
                                                viewModel.logout()
                                                navController.navigate(Home)
                                                show.value = false;
                                            }
                                        }
                                    )
                                } else {
                                    ProfileItem(
                                        painterResource(id = R.drawable.baseline_login_24),
                                        "Login",
                                        onClick = {
                                            navController.navigate(Login)
                                            show.value = false;
                                        }
                                    )
                                }

                            }
                        }
                    }

                }
            }
        )
    }
}