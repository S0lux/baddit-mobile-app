package com.example.baddit.presentation.screens.createPost

import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.baddit.R
import com.example.baddit.presentation.components.AnimatedLogo
import com.example.baddit.presentation.styles.textFieldColors
import com.example.baddit.presentation.utils.Home
import com.example.baddit.presentation.utils.Main
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.neutralGray
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Objects

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMediaPostSCcreen(
    navController: NavHostController,
    viewmodel: CreatePostViewodel = hiltViewModel()
) {
    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val file = context.createImageFile()
    val uri = FileProvider.getUriForFile(
        Objects.requireNonNull(context),
        context.packageName + ".provider",
        file
    )

    var loadingIcon by remember {
        mutableStateOf(0)
    }
    loadingIcon = if (isSystemInDarkTheme()) R.raw.loadingiconwhite else R.raw.loadingicon

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewmodel.selectedImageUri = uri!! }
    )

    val cameraCapture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        viewmodel.selectedImageUri = uri
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Toast.makeText(context, "Permision granted", Toast.LENGTH_LONG).show()
                cameraCapture.launch(uri)
            } else {
                Toast.makeText(context, "Permision denied", Toast.LENGTH_LONG).show()
            }
        }


    if (showBottomSheet) {
        SelectCommunityBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState
        )
    }

    if (viewmodel.error == "Success") {
        LaunchedEffect(key1 = "key") {
            navController.navigate(Home) {
                popUpTo<Main>()
            }
        }

    }

    if (viewmodel.error.isNotEmpty() && viewmodel.error != "Success") {
        Toast.makeText(context, viewmodel.error, Toast.LENGTH_LONG).show()
    }
    Column(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            if (!viewmodel.isPosting) {
                IconButton(
                    onClick = { viewmodel.uploadMediaPost(context) },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_send_24),
                        contentDescription = null
                    )
                }
            }

            if (viewmodel.isPosting) {
                Box(modifier = Modifier.align(Alignment.CenterEnd)) {
                    AnimatedLogo(icon = loadingIcon, iteration = 999, size = 45.dp)
                }
            }
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(Home) {
                            popUpTo<Main>()
                        }
                    },
                    modifier = Modifier,
                    colors = IconButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = MaterialTheme.colorScheme.textPrimary,
                        disabledContentColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = null
                    )
                }

                Text(
                    text = "Media post",
                    style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.textPrimary),
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                )
            }

        }

        Spacer(modifier = Modifier.height(20.dp))

        CommunitySelector(onClick = { showBottomSheet = true }, viewmodel = viewmodel)

        Spacer(modifier = Modifier.height(10.dp))

        TextField(
            value = viewmodel.title.value,
            onValueChange = { viewmodel.onTitleChange(it) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium,
            isError = viewmodel.title.error.isNotEmpty(),
            supportingText = { Text(text = viewmodel.title.error) },
            placeholder = {
                Text(
                    text = "Title",
                    style = MaterialTheme.typography.titleMedium
                )
            },
            colors = textFieldColors()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.SpaceEvenly
        ) {
            Button(
                onClick = {
                    val permisionCheckResult = ContextCompat.checkSelfPermission(
                        context,
                        android.Manifest.permission.CAMERA
                    )
                    if (permisionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        cameraCapture.launch(uri)
                    } else {
                        permissionLauncher.launch(android.Manifest.permission.CAMERA)
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.mutedAppBlue,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.round_photo_camera_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Take a photo")
            }
            Button(
                onClick = {
                    photoPicker.launch(PickVisualMediaRequest())
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonColors(
                    containerColor = MaterialTheme.colorScheme.mutedAppBlue,
                    contentColor = Color.White,
                    disabledContentColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.outline_image_24),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(text = "Choose photo")
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        if (viewmodel.selectedImageUri != Uri.EMPTY) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.TopCenter)
                ) {
                    AsyncImage(
                        model = viewmodel.selectedImageUri,
                        contentDescription = null,
                        modifier = Modifier.height(400.dp)
                    )
                    IconButton(
                        onClick = {
                            viewmodel.selectedImageUri = Uri.EMPTY;
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = -5.dp, y = 5.dp),
                        colors = IconButtonColors(
                            containerColor = MaterialTheme.colorScheme.neutralGray,
                            contentColor = Color.White,
                            disabledContentColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent
                        )
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }
            }


        }

    }

}

fun Context.createImageFile(): File {
    val timeStamp = SimpleDateFormat("dd_MM_yyyy").format(Date())
    val imageFileName = "JPEG_" + timeStamp + "_"
    val image = File.createTempFile(
        imageFileName,
        ".jpg",
        externalCacheDir
    )
    return image
}
