package com.example.biomax.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Floating Quick Action Menu
 * Sleek glassmorphic floating speed dial replacing top bar button clutter.
 */
@Composable
fun BiomaxFloatingMenu(
    unreadAlertsCount: Int,
    onOpenCalculator: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSecurity: () -> Unit,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.testTag("biomax_floating_menu"),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Expandable Speed Dial Actions
        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(spring()),
            exit = fadeOut() + shrinkVertically(spring())
        ) {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionPill(
                    icon = Icons.Default.Calculate,
                    label = "Yield Estimator",
                    color = MaterialTheme.colorScheme.primary,
                    onClick = {
                        expanded = false
                        onOpenCalculator()
                    }
                )

                FloatingActionPill(
                    icon = Icons.Default.Notifications,
                    label = "Live Alerts",
                    badge = if (unreadAlertsCount > 0) "$unreadAlertsCount" else null,
                    color = MaterialTheme.colorScheme.tertiary,
                    onClick = {
                        expanded = false
                        onOpenNotifications()
                    }
                )

                FloatingActionPill(
                    icon = Icons.Default.Shield,
                    label = "FIPS Security",
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = {
                        expanded = false
                        onOpenSecurity()
                    }
                )

                FloatingActionPill(
                    icon = Icons.Default.InvertColors,
                    label = "Theme Switch",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    onClick = {
                        expanded = false
                        onToggleTheme()
                    }
                )
            }
        }

        // Primary Speed Dial Trigger Button
        Box(
            modifier = Modifier
                .size(46.dp)
                .shadow(8.dp, CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                )
                .clickable { expanded = !expanded },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.ElectricBolt,
                contentDescription = "Quick Tools Menu",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun FloatingActionPill(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (badge != null) {
            Spacer(modifier = Modifier.width(6.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color)
                    .padding(horizontal = 5.dp, vertical = 1.dp)
            ) {
                Text(badge, fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Black)
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(13.dp))
        }
    }
}
