package com.example.baddit.presentation.screens.notifications

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.baddit.R
import com.example.baddit.presentation.components.BaseTopNavigationBar
import com.example.baddit.presentation.screens.profile.bottomBorder
import com.example.baddit.presentation.utils.Login
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationScreenViewModel = hiltViewModel(),
    navController: NavController
) {
    if (!viewModel.isUserLoggedIn) navController.navigate(Login)

    val pullState = rememberPullToRefreshState()
    val lazyListState = rememberLazyListState()
    val isRefreshing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BaseTopNavigationBar(
            title = "Notification",
            leftIcon = R.drawable.baseline_arrow_back_24,
            onLeftIconClick = { navController.popBackStack() },
            rightIcons = listOf(
                R.drawable.check_mark to { viewModel.viewModelScope.launch {
                    viewModel.markAllAsRead()
                } }
            )
        )

        PullToRefreshBox (
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.cardBackground),
            state = pullState,
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.onRefresh() },
            indicator = {
                Indicator(
                    state = pullState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.background,
                    color = MaterialTheme.colorScheme.textPrimary)
            }
        ) {
            LazyColumn(
                state = lazyListState,
            ) {
                items(viewModel.notifications) {
                    NotificationItem(
                        title = it.payload.title,
                        message = it.payload.body,
                        isRead = it.isRead,
                        timestamp = formatISOString(it.createdAt),
                        onItemClick = { handleOnNotificationClick(it.type, it.payload.typeId) }
                    )
                    if (it.id != viewModel.notifications.lastOrNull()?.id) {
                        HorizontalDivider(
                            modifier = Modifier
                                .height(1.dp)
                                .fillMaxWidth(),
                            color = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

fun formatISOString(isoString: String): String {
    val instant = Instant.parse(isoString)
    val localDate = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${localDate.date.dayOfMonth} ${localDate.date.month.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)}, ${localDate.date.year} at ${localDate.hour}:${localDate.minute}"
}

fun handleOnNotificationClick(notificationType: String, typeId: String?) {

}

@Composable
fun NotificationItem(
    title: String,
    message: String,
    timestamp: String,
    onItemClick: () -> Unit = {},
    isRead: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isRead) Color.White else Color.LightGray.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onItemClick)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isRead) FontWeight.Normal else FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = timestamp,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        if (!isRead) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(Color.Red, shape = CircleShape)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun NotificationItemPreview() {
    NotificationItem(
        title = "New Message",
        message = "You have a new message from John Doe",
        timestamp = "2 hours ago",
        isRead = false
    )
}

@Preview(showBackground = true)
@Composable
private fun ReadNotificationItemPreview() {
    NotificationItem(
        title = "Order Shipped",
        message = "Your order #12345 has been shipped and is on its way",
        timestamp = "Yesterday",
        isRead = true
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 120)
@Composable
private fun LongNotificationItemPreview() {
    NotificationItem(
        title = "Important System Update",
        message = "A critical system update is available that addresses multiple security vulnerabilities and improves overall system performance. Please update as soon as possible.",
        timestamp = "3 days ago",
        isRead = false
    )
}