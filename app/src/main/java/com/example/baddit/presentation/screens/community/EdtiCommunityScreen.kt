package com.example.baddit.presentation.screens.community

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.presentation.components.ErrorNotification
import com.example.baddit.presentation.utils.Community
import com.example.baddit.presentation.utils.CommunityDetail
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import java.io.File
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCommunityScreen(
    name: String,
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val community = viewModel.community
    val isRefreshing = viewModel.isRefreshing
    val error = viewModel.error
    val context = LocalContext.current
    var saveCompleted by remember { mutableStateOf(false) }
    var changesMade by remember { mutableStateOf(false) }
    var isCommunityDeleted by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    var bannerImage by remember { mutableStateOf<File?>(null) }
    var logoImage by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            bannerImage = file
        }
    }

    val logoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.data?.let { uri ->
            val file = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            logoImage = file
        }
    }

    if (error.isNotEmpty()) {
        ErrorNotification(icon = R.drawable.wifi_off, text = error)
    }

    LaunchedEffect(name) {
        viewModel.fetchCommunity(name)
    }

    when {
        isRefreshing -> {
            CircularProgressIndicator()
        }

        error.isNotEmpty() -> {
            Text(
                text = error,
                color = Color.Red,
            )
        }

        community.value != null -> {
            Column {
                TopAppBar(
                    title = {
                        val titleText = "Edit Community"
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
                            Spacer(modifier = Modifier.weight(1f))

                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(CommunityDetail(name)) }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                                contentDescription = null
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.scaffoldBackground)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.Gray)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(bannerImage ?: community.value?.community?.bannerUrl)
                                    .build(), // Replace with your cover image
                                contentDescription = "Cover Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .clickable {
                                        launcher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                                            type = "image/*"
                                        })
                                    }

                            ) {
                                Icon(
                                    imageVector = Icons.Default.Build,
                                    contentDescription = "Build Icon",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                        Box(modifier = Modifier.align(Alignment.Center)) {

                            Box(
                                modifier = Modifier
                                    .size(104.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp)
                                    .align(Alignment.Center)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(logoImage ?: community.value?.community?.logoUrl)
                                            .build(),
                                        contentDescription = "Avatar Image",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .fillMaxSize()
                                    )
                                }

                            }
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .clickable {
                                        logoLauncher.launch(Intent(Intent.ACTION_GET_CONTENT).apply {
                                            type = "image/*"
                                        })
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Icon",
                                    tint = Color.Gray,
                                    modifier = Modifier
                                        .size(30.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .height(100.dp)
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = "r/${community.value?.community?.name}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.textPrimary,
                                modifier = Modifier.align(Alignment.TopCenter)
                            )
                        }

                    }

                    Box(
                    )
                    {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = { navController.navigate(CommunityDetail(name)) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Cancel", fontSize = 16.sp,  color = MaterialTheme.colorScheme.textPrimary)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Button(
                                onClick = {
                                    bannerImage?.let { viewModel.updateCommunityBanner(name, it) }
                                    logoImage?.let { viewModel.updateCommunityLogo(name, it) }
                                    saveCompleted = true
                                    changesMade = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
                                modifier = Modifier.weight(1f),
                                enabled = changesMade
                            ) {
                                Text(text = "Save", fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.textPrimary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.padding(10.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)

                    Box() {}
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Details", color = MaterialTheme.colorScheme.textPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 8.dp)
                            ) {
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Date created: ${formatDate(community.value?.community?.createdAt)}",
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontSize = 20.sp
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "",
                                    tint = MaterialTheme.colorScheme.textPrimary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${community.value?.community?.memberCount} Members",
                                    color = MaterialTheme.colorScheme.textPrimary,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(10.dp))

                    HorizontalDivider(color = MaterialTheme.colorScheme.primary)

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .fillMaxWidth()
                        ) {
                            Text("Remove Community", color = Color.White, fontSize = 20.sp)
                        }
                    }
                    if (showDeleteDialog) {
                        DeleteConfirmationDialog(
                            onConfirm = {
                                viewModel.deleteCommunity(name)
                                showDeleteDialog = false
                                isCommunityDeleted = true
                            },
                            onDismiss = { showDeleteDialog = false }
                        )
                    }
                }
            }
        }

        else -> {
            Text(text = "Error")
        }
    }
    if (saveCompleted) {
        AlertDialog(
            onDismissRequest = { saveCompleted = false },
            title = { Text("Success", color = MaterialTheme.colorScheme.textPrimary) },
            text = { Text("Your changes have been saved.", color = MaterialTheme.colorScheme.textPrimary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        saveCompleted = false
                    },
                    colors = ButtonDefaults.buttonColors(Color.Blue)
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.textPrimary)
                }
            }
        )
    }

    LaunchedEffect(bannerImage, logoImage) {
        changesMade = bannerImage != null || logoImage != null
    }

    if (isCommunityDeleted) {
        AlertDialog(
            onDismissRequest = { isCommunityDeleted = false },
            title = { Text("Community Deleted", color = MaterialTheme.colorScheme.textPrimary) },
            text = { Text("The community has been successfully deleted.", color = MaterialTheme.colorScheme.textPrimary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        isCommunityDeleted = false
                        navController.navigate(Community)
                    },
                    colors = ButtonDefaults.buttonColors(Color.Blue)
                ) {
                    Text("OK", color = MaterialTheme.colorScheme.textPrimary)
                }
            }
        )
    }

}

fun formatDate(dateString: String?): String {
    val zonedDateTime = ZonedDateTime.parse(dateString)
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    return zonedDateTime.format(formatter)
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirm Deletion", color = MaterialTheme.colorScheme.textPrimary) },
        text = { Text("Are you sure you want to delete this community? This action cannot be undone.", color = MaterialTheme.colorScheme.textPrimary) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(Color.Red)
            ) {
                Text("Delete", color = MaterialTheme.colorScheme.textPrimary)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(Color.Blue)
            ) {
                Text("Cancel", color = MaterialTheme.colorScheme.textPrimary)
            }
        }
    )
}


