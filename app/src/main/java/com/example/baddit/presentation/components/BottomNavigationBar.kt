package com.example.baddit.presentation.components

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.cardForeground
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.launch


@Composable
fun BottomNavigationBar(
    navItems: List<BottomNavigationItem>,
    navController: NavHostController,
    selectedItem: Int,
    barState: MutableState<Boolean>,
    drawerState: DrawerState
) {
    val selectedIndex = selectedItem
    val scope = rememberCoroutineScope()
    AnimatedVisibility(
        visible = barState.value,
        exit = slideOutVertically(),
        enter = slideInVertically()
    ) {
        NavigationBar(
            modifier = Modifier
                .height(60.dp)
                .shadow(elevation = 1.dp),
            containerColor = MaterialTheme.colorScheme.cardBackground
        ) {
            navItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedIndex,
                    modifier = Modifier.fillMaxHeight(),
                    colors = NavigationBarItemColors(
                        selectedIconColor = MaterialTheme.colorScheme.textPrimary,
                        selectedTextColor = Color.Transparent,
                        selectedIndicatorColor = Color.Transparent,
                        unselectedIconColor = MaterialTheme.colorScheme.textSecondary,
                        unselectedTextColor = Color.Transparent,
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    ),
                    onClick = {
                        if(drawerState.isOpen){
                            scope.launch {
                                drawerState.close()
                            }
                        }
                        if(selectedIndex!=index&&drawerState.isClosed){
                            navController.navigate(navItems[index].value)
                        }
                    },
                    icon = {
                        Column(modifier = Modifier, horizontalAlignment = CenterHorizontally) {
                            Icon(
                                painter = if (selectedIndex == index) painterResource(id = item.icon) else painterResource(
                                    id = item.unselectedIcon
                                ),
                                contentDescription = null
                            )
                            Text(
                                text = item.DisplayName,
                                style = MaterialTheme.typography.titleSmall
                            )

                        }
                    },

                    )
            }
        }
    }
}

data class BottomNavigationItem(
    @DrawableRes val icon: Int,
    @DrawableRes val unselectedIcon: Int,
    val DisplayName: String,
    val value: Any
)


