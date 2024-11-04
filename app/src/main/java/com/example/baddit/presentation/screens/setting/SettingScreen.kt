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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.compose.material3.Button
import androidx.wear.compose.material3.OutlinedButton
import com.example.baddit.R
import com.example.baddit.presentation.utils.Home
import com.example.baddit.ui.theme.CustomTheme.appBlue
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    switchTheme: suspend (String) -> Unit,
    darkTheme: Boolean?,
    viewModel: SettingViewModel = hiltViewModel()
) {
    var coroutine = rememberCoroutineScope();

    var selectedTheme by remember {
        mutableStateOf(
            when (darkTheme) {
                true -> "Dark"
                false -> "Light"
                else -> "System"
            }
        )
    }

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

        val openThemeDialog = remember { mutableStateOf(false) }

        val openPasswordDialog = remember { mutableStateOf(false) }

        // Dialog to select theme
        if (openThemeDialog.value) {
            Dialog(onDismissRequest = { openThemeDialog.value = false }) {
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(0.85f)
                        .padding(14.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)

                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            text = "Select Theme",
                            modifier = Modifier
                                .padding(10.dp)
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        themes.forEach { theme ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedTheme == theme,
                                    onClick = {
                                        selectedTheme = theme
                                        coroutine.launch {
                                            switchTheme(theme)
                                        }
                                    },
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

        // Dialog to change password
        if (openPasswordDialog.value) {
            Dialog(onDismissRequest = { openPasswordDialog.value = false }) {
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(14.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)

                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            text = "Change Password",
                            modifier = Modifier
                                .padding(start =  10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        CustomPasswordField(
                            value = viewModel.oldPassword.value,
                            onValueChange = { oldPassword: String ->
                                viewModel.setOldPassword(oldPassword)
                            },
                            label = "Current password"
                        )

                        CustomPasswordField(
                            value = viewModel.newPassword.value,
                            onValueChange = { newPassword: String ->
                                viewModel.setNewPassword(newPassword)
                            },
                            label = "New password"
                        )

                        CustomPasswordField(
                            value = viewModel.checkPassword.value,
                            onValueChange = { checkPassword: String ->
                                viewModel.setCheckPassword(checkPassword)
                            },
                            label = "Comfirm password"
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(20.dp)
                        ) {

                            Spacer(modifier = Modifier.weight(1f))

                            OutlinedButton(
                                shape = RoundedCornerShape(10.dp),
                                onClick = { openPasswordDialog.value = false },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .height(45.dp)
                            ) {
                                Text(
                                    "Cancel",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.CenterVertically),
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            Button(
                                shape = RoundedCornerShape(10.dp),
                                onClick = { /*TODO*/ },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .height(45.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.appBlue)
                            ) {
                                Text(
                                    "Save",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.CenterVertically),
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                        }

                    }
                }

            }
        }


        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.neutralGray.copy(alpha = 0.1f))
                .fillMaxWidth()
                .height(20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "General Setting",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.textPrimary,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 13.dp)
            )
        }

        val icon = if (darkTheme == true) {
            R.drawable.baseline_dark_mode_24
        } else {
            R.drawable.baseline_light_mode_24
        }

        SettingItem(
            icon = painterResource(id = icon),
            text = "Theme",
            onClick = { openThemeDialog.value = true })

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.neutralGray.copy(alpha = 0.1f))
                .fillMaxWidth()
                .height(20.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Account Setting",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.textPrimary,
                fontWeight = FontWeight.Light,
                modifier = Modifier.padding(start = 13.dp)
            )
        }

        SettingItem(
            icon = painterResource(id = R.drawable.key),
            text = "Change Password",
            onClick = { openPasswordDialog.value = true })

        SettingItem(
            icon = painterResource(id = R.drawable.email),
            text = "Re-send Verification Email",
            onClick = { })
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
) {
    OutlinedTextField(
        modifier = Modifier.padding(start = 20.dp, end = 20.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = MaterialTheme.colorScheme.appBlue,
            unfocusedContainerColor = Color.Transparent,
            focusedLabelColor = MaterialTheme.colorScheme.appBlue,
            focusedContainerColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.textPrimary,
            unfocusedTextColor = MaterialTheme.colorScheme.textPrimary,
        )
    )
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
            .height(50.dp)
            .padding(start = 20.dp, end = 20.dp),
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