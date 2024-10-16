package com.example.baddit

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.baddit.presentation.components.BottomNavigationbar
import com.example.baddit.presentation.screens.community.CommunityScreen
import com.example.baddit.presentation.screens.createPost.CreatePostScreen
import com.example.baddit.presentation.screens.home.HomeScreen
import com.example.baddit.presentation.screens.home.HomeViewModel
import com.example.baddit.ui.theme.BadditTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            BadditTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            BottomNavigationbar(navController = navController)
                        }) { it ->
                        NavHost(
                            navController = navController,
                            startDestination = Home,
                            modifier = Modifier.padding(it)
                        ) {
                            composable<Home> {
                                val homeViewModel: HomeViewModel = hiltViewModel()
                                EnterAnimation {
                                    HomeScreen(viewModel = homeViewModel)
                                }
                            }
                            composable<CreatePost>() {
                                EnterAnimation {
                                    CreatePostScreen()
                                }
                            }
                            composable<Community> {
                                EnterAnimation {
                                    CommunityScreen()
                                }
                            }


                        }
                    }
                }
            }
        }
    }
}

@Serializable
object Home

@Serializable
object CreatePost

@Serializable
object Community

@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = slideInHorizontally(),
        exit = fadeOut() + slideOutHorizontally()
    ) {
        content()
    }
}