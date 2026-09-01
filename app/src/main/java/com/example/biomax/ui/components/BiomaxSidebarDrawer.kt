package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*

/**
 * Modern Glassmorphic Sidebar Drawer
 * Consolidates all controls, role switching, settings, tools, theme modes,
 * and quick actions previously cluttering the topbar.
 */
@Composable
fun BiomaxSidebarDrawer(
    user: UserAccount?,
    activeRole: UserRole,
    appSettings: AppSettings,
    unreadAlertsCount: Int,
    currentLanguage: AppLanguage,
    currentCurrency: CurrencyUnit,
    onSwitchRole: (UserRole) -> Unit,
    onToggleThemeMode: () -> Unit,
    onSelectPalette: (ThemePalette) -> Unit,
    onToggleDynamicColor: (Boolean) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectCurrency: (CurrencyUnit) -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    onLogout: () -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    ModalDrawerSheet(
        drawerContainerColor = MaterialTheme.colorScheme.surface,
        drawerTonalElevation = 0.dp,
        modifier = modifier
            .width(320.dp)
            .fillMaxHeight()
            .testTag("biomax_sidebar_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Header: Brand & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BIOMAX",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Circular Bioeconomy",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onCloseDrawer,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close Menu",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // User & Organization Capsule
            GlassmorphicSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.name ?: "Marco Rossi",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = user?.organizationName ?: "Grand Bistro",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        FluidCapsuleBadge(
                            text = if (activeRole == UserRole.RESTAURANT) "KITCHEN" else "PLANT",
                            icon = if (activeRole == UserRole.RESTAURANT) Icons.Filled.Restaurant else Icons.Filled.Factory,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Escrow Wallet Balance Mini-Badge
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Escrow Wallet", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(
                            text = LocalizationManager.formatPrice(user?.escrowWalletBalance ?: 6320.50, currentCurrency),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Role Switcher Toggle
            Text(
                text = "SWITCH OPERATIONAL ROLE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Restaurant Button
                val isRest = activeRole == UserRole.RESTAURANT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isRest) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSwitchRole(UserRole.RESTAURANT) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = if (isRest) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Kitchen",
                            fontSize = 11.sp,
                            fontWeight = if (isRest) FontWeight.Bold else FontWeight.Medium,
                            color = if (isRest) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Plant Button
                val isPlant = activeRole == UserRole.BIOGAS_PLANT
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isPlant) MaterialTheme.colorScheme.primary else Color.Transparent)
                        .clickable { onSwitchRole(UserRole.BIOGAS_PLANT) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Factory,
                            contentDescription = null,
                            tint = if (isPlant) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            "Plant",
                            fontSize = 11.sp,
                            fontWeight = if (isPlant) FontWeight.Bold else FontWeight.Medium,
                            color = if (isPlant) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Tools & Utilities Section
            Text(
                text = "UTILITIES & COMPLIANCE",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            SidebarItemRow(
                icon = Icons.Default.Calculate,
                label = "Yield Estimator Calculator",
                onClick = {
                    onCloseDrawer()
                    onOpenCalculator()
                }
            )

            SidebarItemRow(
                icon = Icons.Default.Notifications,
                label = "Alerts & Notifications",
                badge = if (unreadAlertsCount > 0) "$unreadAlertsCount" else null,
                onClick = {
                    onCloseDrawer()
                    onOpenNotifications()
                }
            )

            SidebarItemRow(
                icon = Icons.Default.Shield,
                label = "Security & TOTP Inspector",
                badge = "FIPS",
                onClick = {
                    onCloseDrawer()
                    onOpenSecurity()
                }
            )

            SidebarItemRow(
                icon = Icons.Default.Tune,
                label = "Preferences & Controls",
                onClick = {
                    onCloseDrawer()
                    onOpenSettings()
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Appearance & Themes Section
            Text(
                text = "THEMING & PALETTES",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Theme Mode Toggle Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .clickable { onToggleThemeMode() }
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (appSettings.themeMode == AppThemeMode.DARK) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Theme Mode", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                }

                Text(
                    text = appSettings.themeMode.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Palette Swatches Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ThemePalette.values().forEach { palette ->
                    val isSelected = appSettings.themePalette == palette && !appSettings.dynamicColor
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(palette.primaryHex))
                            .border(
                                width = if (isSelected) 2.5.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                onToggleDynamicColor(false)
                                onSelectPalette(palette)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Currency & Language Fast Switchers
            Text(
                text = "LOCALIZATION",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Currency Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CurrencyUnit.values().forEach { cur ->
                    val isCur = currentCurrency == cur
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCur) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                            .clickable { onSelectCurrency(cur) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            cur.symbol,
                            fontSize = 12.sp,
                            fontWeight = if (isCur) FontWeight.Black else FontWeight.Medium,
                            color = if (isCur) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Action
            OutlinedButton(
                onClick = {
                    onCloseDrawer()
                    onLogout()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("sidebar_logout_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f))
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Sign Out Securely", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SidebarItemRow(
    icon: ImageVector,
    label: String,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
