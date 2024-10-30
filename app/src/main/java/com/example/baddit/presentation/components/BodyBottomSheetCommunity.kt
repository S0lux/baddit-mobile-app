package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.domain.model.community.Community
import com.example.baddit.domain.model.community.GetCommunityListResponseDTO

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyBottomSheet(
    communities: GetCommunityListResponseDTO,
    onBackButtonClick: () -> Unit){
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }

    val filteredCommunities = communities.filter {
        it.name.contains(text, ignoreCase = true)
    }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth()) {

                SearchBar(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .semantics { traversalIndex = -1f },
                    inputField = {
                        SearchBarDefaults.InputField(
                            query = text,
                            onQueryChange = { text = it },
                            onSearch = { expanded = false },
                            expanded = expanded,
                            onExpandedChange = { expanded = it },
                            placeholder = { Text("Search Community") },
                            leadingIcon = {
                                IconButton(onClick = { onBackButtonClick() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                                }
                            },
                            trailingIcon = {
                                IconButton(onClick = { expanded = false }) {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                }
                            },
                        )
                    },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                ) {
                    SearchListCommunity(filteredCommunities)
                }
            }
            SearchListCommunity(filteredCommunities)
        }
    }
}

@Composable
fun SearchListCommunity(
    communities: List<Community>,
) {
    Box(modifier = Modifier.padding(16.dp)) {
        LazyColumn {
            items(communities) { community ->
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
                            .height(36.dp)
                            .aspectRatio(1f),
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(community.name, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}