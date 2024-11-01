package com.example.baddit

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.baddit.domain.model.posts.PostResponseDTOItem
import com.example.baddit.domain.usecases.LocalThemeUseCases
import com.example.baddit.presentation.components.AvatarMenu
import com.example.baddit.presentation.components.BottomNavigationBar
import com.example.baddit.presentation.components.CreatePostActionButton
import com.example.baddit.presentation.components.TopNavigationBar
import com.example.baddit.presentation.screens.community.CommunityScreen
import com.example.baddit.presentation.screens.createPost.CreateMediaPostSCcreen
import com.example.baddit.presentation.screens.createPost.CreatePostBottomSheet
import com.example.baddit.presentation.screens.createPost.CreateTextPostScreen
import com.example.baddit.presentation.screens.home.HomeScreen
import com.example.baddit.presentation.screens.login.LoginScreen
import com.example.baddit.presentation.screens.post.PostScreen
import com.example.baddit.presentation.screens.profile.ProfileScreen
import com.example.baddit.presentation.screens.signup.SignupScreen
import com.example.baddit.presentation.screens.verify.VerifyScreen
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.CreateMediaPost
import com.example.baddit.presentation.utils.CreateTextPost
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Main
import com.example.baddit.presentation.utils.Post
import com.example.baddit.presentation.utils.PostResponseNavType
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.utils.SignUp
import com.example.baddit.presentation.utils.Verify
import com.example.baddit.ui.theme.BadditTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.reflect.typeOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var localThemes: LocalThemeUseCases

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {

            val navController = rememberNavController()
            val barState = rememberSaveable { mutableStateOf(false) }
            val userTopBarState = rememberSaveable { mutableStateOf(false) }

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }

            var showAvatarMenu = remember { mutableStateOf(false) }

            var bool = remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    localThemes.readDarkTheme().collect{
                        bool.value = it
                    }
                }
            }

            val switchTheme = suspend {
                if (bool.value) {
                    bool.value = false;
                    localThemes.saveDarkTheme(b = bool.value)
                } else {
                    bool.value = true;
                    localThemes.saveDarkTheme(b = bool.value)
                }
            }

            BadditTheme(darkTheme = bool.value) {
                AvatarMenu(
                    show = showAvatarMenu,
                    navController = navController,
                    switchTheme = switchTheme,
                    isDarkTheme = bool.value
                )
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            BottomNavigationBar(
                                navController = navController, barState = barState
                            )
                        },
                        topBar = {
                            TopNavigationBar(
                                navController = navController,
                                barState = barState,
                                userTopBarState = userTopBarState,
                                showAvatarMenu = showAvatarMenu
                            )
                        },
                        floatingActionButton = {
                            if (barState.value) {
                                CreatePostActionButton(onClick = { showBottomSheet = true })
                            }
                        }
                    ) { it ->
                        if (showBottomSheet) {
                            CreatePostBottomSheet(
                                onDismissRequest = { showBottomSheet = false },
                                sheetState = sheetState,
                                navController = navController
                            )
                        }
                        NavHost(
                            navController = navController,
                            startDestination = Main,
                            modifier = Modifier.padding(it)
                        ) {
                            navigation<Main>(startDestination = Home) {
                                composable<Home> {
                                    barState.value = true
                                    userTopBarState.value = false

                                    SlideHorizontally {
                                        HomeScreen(
                                            navigateLogin = { navController.navigate(Login) },
                                            navigatePost = { details: PostResponseDTOItem -> navController.navigate(Post(
                                                postDetails = details
                                            )) }
                                        )
                                    }
                                }
                                composable<Community> {
                                    barState.value = true
                                    userTopBarState.value = true

                                    SlideHorizontally {
                                        CommunityScreen(navController)
                                    }
                                }
                                composable<Profile> {
                                    barState.value = true
                                    userTopBarState.value = true

                                    val username = it.arguments?.getString("username");
                                    SlideHorizontally {
                                        ProfileScreen(
                                            username = username!!,
                                            navController = navController,
                                            navigatePost = { details: PostResponseDTOItem -> navController.navigate(Post(
                                                postDetails = details
                                            )) },
                                            navigateLogin = { navController.navigate(Login) }
                                        )
                                    }
                                }
                                composable<CreateTextPost> {
                                    barState.value = false
                                    userTopBarState.value = false

                                    SlideHorizontally {
                                        CreateTextPostScreen(navController = navController)
                                    }
                                }

                                composable<CreateMediaPost> {
                                    barState.value = false
                                    userTopBarState.value = false

                                    SlideHorizontally {
                                        CreateMediaPostSCcreen(navController = navController)
                                    }
                                }
                                composable<Post>(
                                    typeMap = mapOf(
                                        typeOf<PostResponseDTOItem>() to PostResponseNavType
                                    )
                                ) {
                                    barState.value = true
                                    userTopBarState.value = false

                                    SlideVertically {
                                        PostScreen(navigateLogin = { navController.navigate(Login) })
                                    }
                                }
                            }

                            navigation<Auth>(startDestination = SignUp) {
                                composable<SignUp> {
                                    barState.value = false;
                                    userTopBarState.value = false;

                                    SignupScreen(isDarkMode = bool.value,
                                        navigateToLogin = { navController.navigate(Login) },
                                        navigateHome = { navController.navigate(Home) })
                                }
                                composable<Login> {
                                    barState.value = false;
                                    userTopBarState.value = false;

                                    LoginScreen(isDarkMode = bool.value,
                                        navigateToHome = { navController.navigate(Home) },
                                        navigateToSignup = { navController.navigate(SignUp) })
                                }
                                composable<Verify>(
                                    deepLinks = listOf(navDeepLink {
                                        uriPattern = "https://baddit.life/auth?emailToken={token}"
                                    })
                                ) {
                                    barState.value = false;
                                    userTopBarState.value = false;
                                    val token = it.arguments?.getString("token")
                                    VerifyScreen(
                                        navigateLogin = { navController.navigate(Login) },
                                        navigateHome = { navController.navigate(Home) }, token
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SlideHorizontally(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = slideInHorizontally(),
        exit = slideOutHorizontally() + fadeOut(),
    ) {
        content()
    }
}

@Composable
fun SlideVertically(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = slideInVertically(),
        exit = slideOutVertically() + fadeOut(),
    ) {
        content()
    }
}

