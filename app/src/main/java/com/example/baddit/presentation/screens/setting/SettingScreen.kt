package com.example.baddit.presentation.screens.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.wear.compose.material3.OutlinedButton
import com.example.baddit.R
import com.example.baddit.domain.error.Result
import com.example.baddit.presentation.components.BadditDialog
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

        val openConfirmDialog = remember { mutableStateOf(false) }

        val openBugReportDialog = remember { mutableStateOf(false) }

        val openContentPolicyDialog = remember { mutableStateOf(false) }

        // Dialog to select theme
        if (openThemeDialog.value) {
            Dialog(onDismissRequest = { openThemeDialog.value = false }) {
                Card(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth(0.85f)
                        .padding(start = 14.dp, top = 14.dp, end = 14.dp, bottom = 18.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)

                ) {
                    Column(
                        modifier = Modifier
                    ) {

                        Text(
                            text = "Select Theme",
                            modifier = Modifier
                                .padding(start = 10.dp, end = 10.dp, top = 15.dp, bottom = 10.dp)
                                .align(Alignment.CenterHorizontally)
                                .fillMaxWidth(),
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
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
                                .padding(start = 10.dp, end = 10.dp, top = 20.dp, bottom = 10.dp)
                                .align(Alignment.CenterHorizontally),
                            color = MaterialTheme.colorScheme.textPrimary,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        CustomPasswordField(
                            value = viewModel.oldPasswordState.value,
                            onValueChange = { oldPassword: String ->
                                viewModel.setOldPassword(oldPassword)
                            },
                            label = "Current password",
                            isError = viewModel.oldPasswordState.error.isNotEmpty(),
                            supportingText = viewModel.oldPasswordState.error
                        )

                        CustomPasswordField(
                            value = viewModel.newPasswordState.value,
                            onValueChange = { newPassword: String ->
                                viewModel.setNewPassword(newPassword)
                            },
                            label = "New password",
                            isError = viewModel.newPasswordState.error.isNotEmpty(),
                            supportingText = viewModel.newPasswordState.error
                        )

                        CustomPasswordField(
                            value = viewModel.confirmPasswordState.value,
                            onValueChange = { checkPassword: String ->
                                viewModel.setConfirmPassword(checkPassword)
                            },
                            label = "Comfirm password",
                            isError = viewModel.confirmPasswordState.error.isNotEmpty(),
                            supportingText = viewModel.confirmPasswordState.error
                        )

                        if (viewModel.error.value.isNotEmpty()) {
                            Text(
                                text = viewModel.error.value,
                                color = Color.Red,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 20.dp),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

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
                                onClick = {
                                    coroutine.launch {
                                        val result = viewModel.ChangePassword()

                                        if (result is Result.Success) {
                                            openPasswordDialog.value = false;
                                            openConfirmDialog.value = true;
                                            viewModel.confirmDialogText.value = "Your password has been changed successfully"
                                            viewModel.confirmDialogTitle.value = "Password Changed"
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .height(45.dp),
                                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.appBlue),
                                enabled = viewModel.newPasswordState.error.isEmpty() && viewModel.confirmPasswordState.error.isEmpty() && viewModel.oldPasswordState.error.isEmpty() && !viewModel.isLoading
                            ) {
                                Text(
                                    "Save",
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.CenterVertically),
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))
                        }

                    }
                }

            }
        }

        //Confirm dialog
        if (openConfirmDialog.value) {
            AlertDialog(
                modifier = Modifier.wrapContentHeight(),
                title = {
                    Text(
                        text = viewModel.confirmDialogTitle.value,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                    )
                },
                text = {
                    Text(
                        modifier = Modifier.padding(
                            start = 20.dp,
                            top = 10.dp,
                            end = 20.dp,
                        ),
                        text = viewModel.confirmDialogText.value,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                },
                onDismissRequest = { openConfirmDialog.value = false },
                confirmButton = {
                    Button(
                        shape = RoundedCornerShape(10.dp),
                        onClick = {
                            openConfirmDialog.value = false
                        },
                        modifier = Modifier
                            .height(45.dp),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.appBlue),
                    ) {
                        Text(
                            "OK",
                            modifier = Modifier
                                .padding(5.dp)
                                .align(Alignment.CenterVertically),
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                },
                shape = RoundedCornerShape(10.dp),
                containerColor = MaterialTheme.colorScheme.background
            )
        }

        // Dialog to report a bug
        if (openBugReportDialog.value) {
            AlertDialog(
                onDismissRequest = { openBugReportDialog.value = false },
                confirmButton = {TextButton(onClick = {
                    openBugReportDialog.value = false
                    viewModel.confirmDialogText.value = "Thank you for your feedback"
                    viewModel.confirmDialogTitle.value = "Report Sent"
                    viewModel.reportBugTitleState.value = ""
                    viewModel.reportBugDesState.value = ""
                    openConfirmDialog.value = true
                }, enabled = viewModel.reportBugTitleState.value.isNotEmpty()){
                    Text("Sent", color = MaterialTheme.colorScheme.textPrimary)
                }},
                title = { Text(text = "Bug Report", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = { Column {
                    Text("Title: ", modifier = Modifier.padding(10.dp))
                    TextField(value = viewModel.reportBugTitleState.value, onValueChange = {text -> viewModel.setReportBugTitle(text)}, colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.appBlue,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = MaterialTheme.colorScheme.appBlue,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.textPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.textPrimary,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.textPrimary,
                    ), placeholder = {Text("Enter title")})
                    Text("Description: ", modifier = Modifier.padding(10.dp))
                    TextField(value = viewModel.reportBugDesState.value, onValueChange = {text -> viewModel.setReportBugDes(text)}, colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = MaterialTheme.colorScheme.appBlue,
                        unfocusedContainerColor = Color.Transparent,
                        focusedLabelColor = MaterialTheme.colorScheme.appBlue,
                        focusedContainerColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.textPrimary,
                        unfocusedTextColor = MaterialTheme.colorScheme.textPrimary,
                        errorContainerColor = Color.Transparent,
                        cursorColor = MaterialTheme.colorScheme.textPrimary,), placeholder = {Text("Enter description")})
                } },
                shape = RoundedCornerShape(10.dp),
                containerColor = MaterialTheme.colorScheme.background
            )
        }

        if (openContentPolicyDialog.value) {
            AlertDialog(
                onDismissRequest = { openContentPolicyDialog.value = false },
                confirmButton = {TextButton(onClick = { openContentPolicyDialog.value = false }) {
                    Text("Confirm", color = MaterialTheme.colorScheme.textPrimary)
                }},
                title = { Text(text = "Content Policy", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()) },
                text = { Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("1: Prohibition of Hate Speech and Harassment:\n")
                    }
                    append("\nContent that promotes violence or hatred against individuals or groups based on attributes such as race, ethnicity, religion, gender, sexual orientation, disability, or nationality is not allowed. Harassment, bullying, and threats are also strictly prohibited.\n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("2: Ban on Explicit Content:\n")
                    }
                    append("\nPosting sexually explicit content, including pornography, is prohibited. This policy also includes the sharing of graphic violence, gore, and other disturbing imagery that is intended to shock or disgust.\n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("3: No Illegal Activities:\n")
                    }
                    append("\nContent that promotes, encourages, or facilitates illegal activities is banned. This includes drug trafficking, human trafficking, illegal weapons sales, and any form of criminal activity.\n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("4: Protection of Intellectual Property:\n")
                    }
                    append("\nContent that infringes on someone else's intellectual property rights, such as copyright or trademark violations, is not allowed. Users must ensure they have the necessary rights to share any content they post.\n\n")

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("5: Misinformation and False Information:\n")
                    }
                    append("\nSpreading false information, particularly that which can cause harm, such as health-related misinformation (e.g., false medical advice), or misinformation affecting public safety and elections, is prohibited.")
                }) }},
                shape = RoundedCornerShape(10.dp),
                containerColor = MaterialTheme.colorScheme.background
                
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        CustomDivider(label = "General Setting")

        val icon = if (darkTheme == true) {
            R.drawable.baseline_dark_mode_24
        } else {
            R.drawable.baseline_light_mode_24
        }

        SettingItem(
            icon = painterResource(id = icon),
            text = "Theme",
            onClick = { openThemeDialog.value = true })

        Spacer(modifier = Modifier.height(10.dp))

        CustomDivider(label = "Account Setting")

        SettingItem(
            icon = painterResource(id = R.drawable.key),
            text = "Change Password",
            onClick = { openPasswordDialog.value = true })

        Spacer(modifier = Modifier.height(10.dp))

        CustomDivider(label = "Other")

        SettingItem(
            icon = painterResource(id = R.drawable.baseline_bug_report_24),
            text = "Bug Report",
            onClick = { openBugReportDialog.value = true })

        SettingItem(
            icon = painterResource(id = R.drawable.baseline_policy_24),
            text = "Content Policy",
            onClick = { openContentPolicyDialog.value = true })

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDivider(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(23.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.appBlue,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 13.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    supportingText: String = "",
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
            errorContainerColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.textPrimary,
        ),
        isError = isError,
        supportingText = { Text(supportingText) },
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