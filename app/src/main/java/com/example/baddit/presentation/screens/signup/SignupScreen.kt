package com.example.baddit.presentation.screens.signup

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(viewModel: SignupViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        contentAlignment = Alignment.Center,
    ) {
        if (viewModel.isSignupDone) {
            SignUpComplete()
        } else
            SignupProcess(
                pagerState = pagerState,
                isDarkTheme = isDarkTheme,
                viewModel = viewModel,
                coroutineScope = coroutineScope
            )
    }
}

@Composable
fun SignupProcess(
    pagerState: PagerState,
    isDarkTheme: Boolean,
    viewModel: SignupViewModel,
    coroutineScope: CoroutineScope
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AppLogo(isDarkTheme = isDarkTheme)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(50.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SignupHeader()
                Spacer(modifier = Modifier.size(5.dp))

                HorizontalPager(
                    state = pagerState,
                    pageSpacing = 50.dp,
                    userScrollEnabled = false
                ) { page ->
                    if (page == 0) {
                        UserCredentialsSection(viewModel)
                    } else {
                        PasswordSection(viewModel)
                    }
                }

                Spacer(modifier = Modifier.size(25.dp))

                NavigationButton(pagerState, viewModel, coroutineScope)
            }
        }
    }
}

@Composable
fun AppLogo(isDarkTheme: Boolean) {
    Icon(
        painter = painterResource(id = R.drawable.baddit_white),
        contentDescription = null,
        modifier = Modifier.size(200.dp),
        tint = if (isDarkTheme) Color.White else Color.Black
    )
}

@Composable
fun SignupHeader() {
    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            text = "Signup",
            color = MaterialTheme.colorScheme.textPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 25.sp
        )

        Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            Text(
                text = "Already has an account?",
                color = MaterialTheme.colorScheme.textSecondary
            )

            Text(
                text = "Login now",
                color = MaterialTheme.colorScheme.mutedAppBlue,
                fontWeight = FontWeight.SemiBold
            )
        }

    }

}

@Composable
fun UserCredentialsSection(viewModel: SignupViewModel) {
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val usernameError = viewModel.usernameFieldError
        val emailError = viewModel.emailFieldError

        OutlinedTextField(
            label = { Text("Username") },
            singleLine = true,
            value = viewModel.usernameField,
            onValueChange = { username -> viewModel.setUsername(username) },
            supportingText = { Text(usernameError) },
            modifier = Modifier.fillMaxWidth(),
            isError = usernameError.isNotEmpty(),
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
            label = { Text("Email") },
            singleLine = true,
            value = viewModel.emailField,
            onValueChange = { email -> viewModel.setEmail(email) },
            supportingText = { Text(emailError) },
            isError = emailError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.email),
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
fun PasswordSection(viewModel: SignupViewModel) {
    val focusManager = LocalFocusManager.current
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val passwordError = viewModel.passwordFieldError
        val confirmPasswordError = viewModel.confirmPasswordFieldError

        OutlinedTextField(
            label = { Text("Password") },
            singleLine = true,
            value = viewModel.passwordField,
            onValueChange = { password -> viewModel.setPassword(password) },
            supportingText = { Text(passwordError) },
            modifier = Modifier.fillMaxWidth(),
            isError = passwordError.isNotEmpty(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = textFieldColors(),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.key),
                    contentDescription = null
                )
            },
            visualTransformation = PasswordVisualTransformation()
        )

        OutlinedTextField(
            label = { Text("Confirm password") },
            singleLine = true,
            value = viewModel.confirmPasswordField,
            onValueChange = { password -> viewModel.setConfirmationPassword(password) },
            supportingText = { Text(confirmPasswordError) },
            isError = confirmPasswordError.isNotEmpty(),
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
fun NavigationButton(
    pagerState: PagerState,
    viewModel: SignupViewModel,
    coroutineScope: CoroutineScope
) {
    val isLoading = viewModel.isLoading
    val canProceed =
        (pagerState.currentPage == 0 && viewModel.usernameField.isNotEmpty() && viewModel.emailField.isNotEmpty() && viewModel.usernameFieldError.isEmpty() && viewModel.emailFieldError.isEmpty())
                || (pagerState.currentPage == 1 && viewModel.passwordField.isNotEmpty() && viewModel.confirmPasswordField.isNotEmpty() && viewModel.passwordFieldError.isEmpty() && viewModel.confirmPasswordFieldError.isEmpty())

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage == 0) {
                        pagerState.animateScrollToPage(1)
                    } else {
                        val isSuccess = viewModel.trySignUp()
                        if (!isSuccess) pagerState.animateScrollToPage(0)
                    }
                }
            },
            enabled = !isLoading && canProceed,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.mutedAppBlue
            )
        ) {
            if (pagerState.currentPage == 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text("Next")
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_forward),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else
                Text("Signup", color = MaterialTheme.colorScheme.textPrimary)
        }
    }
}
