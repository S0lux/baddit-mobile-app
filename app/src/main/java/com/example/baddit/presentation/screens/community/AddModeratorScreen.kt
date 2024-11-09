package com.example.baddit.presentation.screens.community

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.baddit.R
import com.example.baddit.domain.model.community.Member
import com.example.baddit.presentation.utils.EditCommunity
import com.example.baddit.presentation.utils.Profile
import com.example.baddit.presentation.viewmodel.CommunityViewModel
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddModeratorScreen(
    name: String,
    navController: NavController,
    viewModel: CommunityViewModel = hiltViewModel()
) {
    val memberList = viewModel.memberList
    val isRefreshing = viewModel.isRefreshing
    val error = viewModel.error

    LaunchedEffect(Unit) {
        viewModel.fetchMembers(name)
    }

    Column {
        TopAppBar(
            title = {
                val titleText = "Add Moderator"
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
                IconButton(onClick = { navController.navigate(EditCommunity(name)) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.scaffoldBackground)
        )

        var text by rememberSaveable { mutableStateOf("") }
        var expanded by rememberSaveable { mutableStateOf(false) }

        val filteredMember = memberList.value.filter {
            it.username.contains(text, ignoreCase = true)
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

            memberList.value.isNotEmpty() -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .semantics { isTraversalGroup = true }
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    SearchBar(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .semantics { traversalIndex = 0f },
                        inputField = {
                            SearchBarDefaults.InputField(
                                query = text,
                                onQueryChange = { text = it },
                                onSearch = { expanded = false },
                                expanded = expanded,
                                onExpandedChange = { expanded = it },
                                placeholder = { Text("Find member") },
                                leadingIcon = {
                                    IconButton(onClick = { expanded = false
                                    text = ""}) {
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
                        Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                            SearchListMember(filteredMember, viewModel, name)
                        }

                    }
                    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                        SearchListMember(filteredMember, viewModel, name)
                    }
                }
            }

            else -> {
                Text(text = "Error")
            }
        }
    }
}


@Composable
fun UserRow(member: Member, viewModel: CommunityViewModel, name: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(member.avatarUrl).build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .clip(CircleShape)
                .height(36.dp)
                .aspectRatio(1f),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(member.username, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.textPrimary)

        }
        Spacer(modifier = Modifier.weight(1f))
        if (member.communityRole == "ADMIN") {
            Text(text = "Admin", color = MaterialTheme.colorScheme.textPrimary)
        } else if (member.communityRole == "MODERATOR") {
            OutlinedButton(onClick = { viewModel.unModerateMember(name, member.username) }) {
                Text(text = "Unmoderate", color = MaterialTheme.colorScheme.textPrimary)
            }
        } else {
            OutlinedButton(onClick = { viewModel.moderateMember(name, member.username) }) {
                Text(text = "Moderate", color = MaterialTheme.colorScheme.textPrimary)
            }
        }
    }
}


@Composable
fun SearchListMember(filteredMember: List<Member>, viewModel: CommunityViewModel, name: String){
    if (filteredMember.isEmpty()){
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "User was not found.",
                color = MaterialTheme.colorScheme.textPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(16.dp)
            )
        }
    }
    else{
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics { traversalIndex = 1f },
        ) {
            items(filteredMember) { member ->
                UserRow(member, viewModel, name)
            }
        }
    }

}