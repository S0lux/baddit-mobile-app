package com.example.baddit

import android.content.Intent
import android.content.pm.verify.domain.DomainVerificationManager
import android.content.pm.verify.domain.DomainVerificationUserState
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.baddit.presentation.components.BottomNavigationBar
import com.example.baddit.presentation.components.TopNavigationBar
import com.example.baddit.presentation.screens.community.CommunityScreen
import com.example.baddit.presentation.screens.createPost.CreatePostScreen
import com.example.baddit.presentation.screens.home.HomeScreen
import com.example.baddit.presentation.screens.login.LoginScreen
import com.example.baddit.presentation.screens.profile.ProfileScreen
import com.example.baddit.presentation.screens.signup.SignupScreen
import com.example.baddit.presentation.screens.verify.VerifyScreen
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.CreatePost
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Main
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.utils.SignUp
import com.example.baddit.presentation.utils.Verify
import com.example.baddit.ui.theme.BadditTheme
import dagger.hilt.android.AndroidEntryPoint
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDeepLink

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            val barState = rememberSaveable { mutableStateOf(true) }
            val userTopBarState = rememberSaveable { mutableStateOf(false) }
            val navBackStackEntry by navController.currentBackStackEntryAsState()

            when (navBackStackEntry?.destination?.route) {
                "com.example.baddit.presentation.utils.Home" -> {
                    // Show BottomBar and TopBar
                    barState.value = true
                    userTopBarState.value = false
                }

                "com.example.baddit.presentation.utils.CreatePost" -> {
                    // Show BottomBar and TopBar
                    barState.value = true
                    userTopBarState.value = false
                }

                "com.example.baddit.presentation.utils.Community" -> {
                    // Show BottomBar and TopBar
                    barState.value = true
                    userTopBarState.value = false
                }

                "com.example.baddit.presentation.utils.SignUp" -> {
                    // Hide BottomBar and TopBar
                    barState.value = false
                    userTopBarState.value = false
                }

                "com.example.baddit.presentation.utils.Login" -> {
                    barState.value = false
                    userTopBarState.value = false
                }

                "com.example.baddit.presentation.utils.Profile" -> {
                    barState.value = true
                    userTopBarState.value = true
                }

                "com.example.baddit.presentation.utils.Verify" -> {
                    barState.value = false
                }
            }


            BadditTheme {
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
                            TopNavigationBar(navController = navController, barState = barState ,userTopBarState = userTopBarState)
                        }
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Main,
                            modifier = Modifier.padding(it)
                        ) {
                            navigation<Main>(startDestination = Home) {
                                composable<Home> {
                                    SlideHorizontally {
                                        HomeScreen { navController.navigate(Login) }
                                    }
                                }
                                composable<CreatePost> {
                                    SlideHorizontally {
                                        CreatePostScreen()
                                    }
                                }
                                composable<Community> {
                                    SlideHorizontally {
                                        CommunityScreen()
                                    }
                                }
                                composable<Profile> {
                                    SlideHorizontally {
                                        ProfileScreen()
                                    }
                                }
                            }
                            navigation<Auth>(startDestination = SignUp) {
                                composable<SignUp> {
                                    SignupScreen(navigateToLogin = { navController.navigate(Login) })
                                }
                            }

                            composable<Login> {
                                LoginScreen(
                                    navigateToHome = { navController.navigate(Home) },
                                    navigateToSignup = { navController.navigate(SignUp) })
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