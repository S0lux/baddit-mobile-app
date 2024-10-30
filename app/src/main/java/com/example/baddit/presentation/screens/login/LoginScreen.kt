package com.example.baddit.presentation.screens.login

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.domain.error.Result
import com.example.baddit.presentation.components.Alert
import com.example.baddit.presentation.components.AppLogo
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.ui.theme.CustomTheme.errorRed
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(isDarkMode: Boolean = isSystemInDarkTheme(),
    viewModel: LoginViewModel = hiltViewModel(),
    navigateToHome: () -> Unit,
    navigateToSignup: () -> Unit
) {
    val isDarkMode = isDarkMode;
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            AppLogo(isDarkTheme = isDarkMode)

            Column(
                modifier = Modifier.padding(50.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LoginHeader(navigateToSignup)

                Spacer(modifier = Modifier.size(5.dp))

                CredentialsInputs(viewModel = viewModel)

                AnimatedVisibility(visible = viewModel.generalError.isNotEmpty()) {
                    Alert {
                        Text(
                            text = viewModel.generalError,
                            color = MaterialTheme.colorScheme.errorRed
                        )
                    }
                }

                LoginButton(viewModel = viewModel, coroutineScope = coroutineScope, navigateToHome)
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LoginHeader(onSignupClicked: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "Login",
            color = MaterialTheme.colorScheme.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        FlowRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "Don't have an account?",
                color = MaterialTheme.colorScheme.textSecondary,
            )

            Text(
                text = "Signup now",
                color = MaterialTheme.colorScheme.mutedAppBlue,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onSignupClicked() }
            )
        }

    }

}

@Composable
fun CredentialsInputs(viewModel: LoginViewModel) {
    val focusManager = LocalFocusManager.current

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
            label = { Text("Username") },
            singleLine = true,
            value = viewModel.usernameState.value,
            onValueChange = { username -> viewModel.onUsernameChange(username) },
            supportingText = { Text(viewModel.usernameState.error) },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.usernameState.error.isNotEmpty(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.person),
                    contentDescription = null
                )
            }
        )

        OutlinedTextField(
            label = { Text("Password") },
            singleLine = true,
            value = viewModel.passwordState.value,
            onValueChange = { email -> viewModel.onPasswordChange(email) },
            supportingText = { Text(viewModel.passwordState.error) },
            isError = viewModel.passwordState.error.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.key),
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun LoginButton(
    viewModel: LoginViewModel,
    coroutineScope: CoroutineScope,
    onLoginSuccess: () -> Unit
) {
    val canProceed =
        viewModel.usernameState.value.isNotEmpty() && viewModel.passwordState.value.isNotEmpty()

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    val result = viewModel.login()

                    if (result is Result.Success) onLoginSuccess()
                }
            },
            enabled = canProceed,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.mutedAppBlue
            )
        ) {
            Text("Login", color = MaterialTheme.colorScheme.textPrimary)
        }
    }
}