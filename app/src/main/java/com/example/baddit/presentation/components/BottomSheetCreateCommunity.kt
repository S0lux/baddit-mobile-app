package com.example.baddit.presentation.components

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun CreateCommunity(onBackButtonClick: () -> Unit){
    var communityName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
   Scaffold(topBar = {
   }) {
       Column(modifier = Modifier.padding(10.dp)) {

           Row(
               modifier = Modifier
                   .fillMaxWidth()
                   .padding(4.dp),
               horizontalArrangement = Arrangement.Start,
               verticalAlignment = Alignment.CenterVertically
           ) {
               IconButton(
                   onClick = { onBackButtonClick() },
                   modifier = Modifier.background(Color.Transparent)
               ) {
                   Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.textPrimary)
               }

               Spacer(modifier = Modifier.width(8.dp)) // Add space between the IconButton and Text

               Text(text = "Create Community", color = MaterialTheme.colorScheme.textPrimary)
           }

           Column(modifier = Modifier.padding(10.dp)){
               Text(text = "Community name", color = MaterialTheme.colorScheme.textPrimary)
               Spacer(modifier = Modifier.padding(5.dp))
               OutlinedTextField(
                   value = communityName,
                   onValueChange = { communityName = it },
                   placeholder = { Text("r/community_name") },
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth()
               )
               Spacer(modifier = Modifier.height(20.dp))
               Text(text = "Description", color = MaterialTheme.colorScheme.textPrimary)
               Spacer(modifier = Modifier.padding(5.dp))
               OutlinedTextField(
                   value = description,
                   onValueChange = { description = it },
                   placeholder = { Text("Description") },
                   singleLine = true,
                   modifier = Modifier.fillMaxWidth()
               )

               OutlinedButton(
                   onClick = { /* Handle create community action */ },
                   modifier = Modifier
                       .fillMaxWidth()
                       .padding(top = 20.dp)
               ) {
                   Text(text = "Create community", color = MaterialTheme.colorScheme.textPrimary)
               }
           }
       }
   }
}


