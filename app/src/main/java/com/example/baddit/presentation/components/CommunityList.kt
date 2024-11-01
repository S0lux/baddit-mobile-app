package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityList(paddingValues: PaddingValues, communities: GetCommunityListResponseDTO) {
    LazyColumn(contentPadding = paddingValues) {
        items(communities) { community ->
            CommunityRow(community = community)
        }
    }
}

@Composable
fun CommunityRow(community: Community) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(community.logoUrl).build(),
            contentDescription = null,
            modifier = Modifier
                .clip(CircleShape)
                .height(50.dp)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(community.name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.textPrimary)
            Text("${community.memberCount} members", color = MaterialTheme.colorScheme.textSecondary)
        }
    }
}

