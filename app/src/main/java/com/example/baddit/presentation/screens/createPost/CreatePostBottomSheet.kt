package com.example.baddit.presentation.screens.createPost

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.baddit.R
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.utils.Auth
import com.example.baddit.ui.theme.CustomTheme.PrimaryContainter
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostBottomSheet(
    onDismissRequest: () -> Unit,
    sheetState: SheetState,
    viewmodel: CreatePostViewodel = hiltViewModel(),
    navController: NavHostController
) {
    val loggedIn by viewmodel.isLoggedIn
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        LoggedInScreen(viewmodel = viewmodel, onDismissRequest = onDismissRequest)
    }
}

@Composable
private fun LoggedInScreen(viewmodel: CreatePostViewodel, onDismissRequest: () -> Unit) {
    val singlePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewmodel.selectedImage = uri }
    )
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Button(
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    viewmodel.res = viewmodel.title + viewmodel.content
                },
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.PrimaryContainter,
                    contentColor = MaterialTheme.colorScheme.textPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                ),
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    "Post",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            IconButton(
                onClick = onDismissRequest,
                modifier = Modifier.align(Alignment.CenterStart),
                colors = IconButtonColors(
                    containerColor = Color.LightGray,
                    contentColor = MaterialTheme.colorScheme.textPrimary,
                    disabledContainerColor = Color.Transparent,
                    disabledContentColor = Color.Transparent
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_arrow_back_24),
                    contentDescription = null
                )
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "CREATE POST",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 13.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

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

        if (viewmodel.linkTypeSelected) {
            TextField(
                value = viewmodel.content,
                onValueChange = { viewmodel.content = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium,
                placeholder = {
                    Text(
                        text = "URL",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                colors = textFieldColors(),
                suffix = {
                    IconButton(onClick = { viewmodel.linkTypeSelected = false }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            )

        }

        if (viewmodel.mediaTypeSelected && viewmodel.selectedImage != null) {
            AsyncImage(model = viewmodel.selectedImage, contentDescription = null)
        }

        TextField(
            value = viewmodel.content,
            onValueChange = { viewmodel.content = it },
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            singleLine = false,
            minLines = 20,
            textStyle = MaterialTheme.typography.bodyMedium,
            placeholder = {
                Text(
                    text = "body text (optional)",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            colors = textFieldColors()
        )
        Spacer(modifier = Modifier.height(10.dp))

        Row {
            IconButton(
                onClick = { viewmodel.linkTypeSelected = true },
                enabled = !viewmodel.mediaTypeSelected
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_insert_link_24),
                    contentDescription = null
                )

            }
            IconButton(onClick = {
                viewmodel.mediaTypeSelected = true
                singlePhotoPicker.launch(
                    PickVisualMediaRequest()
                )
            }, enabled = !viewmodel.linkTypeSelected) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_image_24),
                    contentDescription = null
                )

            }

        }

    }
}
