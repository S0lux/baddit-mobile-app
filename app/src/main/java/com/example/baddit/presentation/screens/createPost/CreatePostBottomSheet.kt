package com.example.baddit.presentation.screens.createPost

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.ui.theme.CustomTheme.PrimaryContainter
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    viewmodel: CreatePostViewodel = hiltViewModel(),
    navController: NavHostController
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier.fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 15.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if(viewmodel.isLoggedIn) "CREATE POST" else "YOU'RE NOT LOGGED IN",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if(viewmodel.isLoggedIn){
                TextField(
                    value = viewmodel.title,
                    onValueChange = { viewmodel.title = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleMedium,
                    placeholder = {
                        Text(
                            text = "Title",
                            style = MaterialTheme.typography.titleMedium
                        )
                    },
                    colors = textFieldColors()
                )

                TextField(
                    value = viewmodel.content,
                    onValueChange = { viewmodel.content = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    singleLine = false,
                    minLines = 10,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    placeholder = {
                        Text(
                            text = "body text (optional)",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    colors = textFieldColors()
                )

            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {navController.navigate(Auth); onDismissRequest()},
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.PrimaryContainter,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
                    Text(if(viewmodel.isLoggedIn)"Post" else "Log in", style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = onDismissRequest,
                    colors = ButtonColors(
                        containerColor = Color.LightGray,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContainerColor = Color.Transparent,
                        disabledContentColor = Color.Transparent
                    )
                ) {
                    Text(text = "Cancel", style = MaterialTheme.typography.titleMedium)
                }
            }
            Text(text = viewmodel.title + viewmodel.content)
        }
    }
}

