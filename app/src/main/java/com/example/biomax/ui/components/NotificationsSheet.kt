package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.AlertSeverity
import com.example.biomax.model.AlertType
import com.example.biomax.model.SystemAlertNotification
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    alerts: List<SystemAlertNotification>,
    onMarkRead: (String) -> Unit,
    onMarkAllRead: () -> Unit,
    onTriggerSpoilageSim: () -> Unit,
    onTriggerSurgeSim: () -> Unit,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("HH:mm - MMM dd", Locale.getDefault())

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .testTag("notifications_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "System Alerts (${alerts.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                if (alerts.any { !it.isRead }) {
                    TextButton(
                        onClick = onMarkAllRead,
                        modifier = Modifier.testTag("mark_all_read_btn")
                    ) {
                        Text("Mark all read", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Simulation Trigger Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTriggerSpoilageSim,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Spoilage Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onTriggerSurgeSim,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grid Surge Alert", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notifications", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alerts, key = { it.id }) { alert ->
                        NotificationItemRow(alert = alert, dateFormat = dateFormat, onMarkRead = { onMarkRead(alert.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationItemRow(
    alert: SystemAlertNotification,
    dateFormat: SimpleDateFormat,
    onMarkRead: () -> Unit
) {
    val severityColor = when (alert.severity) {
        AlertSeverity.HIGH -> MaterialTheme.colorScheme.error
        AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.INFO -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                1.dp,
                if (!alert.isRead) severityColor.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outlineVariant,
                RoundedCornerShape(12.dp)
            )
            .clickable { onMarkRead() }
            .padding(12.dp)
    ) {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(if (alert.isRead) Color.Transparent else severityColor)
                    .padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = alert.title,
                        fontWeight = if (!alert.isRead) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateFormat.format(Date(alert.timestamp)),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = alert.message,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
