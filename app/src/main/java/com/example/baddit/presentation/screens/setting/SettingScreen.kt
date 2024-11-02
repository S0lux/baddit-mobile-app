package com.example.baddit.presentation.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.baddit.R
import com.example.baddit.presentation.utils.Home
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController,
                  switchTheme: suspend (Boolean) -> Unit,
                  darkTheme: Boolean) {

    var selectedTheme by remember { mutableStateOf(when(darkTheme) {
        true -> "Dark"
        false -> "Light"

    }) }
    val themes = listOf("Dark", "Light", "System")

    Column {
        TopAppBar(
            title = {
                val titleText = "Settings"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = titleText,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                }
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(Home) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.scaffoldBackground)
        )

        val openDialog = remember { mutableStateOf(false) }
        if (openDialog.value) {
            Dialog(onDismissRequest = { openDialog.value = false }) {
                Card(
                    modifier = Modifier.wrapContentHeight()
                        .fillMaxWidth(0.85f)
                        .padding(14.dp).align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)

                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            text = "Select Theme",
                            modifier = Modifier.padding(10.dp).align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        themes.forEach { theme ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedTheme == theme,
                                    onClick = { selectedTheme = theme },
                                    modifier = Modifier.selectable(
                                        selected = selectedTheme == theme,
                                        onClick = { selectedTheme = theme }
                                    ),
                                    colors = RadioButtonColors(
                                        selectedColor = MaterialTheme.colorScheme.appBlue,
                                        unselectedColor = MaterialTheme.colorScheme.neutralGray,
                                        disabledSelectedColor = MaterialTheme.colorScheme.neutralGray,
                                        disabledUnselectedColor = MaterialTheme.colorScheme.neutralGray
                                    )
                                )
                                Text(
                                    text = theme,
                                    modifier = Modifier.padding(start = 5.dp),
                                    color = MaterialTheme.colorScheme.textPrimary
                                )
                            }

                        }
                    }
                }

            }
        }

        SettingItem(
            icon = painterResource(id = R.drawable.baseline_light_mode_24),
            text = "Theme",
            onClick = { openDialog.value = true })
    }
}


@Composable
fun SettingItem(icon: Painter, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clickable(
                onClick = onClick
            )
            .fillMaxWidth()
            .defaultMinSize(Dp.Unspecified, 40.dp)
            .height(50.dp). padding(start = 20.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier,
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.textPrimary,
                painter = icon,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.fillMaxWidth(0.02f))
            Text(text = text, fontSize = 14.sp, color = MaterialTheme.colorScheme.textPrimary)
        }

        Icon(
            modifier = Modifier.size(20.dp),
            painter = painterResource(id = R.drawable.arrow_right),
            contentDescription = "", tint = MaterialTheme.colorScheme.textPrimary
        )
    }
}