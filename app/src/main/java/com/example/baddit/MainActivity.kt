package com.example.baddit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.example.baddit.domain.repository.AuthRepository
import com.example.baddit.domain.usecases.LocalThemeUseCases
import com.example.baddit.presentation.components.AvatarMenu
import com.example.baddit.presentation.components.BadditActionButton
import com.example.baddit.presentation.components.BottomNavigationBar
import com.example.baddit.presentation.components.BottomNavigationItem
import com.example.baddit.presentation.components.LoginDialog
import com.example.baddit.presentation.components.SideDrawerContent.SideDrawerContent
import com.example.baddit.presentation.components.TopNavigationBar
import com.example.baddit.presentation.screens.comment.CommentScreen
import com.example.baddit.presentation.screens.community.AddModeratorScreen
import com.example.baddit.presentation.screens.community.CommunityDetailScreen
import com.example.baddit.presentation.screens.community.CommunityScreen
import com.example.baddit.presentation.screens.community.EditCommunityScreen
import com.example.baddit.presentation.screens.createPost.CreateMediaPostSCcreen
import com.example.baddit.presentation.screens.createPost.CreatePostBottomSheet
import com.example.baddit.presentation.screens.createPost.CreateTextPostScreen
import com.example.baddit.presentation.screens.editing.EditingScreen
import com.example.baddit.presentation.screens.home.HomeScreen
import com.example.baddit.presentation.screens.login.LoginScreen
import com.example.baddit.presentation.screens.post.PostScreen
import com.example.baddit.presentation.screens.profile.ProfileScreen
import com.example.baddit.presentation.screens.setting.SettingScreen
import com.example.baddit.presentation.screens.signup.SignupScreen
import com.example.baddit.presentation.screens.verify.VerifyScreen
import com.example.baddit.presentation.utils.AddModerator
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.presentation.utils.Comment
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.CommunityDetail
import com.example.baddit.presentation.utils.CreateMediaPost
import com.example.baddit.presentation.utils.CreateTextPost
import com.example.baddit.presentation.utils.EditCommunity
import com.example.baddit.presentation.utils.Editing
import com.example.baddit.presentation.utils.FAButtons
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Login
import com.example.baddit.presentation.utils.Main
import com.example.baddit.presentation.utils.Post
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.utils.Setting
import com.example.baddit.presentation.utils.SignUp
import com.example.baddit.presentation.utils.Verify
import com.example.baddit.ui.theme.BadditTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var localThemes: LocalThemeUseCases

    @Inject
    lateinit var authRepository: AuthRepository

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {

            val navController = rememberNavController()
            val barState = rememberSaveable { mutableStateOf(false) }
            val userTopBarState = rememberSaveable { mutableStateOf(false) }
            var showLoginDialog by remember { mutableStateOf(false) }

            val sheetState = rememberModalBottomSheetState()
            var showBottomSheet by remember { mutableStateOf(false) }
            val showAvatarMenu = remember { mutableStateOf(false) }
            var selectedBottomNavigation by rememberSaveable { mutableIntStateOf(0) }
            var activeFAB: FAButtons? by remember { mutableStateOf(FAButtons.POST_CREATE) }
            var activePostId: String? by remember { mutableStateOf(null) }
            var activeCommentId: String? by remember { mutableStateOf(null) }
            var activeCommentComment: String? by remember { mutableStateOf(null) }

            val bool = remember { mutableStateOf<Boolean?>(false) }

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()
            val interactionSource = remember { MutableInteractionSource() }

            LaunchedEffect(Unit) {
                lifecycleScope.launch {
                    localThemes.readDarkTheme().collect {
                        when (it) {
                            "Dark" -> bool.value = true
                            "Light" -> bool.value = false
                            else -> bool.value;
                        }
                    }
                }
            }

            val switchTheme: suspend (String) -> Unit = { darkTheme ->
                localThemes.saveDarkTheme(b = darkTheme)
                localThemes.readDarkTheme().collect {
                    when (it) {
                        "Dark" -> bool.value = true
                        "Light" -> bool.value = false
                        else -> bool.value = null
                    }
                }
            }

            BadditTheme(darkTheme = bool.value ?: isSystemInDarkTheme()) {
                if (showLoginDialog) {
                    LoginDialog(
                        navigateLogin = { navController.navigate(Login); showLoginDialog = false },
                        onDismiss = { showLoginDialog = false })
                }

                BadditTheme(darkTheme = bool.value ?: isSystemInDarkTheme()) {
                    AvatarMenu(
                        show = showAvatarMenu,
                        navController = navController,
                        switchTheme = switchTheme,
                        isDarkTheme = bool.value ?: isSystemInDarkTheme()
                    )
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        DismissibleNavigationDrawer(drawerContent = {
                            SideDrawerContent(
                                onExploreClick = {
                                    scope.launch {
                                        navController.navigate(Community)
                                        drawerState.close()
                                    }
                                },
                                navController = navController,
                                drawerState =  drawerState)
                        }, drawerState = drawerState)
                        {
                            Scaffold(
                                modifier = Modifier.clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    if (drawerState.isOpen) scope.launch { drawerState.close() }
                                },
                                bottomBar = {
                                    BottomNavigationBar(
                                        navController = navController,
                                        barState = barState,
                                        navItems = navItems,
                                        selectedItem = selectedBottomNavigation,
                                        drawerState = drawerState
                                    )
                                },
                                topBar = {
                                    TopNavigationBar(
                                        navController = navController,
                                        barState = barState,
                                        userTopBarState = userTopBarState,
                                        showAvatarMenu = showAvatarMenu,
                                        onDrawerClicked = {
                                            scope.launch {
                                                if (drawerState.isOpen) drawerState.close()
                                                else drawerState.open()
                                            }
                                        }
                                    )
                                },
                                floatingActionButton = {
                                    if (barState.value) {
                                        when (activeFAB) {
                                            FAButtons.POST_CREATE -> BadditActionButton(onClick = {
                                                showBottomSheet = true
                                            })

                                            FAButtons.POST_REPLY -> BadditActionButton(onClick = {
                                                if (authRepository.isLoggedIn.value.not()) {
                                                    showLoginDialog = true
                                                } else navController.navigate(
                                                    Comment(
                                                        postId = activePostId,
                                                        commentId = null,
                                                        commentContent = activeCommentComment,
                                                        darkMode = bool.value ?: false,
                                                    )
                                                )
                                            }, icon = R.drawable.reply)

                                            else -> Unit
                                        }
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
                                            selectedBottomNavigation = 0
                                            barState.value = true
                                            userTopBarState.value = false

                                            activeFAB = FAButtons.POST_CREATE
                                            HomeScreen(
                                                navController = navController,
                                                darkMode = bool.value ?: isSystemInDarkTheme(),
                                                onComponentClick = {
                                                    scope.launch {
                                                        if (drawerState.isOpen) drawerState.close()
                                                    }
                                                },
                                                drawerState = drawerState
                                            )
                                        }
                                        composable<Community> {
                                            selectedBottomNavigation = 1
                                            barState.value = true
                                            userTopBarState.value = true

                                            activeFAB = FAButtons.POST_CREATE
                                            CommunityScreen(navController)
                                        }
                                        composable<Profile> {
                                            selectedBottomNavigation = -1
                                            barState.value = true
                                            userTopBarState.value = true

                                            val username = it.arguments?.getString("username");

                                            activeFAB = null
                                            ProfileScreen(
                                                username = username!!,
                                                navController = navController,
                                                navigatePost = { postId: String ->
                                                    navController.navigate(
                                                        Post(postId = postId)
                                                    )
                                                },
                                                navigateLogin = { navController.navigate(Login) },
                                                navigateReply = { id: String, content: String ->
                                                    activeCommentId = id
                                                    activeCommentComment = content
                                                    navController.navigate(
                                                        Comment(
                                                            postId = null,
                                                            commentId = activeCommentId,
                                                            commentContent = activeCommentComment,
                                                            darkMode = bool.value ?: false,
                                                        )
                                                    )
                                                },
                                                darkMode = bool.value ?: false
                                            )
                                        }
                                        composable<CreateTextPost> {
                                            selectedBottomNavigation = -1

                                            activeFAB = null
                                            barState.value = false
                                            userTopBarState.value = false

                                            CreateTextPostScreen(
                                                navController = navController,
                                                isDarkTheme = bool.value ?: isSystemInDarkTheme()
                                            )
                                        }

                                        composable<CreateMediaPost> {
                                            selectedBottomNavigation = -1

                                            activeFAB = null
                                            barState.value = false
                                            userTopBarState.value = false

                                            CreateMediaPostSCcreen(
                                                navController = navController,
                                                isDarkTheme = bool.value ?: isSystemInDarkTheme()
                                            )
                                        }
                                        composable<Post> {
                                            selectedBottomNavigation = -1

                                            activeFAB = FAButtons.POST_REPLY
                                            barState.value = true
                                            userTopBarState.value = false

                                            activePostId = it.toRoute<Post>().postId
                                            PostScreen(
                                                navController = navController,
                                                navReply = { id: String, content: String ->
                                                    activeCommentId = id
                                                    activeCommentComment = content
                                                    navController.navigate(
                                                        Comment(
                                                            postId = activePostId,
                                                            commentId = activeCommentId,
                                                            commentContent = activeCommentComment,
                                                            darkMode = bool.value ?: false,
                                                        )
                                                    )
                                                },
                                                darkMode = bool.value ?: false,
                                                onComponentClick = {
                                                    if (drawerState.isOpen) scope.launch {
                                                        drawerState.close()
                                                    }
                                                }
                                            )
                                        }
                                        composable<Setting> {
                                            selectedBottomNavigation = -1

                                            activeFAB = null
                                            barState.value = false
                                            userTopBarState.value = false

                                            SettingScreen(
                                                navController = navController,
                                                switchTheme = switchTheme,
                                                darkTheme = bool.value
                                            );
                                        }
                                        composable<Comment> {
                                            selectedBottomNavigation = -1

                                            activeFAB = null
                                            barState.value = false
                                            userTopBarState.value = false

                                            CommentScreen(navController = navController)
                                        }
                                        composable<Editing> {
                                            selectedBottomNavigation = -1

                                            activeFAB = null
                                            barState.value = false
                                            userTopBarState.value = false

                                            EditingScreen(navController = navController)
                                        }
                                    }

                                    navigation<Auth>(startDestination = Login) {
                                        composable<SignUp> {
                                            selectedBottomNavigation = -1

                                            barState.value = false;
                                            userTopBarState.value = false;

                                            SignupScreen(isDarkMode = bool.value
                                                ?: isSystemInDarkTheme(),
                                                navigateToLogin = { navController.navigate(Login) },
                                                navigateHome = { navController.navigate(Home) { popUpTo<Auth>() } })
                                        }
                                        composable<Login> {
                                            selectedBottomNavigation = -1

                                            barState.value = false;
                                            userTopBarState.value = false;

                                            LoginScreen(isDarkMode = bool.value
                                                ?: isSystemInDarkTheme(),
                                                navigateToHome = { navController.navigate(Home) { popUpTo<Auth>() } },
                                                navigateToSignup = { navController.navigate(SignUp) })
                                        }
                                        composable<Verify>(
                                            deepLinks = listOf(navDeepLink {
                                                uriPattern =
                                                    "https://baddit.life/auth?emailToken={token}"
                                            })
                                        ) {
                                            selectedBottomNavigation = -1
                                            barState.value = false;
                                            userTopBarState.value = false;
                                            val token = it.arguments?.getString("token")
                                            VerifyScreen(
                                                navigateLogin = { navController.navigate(Login) },
                                                navigateHome = { navController.navigate(Home) { popUpTo<Auth>() } },
                                                token
                                            )
                                        }
                                        composable<CommunityDetail> {
                                            selectedBottomNavigation = -1
                                            barState.value = true
                                            userTopBarState.value = true

                                            val name = it.arguments?.getString("name");
                                            CommunityDetailScreen(
                                                name = name!!,
                                                navController = navController,
                                                navigatePost = { postId: String ->
                                                    navController.navigate(
                                                        Post(postId = postId)
                                                    )
                                                },
                                                navigateLogin = { navController.navigate(Login) },
                                                navigateReply = { id: String, content: String ->
                                                    activeCommentId = id
                                                    activeCommentComment = content
                                                    navController.navigate(
                                                        Comment(
                                                            postId = null,
                                                            commentId = activeCommentId,
                                                            commentContent = activeCommentComment,
                                                            darkMode = bool.value ?: false,
                                                        )
                                                    )
                                                },
                                                darkMode = bool.value ?: false,
                                            )
                                        }
                                        composable<EditCommunity> {
                                            selectedBottomNavigation = -1
                                            barState.value = false
                                            userTopBarState.value = true
                                            val name = it.arguments?.getString("name");
                                            EditCommunityScreen(name = name!!, navController)
                                        }
                                        composable<AddModerator> {
                                            selectedBottomNavigation = -1
                                            barState.value = false
                                            userTopBarState.value = true
                                            val name = it.arguments?.getString("name");
                                            AddModeratorScreen(name = name!!, navController = navController )
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    val navItems = listOf(
        BottomNavigationItem(
            icon = R.drawable.round_home_24,
            value = Home,
            unselectedIcon = R.drawable.outline_home_24,
            DisplayName = "Home"
        ),
        BottomNavigationItem(
            icon = R.drawable.round_groups_24,
            unselectedIcon = R.drawable.outline_groups_24,
            value = Community,
            DisplayName = "Explore"
        )
    )

    @Composable
    fun SlideVertically(content: @Composable () -> Unit) {
        val visibleState = remember {
            MutableTransitionState(initialState = false).apply { targetState = true }
        }

        AnimatedVisibility(
            visibleState = visibleState,
            modifier = Modifier,
            enter = slideInVertically(),
            exit = slideOutVertically() + fadeOut(),
        ) {
            content()
        }
    }
}

