package com.example.baddit.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.baddit.R
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileItem(icon: Painter, text: String){
    Row(modifier = Modifier.fillMaxWidth().defaultMinSize(Dp.Unspecified,40.dp).height(70.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier ,horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
            Icon(modifier = Modifier.size(30.dp), painter = icon, contentDescription = "")
            Spacer(modifier = Modifier.fillMaxWidth(0.01f))
            Text(text = text)
        }

        Icon(modifier = Modifier.size(30.dp), painter = painterResource(id = R.drawable.arrow_right_solid), contentDescription = "")
    }
}

@Preview
@Composable
fun ProfileItemPreview(){
    ProfileItem(painterResource(id = R.drawable.comment),"Test")
}
