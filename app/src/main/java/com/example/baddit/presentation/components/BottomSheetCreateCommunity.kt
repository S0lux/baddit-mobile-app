package com.example.baddit.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.baddit.domain.error.Result
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateCommunity(communityViewModel: CommunityViewModel, onBackButtonClick: () -> Unit) {

    val nameState = communityViewModel.nameState
    val descriptionState = communityViewModel.descriptionState
    val isLoading = communityViewModel.isLoading
    val isCreateDone = communityViewModel.isCreateDone

    LaunchedEffect(isCreateDone) {
        if (isCreateDone) {
          launch {

              delay(2000) // Delay to show the success message for a while
              onBackButtonClick() // Close the bottom sheet and navigate back
          }
        }
    }

    LaunchedEffect(Unit) {
        communityViewModel.resetCreateState()
    }

    Scaffold(topBar = {},
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onBackButtonClick()
                        communityViewModel.resetCreateState() },
                    modifier = Modifier.background(Color.Transparent)
                ) {
                    Icon(
                        Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.textPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp)) // Add space between the IconButton and Text

                Text(text = "Create Community", color = MaterialTheme.colorScheme.textPrimary, fontWeight = FontWeight.Bold)
            }

            Column(modifier = Modifier.padding(10.dp)) {

                Text(text = "Community name", color = MaterialTheme.colorScheme.textPrimary)

                Spacer(modifier = Modifier.padding(5.dp))

                OutlinedTextField(
                    value = nameState.value,
                    onValueChange = { communityViewModel.setName(it) },
                    placeholder = { Text("Community name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors(),
                    isError = nameState.error.isNotEmpty()
                )
                if(nameState.error.isNotEmpty()){
                    Text(text = "The community name already exists. Please choose another name!", color = Color.Red)}
                Spacer(modifier = Modifier.height(20.dp))

                Text(text = "Description", color = MaterialTheme.colorScheme.textPrimary)

                Spacer(modifier = Modifier.padding(5.dp))

                OutlinedTextField(
                    value = descriptionState.value,
                    onValueChange = { communityViewModel.setDescription(it) },
                    placeholder = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = textFieldColors()
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = { communityViewModel.createCommunityNonSuspend() },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.mutedAppBlue)
                ) {
                    Text(text = "Create", color = Color.White)
                }

            }
        }
        if (isCreateDone) {
            SuccessMessage()
        }
    }
}

@Composable
fun SuccessMessage() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(350.dp)
            .padding(16.dp)
            .background(
                color = Color(0xFFE8F5E9), // Light green background
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = 2.dp,
                color = Color(0xFF388E3C), // Dark green border
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF388E3C), // Dark green icon
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Community created successfully!",
                style = MaterialTheme.typography.bodyLarge,
                color = Color(0xFF388E3C), // Dark green text
                textAlign = TextAlign.Center
            )
        }
    }
}


