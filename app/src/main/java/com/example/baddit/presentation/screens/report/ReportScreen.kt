import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.baddit.R
import com.example.baddit.domain.model.report.Post
import com.example.baddit.domain.model.report.ReportResponseDTO
import com.example.baddit.domain.model.report.ReportStatus
import com.example.baddit.domain.model.report.ReportType
import com.example.baddit.domain.model.report.User
import com.example.baddit.presentation.screens.report.ReportViewModel
import com.example.baddit.presentation.utils.Home
import com.example.baddit.ui.theme.CustomTheme.PrimaryContainter
import com.example.baddit.ui.theme.CustomTheme.cardBackground
import com.example.baddit.ui.theme.CustomTheme.cardForeground
import com.example.baddit.ui.theme.CustomTheme.errorRed
import com.example.baddit.ui.theme.CustomTheme.scaffoldBackground
import com.example.baddit.ui.theme.CustomTheme.textPrimary
import com.example.baddit.ui.theme.CustomTheme.textSecondary
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportManagementScreen(
    navController: NavController,
    viewModel: ReportViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedStatus = remember { mutableStateOf<ReportStatus?>(null) }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val refreshBoxState = rememberPullToRefreshState()

    LaunchedEffect(Unit) {
        viewModel.loadReports()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.scaffoldBackground)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Report Management",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.textPrimary
                )
            },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(Home) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_arrow_back_24),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.textPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.scaffoldBackground,
                titleContentColor = MaterialTheme.colorScheme.textPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.textPrimary
            )
        )

        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Type Filter
            ReportTypeFilter(
                selectedType = selectedType,
                onTypeSelected = { viewModel.setSelectedType(it.toString()) }
            )

            // Status Filter
            ReportStatusFilter(
                selectedStatus = selectedStatus.value,
                onStatusSelected = { selectedStatus.value = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadReports() },
            state = refreshBoxState,
            indicator = {
                Indicator(
                    state = refreshBoxState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier.align(Alignment.TopCenter),
                    containerColor = MaterialTheme.colorScheme.scaffoldBackground,
                    color = MaterialTheme.colorScheme.textPrimary
                )
            }
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scaffoldBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                }
                error != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.scaffoldBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = error ?: "Unknown error occurred",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                else -> {
                    val filteredReports = viewModel.getFilteredReports().filter { report ->
                        when (selectedStatus.value) {
                            null -> true
                            else -> report.status == selectedStatus.value
                        }
                    }

                    if (filteredReports.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.scaffoldBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No reports found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.textSecondary
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .padding(10.dp)
                                .background(MaterialTheme.colorScheme.scaffoldBackground)
                        ) {
                            items(filteredReports) { report ->
                                ReportCard(
                                    report = report,
                                    onResolveClick = { viewModel.resolveReport(report.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ReportStatusFilter(
    selectedStatus: ReportStatus?,
    onStatusSelected: (ReportStatus?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedStatus == null,
            onClick = { onStatusSelected(null) },
            label = {
                Text(
                    "All Status",
                    color = if (selectedStatus == null)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.textPrimary
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = if (selectedStatus == null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface
            )
        )

        ReportStatus.values().forEach { status ->
            FilterChip(
                selected = selectedStatus == status,
                onClick = { onStatusSelected(status) },
                label = {
                    Text(
                        status.name,
                        color = if (selectedStatus == status)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.textPrimary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selectedStatus == status)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun ReportTypeFilter(
    selectedType: ReportType?,
    onTypeSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedType == null,
            onClick = { onTypeSelected(null) },
            label = {
                Text(
                    "All",
                    color = if (selectedType == null)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.textPrimary
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                containerColor = if (selectedType == null)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surface
            )
        )
        ReportType.values().forEach { type ->
            FilterChip(
                selected = selectedType == type,
                onClick = { onTypeSelected(type.name) },
                label = {
                    Text(
                        type.name,
                        color = if (selectedType == type)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.textPrimary
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = if (selectedType == type)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
fun ReportCard(
    report: ReportResponseDTO,
    onResolveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.cardBackground,
            contentColor = MaterialTheme.colorScheme.textPrimary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Report Type: ${report.type}",
                    color = MaterialTheme.colorScheme.textPrimary
                )
                StatusChip(status = report.status)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Content: ${report.content}",
                color = MaterialTheme.colorScheme.textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            when (report.type) {
                ReportType.USER -> ReportedUserInfo(report.reportedUser)
                ReportType.POST -> ReportedPostInfo(report.reportedPost)
            }

            Spacer(modifier = Modifier.height(8.dp))

            ReporterInfo(report.reporter)

            Text(
                text = "Reported on: ${formatMessageTimestamp(report.createdAt)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.textPrimary.copy(alpha = 0.7f)
            )

            if (report.status == ReportStatus.PENDING) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onResolveClick,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.cardBackground
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    var buttonTextValue = "SUSPEND USER"
                    if(report.type == ReportType.POST){
                        buttonTextValue = "DELETE POST"
                    }
                    Text(
                        text = buttonTextValue,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: ReportStatus) {
    val backgroundColor = when (status) {
        ReportStatus.PENDING -> MaterialTheme.colorScheme.PrimaryContainter
        ReportStatus.RESOLVED -> MaterialTheme.colorScheme.PrimaryContainter
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status.name,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp,),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
fun ReportedUserInfo(user: User?) {
    user?.let {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "Avatar",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = user.username,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Status: ${user.status}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
@Composable
fun ReportedPostInfo(post: Post?) {
    post?.let { postDetails ->
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.cardForeground,
                contentColor = MaterialTheme.colorScheme.textPrimary
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = postDetails.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.textPrimary
                )

                when (postDetails.type) {
                    "TEXT" -> {
                        Text(
                            text = postDetails.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.textSecondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.PrimaryContainter.copy(alpha = 0.1f))
                                .padding(8.dp),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    "MEDIA" -> {
                        if (postDetails.mediaUrls.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                AsyncImage(
                                    model = postDetails.mediaUrls.first(),
                                    contentDescription = "Post media",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

                // Post Metadata
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (postDetails.deleted) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.errorRed.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.errorRed
                        ) {
                            Text(
                                text = "Deleted",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.PrimaryContainter
                    ) {
                        Text(
                            text = postDetails.type,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReporterInfo(reporter: User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reported by: ",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = reporter.username,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}

fun formatMessageTimestamp(timestamp: String): String {
    return try {
        val parsedDateTime =
            java.time.LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME) // Parse the timestamp
        val now = java.time.LocalDateTime.now(ZoneId.systemDefault()) // Current date & time

        when {
            // Check if the message is from today
            parsedDateTime.toLocalDate() == now.toLocalDate() -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
            }
            // Check if the message is from yesterday
            parsedDateTime.toLocalDate() == now.minus(1, ChronoUnit.DAYS).toLocalDate() -> {
                "Yesterday"
            }
            // For older messages, show `MMM d, yyyy` if the year differs
            parsedDateTime.year != now.year -> {
                parsedDateTime.format(
                    DateTimeFormatter.ofPattern(
                        "MMM d, yyyy",
                        Locale.getDefault()
                    )
                )
            }

            else -> {
                parsedDateTime.format(DateTimeFormatter.ofPattern("MMM d", Locale.getDefault()))
            }
        }
    } catch (e: Exception) {
        timestamp // Fallback to original timestamp if parsing fails
    }
}