package com.example.baddit.presentation.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.ui.theme.BadditTheme
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(viewModel: SignupViewModel = hiltViewModel()) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()
    val isDarkTheme = isSystemInDarkTheme()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SignupContent(
            pagerState = pagerState,
            isDarkTheme = isDarkTheme,
            viewModel = viewModel,
            coroutineScope = coroutineScope
        )
    }
}

@Composable
fun SignupContent(
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
    Text(
        text = "Signup",
        color = MaterialTheme.colorScheme.textPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 25.sp
    )
}

@Composable
fun UserCredentialsSection(viewModel: SignupViewModel) {
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
            isError = usernameError.isNotEmpty()
        )

        OutlinedTextField(
            label = { Text("Email") },
            singleLine = true,
            value = viewModel.emailField,
            onValueChange = { email -> viewModel.setEmail(email) },
            supportingText = { Text(emailError) },
            isError = emailError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun PasswordSection(viewModel: SignupViewModel) {
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
            isError = passwordError.isNotEmpty()
        )

        OutlinedTextField(
            label = { Text("Confirm password") },
            singleLine = true,
            value = viewModel.confirmPasswordField,
            onValueChange = { password -> viewModel.setConfirmationPassword(password) },
            supportingText = { Text(confirmPasswordError) },
            isError = confirmPasswordError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
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
    val canProceed = (pagerState.currentPage == 0 && viewModel.usernameField.isNotEmpty() && viewModel.emailField.isNotEmpty() && viewModel.usernameFieldError.isEmpty() && viewModel.emailFieldError.isEmpty())
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
            enabled = !isLoading && canProceed
        ) {
            Text(if (pagerState.currentPage == 0) "Next" else "Signup")
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            SignupScreen()
        }
    }
}
