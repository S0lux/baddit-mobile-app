package com.example.baddit.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import com.example.baddit.Community
import com.example.baddit.CreatePost
import com.example.baddit.Home
import com.example.baddit.R


@Composable
fun BottomNavigationbar(
    navController: NavHostController,
) {
    var selectedIndex by remember { mutableStateOf(0) }
    val navitems = listOf(
        BottomNavigationItem(icon = R.drawable.round_home_24, value = Home),
        BottomNavigationItem(icon = R.drawable.round_add_24, value = CreatePost),
        BottomNavigationItem(
            icon = R.drawable.round_groups_24,
            value = Community
        ),
    )

    NavigationBar {
        navitems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = index.equals(selectedIndex),
                onClick = {
                    if (index != selectedIndex) {
                        navController.navigate(item.value)
                        selectedIndex = index
                    }
                },
                icon = {
                    Column(modifier = Modifier, horizontalAlignment = CenterHorizontally) {
                        Icon(painter = painterResource(id = item.icon), contentDescription = null)
                    }
                })

        }
    }
}

data class BottomNavigationItem(
    @DrawableRes val icon: Int,
    val value: Any
)

