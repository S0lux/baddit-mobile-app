package com.example.baddit.presentation.components.SideDrawerContent

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary

@Composable
fun SideDrawerContent(
    onExploreClick: () -> Unit,
    viewModel: SideDrawerContentViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn
    if (isLoggedIn) {
        LaunchedEffect(true) {
            viewModel.getJoinCommunity()
        }
    } else {
        LaunchedEffect(true) {
            viewModel.joinedCommunities.clear()
        }
    }
    DismissibleDrawerSheet(
        modifier = Modifier
            .width(250.dp)
            .shadow(
                elevation = 2.dp,
            ),
        windowInsets = WindowInsets(left = 15.dp, right = 15.dp, top = 20.dp, bottom = 0.dp),
        drawerContainerColor = MaterialTheme.colorScheme.cardBackground,
        drawerContentColor = MaterialTheme.colorScheme.textPrimary,
    ) {
        DrawerHeader(header = "Your Communities")
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp)
                .clickable {
                    onExploreClick()
                },
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.outline_explore_24),
                contentDescription = null
            )
            Text(text = "Explore more", style = MaterialTheme.typography.bodyMedium)
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (viewModel.joinedCommunities.isEmpty()) {
            Text(
                text = if (isLoggedIn) "Consider joining some communities!" else "Login to see your communities",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.textSecondary)
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(viewModel.joinedCommunities) {
                DrawerItem(communityName = it.name, logoUrl = it.logoUrl)
            }
        }
    }
}

@Composable
private fun DrawerItem(communityName: String?, logoUrl: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 1.dp)
            .clickable { }
    ) {

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(logoUrl)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .height(22.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(
                text = "r/ $communityName",
                style = MaterialTheme.typography.bodyMedium
            )
        }

    }
}

@Composable
private fun DrawerHeader(header: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = header, style = MaterialTheme.typography.titleMedium)
    }
}
