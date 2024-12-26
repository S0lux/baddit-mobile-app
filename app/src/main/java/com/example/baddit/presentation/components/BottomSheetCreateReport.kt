package com.example.baddit.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.baddit.domain.model.report.ReportType
import com.example.baddit.presentation.screens.chat.ChatViewModel
import com.example.baddit.presentation.screens.report.ReportViewModel
import com.example.baddit.ui.theme.CustomTheme.mutedAppBlue
import com.example.baddit.ui.theme.CustomTheme.textPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReportBottomSheet(
    reportType: ReportType,
    targetId: String,
    viewModel: ReportViewModel,
    onDismiss: () -> Unit
) {
    var reportContent by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            var title = "Report User"
            if(reportType == ReportType.POST){
                title = "Report Post"
            }
            Text(
                title,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = reportContent,
                onValueChange = { reportContent = it },
                maxLines = 20,
                placeholder = { Text("Describe why you're reporting this ${if (reportType == ReportType.USER) "user" else "post"}", ) },
                modifier = Modifier.fillMaxWidth()
            )


                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        if(reportType == ReportType.USER){
                            viewModel.createReport(
                                content = reportContent, type = reportType, targetUserId = targetId
                            )
                        }
                        else{
                            viewModel.createReport(
                                content = reportContent, type = reportType, targetPostId = targetId
                            )
                        }
                        onDismiss()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.mutedAppBlue)
                ) {
                    Text("Submit", color = Color.White)
                }
        }
    }
}