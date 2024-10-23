package com.example.baddit.presentation.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.baddit.R
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.Home
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.cardForeground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary


@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    barState: MutableState<Boolean>
) {
    var selectedIndex by remember { mutableIntStateOf(0) }
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

    AnimatedVisibility(
        visible = barState.value,
        exit = slideOutVertically(),
        enter = slideInVertically()
    ) {
        NavigationBar(
            modifier = Modifier.height(60.dp),
            containerColor = Color.White
        ) {
            navItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedIndex,
                    modifier = Modifier.fillMaxHeight(),
                    colors = NavigationBarItemColors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = Color.Transparent,
                        selectedIndicatorColor = Color.Transparent,
                        unselectedIconColor = MaterialTheme.colorScheme.textSecondary,
                        unselectedTextColor = Color.Transparent,
                        disabledIconColor = Color.Transparent,
                        disabledTextColor = Color.Transparent
                    ),
                    onClick = {
                        if (index != selectedIndex) {
                            navController.navigate(item.value)
                            selectedIndex = index
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
                            Text(text = item.DisplayName, style = MaterialTheme.typography.titleSmall)

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

@Preview(showBackground = true)
@Composable
fun previewNavBar() {
    Surface(
        modifier = Modifier
            .wrapContentSize()
    ) {
        BottomNavigationBar(navController = rememberNavController(), barState = remember {
            mutableStateOf(true)
        })
    }
}

