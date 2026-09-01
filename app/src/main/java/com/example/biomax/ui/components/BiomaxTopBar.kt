package com.example.biomax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.UserAccount
import com.example.biomax.model.UserRole

/**
 * Minimalist Glassmorphic TopBar
 * Free of button clutter. Provides a clean brand anchor and quick drawer toggle.
 */
@Composable
fun BiomaxTopBar(
    user: UserAccount?,
    activeRole: UserRole,
    unreadAlertsCount: Int,
    onOpenDrawer: () -> Unit,
    onQuickSwitchRole: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val surfaceAlpha = if (isDark) 0.85f else 0.92f

    val topBarGradient = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha),
            MaterialTheme.colorScheme.surface.copy(alpha = surfaceAlpha * 0.9f)
        )
    )

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("biomax_top_bar"),
        color = Color.Transparent,
        tonalElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(topBarGradient)
                .border(
                    width = 1.dp,
                    color = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f)
                )
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Sidebar Hamburger Menu Button + Minimal Brand
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onOpenDrawer)
                ) {
                    // Menu Icon with optional unread indicator badge
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                            .testTag("sidebar_menu_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Open Sidebar Navigation Menu",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )

                        if (unreadAlertsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.tertiary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Minimal Brand Title
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "BIOMAX",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary)
                            )
                        }
                        Text(
                            text = user?.organizationName ?: "Circular Bioeconomy",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.5.sp,
                            maxLines = 1
                        )
                    }
                }

                // Right: Active Role Pill & Switcher Trigger
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                        .clickable(onClick = onQuickSwitchRole)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("topbar_role_pill")
                ) {
                    Icon(
                        imageVector = if (activeRole == UserRole.RESTAURANT) Icons.Filled.Restaurant else Icons.Filled.Factory,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = if (activeRole == UserRole.RESTAURANT) "KITCHEN" else "PLANT",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.SwapHoriz,
                        contentDescription = "Switch Operational Role",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
    }
}
