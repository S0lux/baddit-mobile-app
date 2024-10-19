package com.example.baddit.presentation.screens.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.example.baddit.domain.error.Result
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
        AnimatedVisibility(
            visible = viewModel.isSignupDone,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            SignUpComplete()
        }

        AnimatedVisibility(
            visible = !viewModel.isSignupDone,
            enter = fadeIn(animationSpec = tween(durationMillis = 500)),
            exit = fadeOut(animationSpec = tween(durationMillis = 500))
        ) {
            SignupProcess(
                pagerState = pagerState,
                isDarkTheme = isDarkTheme,
                viewModel = viewModel,
                coroutineScope = coroutineScope
            )
        }
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
        OutlinedTextField(
            label = { Text("Username") },
            singleLine = true,
            value = viewModel.usernameState.value,
            onValueChange = { username -> viewModel.setUsername(username) },
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
            label = { Text("Email") },
            singleLine = true,
            value = viewModel.emailState.value,
            onValueChange = { email -> viewModel.setEmail(email) },
            supportingText = { Text(viewModel.emailState.error) },
            isError = viewModel.emailState.error.isNotEmpty(),
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
        OutlinedTextField(
            label = { Text("Password") },
            singleLine = true,
            value = viewModel.passwordState.value,
            onValueChange = { password -> viewModel.setPassword(password) },
            supportingText = { Text(viewModel.passwordState.error) },
            modifier = Modifier.fillMaxWidth(),
            isError = viewModel.passwordState.error.isNotEmpty(),
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
            value = viewModel.confirmPasswordState.value,
            onValueChange = { password -> viewModel.setConfirmationPassword(password) },
            supportingText = { Text(viewModel.confirmPasswordState.error) },
            isError = viewModel.confirmPasswordState.error.isNotEmpty(),
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
        (pagerState.currentPage == 0 && viewModel.usernameState.value.isNotEmpty() && viewModel.emailState.value.isNotEmpty() && viewModel.usernameState.error.isEmpty() && viewModel.emailState.error.isEmpty())
                || (pagerState.currentPage == 1 && viewModel.passwordState.value.isNotEmpty() && viewModel.confirmPasswordState.value.isNotEmpty() && viewModel.passwordState.error.isEmpty() && viewModel.confirmPasswordState.error.isEmpty())

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Button(
            onClick = {
                coroutineScope.launch {
                    if (pagerState.currentPage == 0) {
                        pagerState.animateScrollToPage(
                            page = 1,
                            animationSpec = tween(
                                durationMillis = 600,
                                easing = FastOutSlowInEasing
                            )
                        )
                    } else {
                        val result = viewModel.trySignUp()
                        if (result is Result.Error) {
                            pagerState.animateScrollToPage(
                                page = 0,
                                animationSpec = tween(
                                    durationMillis = 600,
                                    easing = FastOutSlowInEasing
                                )
                            )
                        }
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
