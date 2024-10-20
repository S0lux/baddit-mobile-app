package com.example.baddit.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.presentation.screens.login.LoginViewModel
import com.example.baddit.presentation.utils.LeftSideBar
import com.example.baddit.presentation.utils.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavHostController,
    barState: MutableState<Boolean>,
    viewModel: LoginViewModel = hiltViewModel()
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    val navItems = listOf(
        BottomNavigationItem(icon = R.drawable.baseline_menu_24, value = LeftSideBar),
        BottomNavigationItem(icon = R.drawable.baseline_search_24, value = Search),
    )
    AnimatedVisibility(
        visible = barState.value,
        exit = slideOutVertically(),
        enter = slideInVertically()
    ) {

        TopAppBar(
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
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(
                        painter = painterResource(id = navItems[1].icon),
                        contentDescription = null
                    )
                }

            })
    }
}
