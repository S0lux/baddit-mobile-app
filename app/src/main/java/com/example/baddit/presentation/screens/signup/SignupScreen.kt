package com.example.baddit.presentation.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.baddit.R
import com.example.baddit.ui.theme.BadditTheme
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.launch

@Composable
fun SignupScreen(viewModel: SignupViewModel = hiltViewModel()) {
    val emailError = viewModel.emailFieldError;
    val usernameError = viewModel.usernameFieldError;
    val passwordError = viewModel.passwordFieldError;
    val confirmPasswordError = viewModel.confirmPasswordFieldError;
    val isLoading = viewModel.isLoading;
    val pager = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope();

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baddit_white),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
                tint = if (isSystemInDarkTheme()) Color.White else Color.Black
            )

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(50.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Signup",
                        color = MaterialTheme.colorScheme.textPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )

                    Spacer(modifier = Modifier.size(5.dp))

                    HorizontalPager(
                        state = pager, pageSpacing = 50.dp, userScrollEnabled = false
                    ) { page ->
                        if (page == 0) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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

                        if (page == 1) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
                                    onValueChange = { password ->
                                        viewModel.setConfirmationPassword(
                                            password
                                        )
                                    },
                                    supportingText = { Text(confirmPasswordError) },
                                    isError = confirmPasswordError.isNotEmpty(),
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.size(25.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd
                    ) {
                        Button(
                            onClick = {
                                if (pager.currentPage == 0) {
                                    coroutineScope.launch {
                                        pager.animateScrollToPage(1)
                                    }
                                }
                                else {
                                    coroutineScope.launch {
                                        val isSuccess = viewModel.trySignUp()
                                        if (!isSuccess) pager.animateScrollToPage(0)
                                    }
                                }
                            },

                            enabled = (!isLoading && (pager.currentPage == 0 && viewModel.usernameField.isNotEmpty() && viewModel.emailField.isNotEmpty() && usernameError.isEmpty() && emailError.isEmpty())
                                    || (pager.currentPage == 1 && viewModel.passwordField.isNotEmpty() && viewModel.confirmPasswordField.isNotEmpty() && passwordError.isEmpty() && confirmPasswordError.isEmpty()))
                        ) {
                            Text(if (pager.currentPage == 0) "Next" else "Signup")
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SignupScreenPreview() {
    BadditTheme {
        Surface(
            modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
        ) {
            SignupScreen()
        }
    }
}