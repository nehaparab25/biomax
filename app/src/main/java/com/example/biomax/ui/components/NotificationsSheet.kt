package com.example.biomax.ui.components

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
import com.example.ui.theme.*
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
                    Surface(
                        shape = CircleShape,
                        color = BioOrangeUrgent.copy(alpha = 0.15f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = BioOrangeUrgent,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Automated System Alerts",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "Urgent Spoilage, Logistics & Escrow Notifications",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                TextButton(onClick = onMarkAllRead, modifier = Modifier.testTag("mark_all_read_button")) {
                    Text("Clear All", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BioGreenDark)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Simulation Triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onTriggerSpoilageSim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BioOrangeUrgent),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_spoilage_alert_button")
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Spoilage Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onTriggerSurgeSim,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = BioAmberEnergy),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("simulate_surge_alert_button")
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Grid Surge Alert", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Alert List
            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No active system alerts. Operations nominal.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 340.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(alerts) { alert ->
                        val alertColor = when (alert.severity) {
                            AlertSeverity.HIGH -> BioOrangeUrgent
                            AlertSeverity.MEDIUM -> BioAmberEnergy
                            AlertSeverity.INFO -> BioTealAccent
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (alert.isRead) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (!alert.isRead) alertColor.copy(alpha = 0.5f) else Color.Transparent),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onMarkRead(alert.id) }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = alertColor.copy(alpha = 0.2f),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = when (alert.alertType) {
                                            AlertType.URGENT_SPOILAGE -> Icons.Default.Thermostat
                                            AlertType.SURGE_DEMAND -> Icons.Default.ElectricBolt
                                            AlertType.LOGISTICS_DISPATCH -> Icons.Default.LocalShipping
                                            AlertType.ESCROW_SETTLED -> Icons.Default.CheckCircle
                                            AlertType.MFA_SECURITY -> Icons.Default.Shield
                                            AlertType.COMPLIANCE_ALERT -> Icons.Default.Verified
                                        },
                                        contentDescription = null,
                                        tint = alertColor,
                                        modifier = Modifier.padding(5.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = alert.title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = dateFormat.format(Date(alert.timestamp)),
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = alert.message,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
