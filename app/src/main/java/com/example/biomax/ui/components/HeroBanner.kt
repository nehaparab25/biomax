package com.example.biomax.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.ui.theme.*

@Composable
fun HeroBanner(
    user: UserAccount?,
    activeRole: UserRole,
    currentLanguage: AppLanguage,
    currentCurrency: CurrencyUnit,
    unreadAlertsCount: Int,
    onSwitchRole: (UserRole) -> Unit,
    onOpenCalculator: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenSecurity: () -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onCurrencyChange: (CurrencyUnit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hero_banner_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Image & Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(135.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.biomax_hero_banner),
                    contentDescription = "Biomax Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.35f),
                                    Color.Black.copy(alpha = 0.85f)
                                )
                            )
                        )
                )

                // Top Controls (Language, Currency, Alerts, Security)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // App Brand
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BioGreenPrimary,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = "Biomax Logo",
                                tint = Color.White,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "BIOMAX",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            letterSpacing = 1.sp,
                            color = Color.White
                        )
                    }

                    // Action Icons
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Language Dropdown / Toggle
                        LanguageSelectorBadge(
                            currentLanguage = currentLanguage,
                            onLanguageSelected = onLanguageChange
                        )

                        // Currency Selector Badge
                        CurrencySelectorBadge(
                            currentCurrency = currentCurrency,
                            onCurrencySelected = onCurrencyChange
                        )

                        // Notifications Icon with Badge
                        IconButton(
                            onClick = onOpenNotifications,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .testTag("notifications_icon_button")
                        ) {
                            BadgedBox(badge = {
                                if (unreadAlertsCount > 0) {
                                    Badge(containerColor = BioOrangeUrgent) {
                                        Text(unreadAlertsCount.toString(), color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Alerts",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Security E2E Badge Button
                        IconButton(
                            onClick = onOpenSecurity,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(BioGreenDark.copy(alpha = 0.8f))
                                .testTag("security_inspector_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "E2E Encryption",
                                tint = BioGreenLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Banner Bottom Titles
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 14.dp, bottom = 10.dp, end = 14.dp)
                ) {
                    Text(
                        text = LocalizationManager.getString("app_tagline", currentLanguage),
                        color = BioGreenLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = user?.organizationName ?: "Biomax Global Clean Grid",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            // Role Switcher & Live Quick Stats
            Column(modifier = Modifier.padding(12.dp)) {
                // Role Switcher Segment
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Biogas Plant Button
                    RoleSegmentButton(
                        title = LocalizationManager.getString("role_biogas", currentLanguage),
                        icon = Icons.Default.Factory,
                        isSelected = activeRole == UserRole.BIOGAS_PLANT,
                        onClick = { onSwitchRole(UserRole.BIOGAS_PLANT) },
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Restaurant Button
                    RoleSegmentButton(
                        title = LocalizationManager.getString("role_restaurant", currentLanguage),
                        icon = Icons.Default.Restaurant,
                        isSelected = activeRole == UserRole.RESTAURANT,
                        onClick = { onSwitchRole(UserRole.RESTAURANT) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // KPI Quick Highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiStatCard(
                        title = "Feedstock Traded",
                        value = "${user?.totalWasteTradedTons ?: 42.5} Tons",
                        icon = Icons.Default.Recycling,
                        iconTint = BioGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    KpiStatCard(
                        title = "Green Power",
                        value = "${user?.greenEnergyGeneratedMwh ?: 31.8} MWh",
                        icon = Icons.Default.ElectricBolt,
                        iconTint = BioAmberEnergy,
                        modifier = Modifier.weight(1f)
                    )
                    KpiStatCard(
                        title = "CO₂ Abated",
                        value = "${user?.co2AbatedTons ?: 18.2} T",
                        icon = Icons.Default.Forest,
                        iconTint = BioTealAccent,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interactive Simulator Launcher button
                FilledTonalButton(
                    onClick = onOpenCalculator,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("open_yield_calc_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = BioGreenDark.copy(alpha = 0.15f),
                        contentColor = BioGreenDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Interactive Biogas & Electricity Yield Simulator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun RoleSegmentButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) BioGreenPrimary else Color.Transparent,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun KpiStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LanguageSelectorBadge(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier
                .height(30.dp)
                .clickable { expanded.value = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(currentLanguage.flag, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    currentLanguage.code.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(lang.flag, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("${lang.displayName} (${lang.code.uppercase()})", fontSize = 13.sp)
                        }
                    },
                    onClick = {
                        onLanguageSelected(lang)
                        expanded.value = false
                    }
                )
            }
        }
    }
}

@Composable
fun CurrencySelectorBadge(
    currentCurrency: CurrencyUnit,
    onCurrencySelected: (CurrencyUnit) -> Unit
) {
    var expanded = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Box {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color.Black.copy(alpha = 0.5f),
            modifier = Modifier
                .height(30.dp)
                .clickable { expanded.value = true }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${currentCurrency.symbol} ${currentCurrency.code}",
                    color = BioAmberEnergy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            CurrencyUnit.values().forEach { curr ->
                DropdownMenuItem(
                    text = {
                        Text("${curr.symbol} ${curr.code}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    },
                    onClick = {
                        onCurrencySelected(curr)
                        expanded.value = false
                    }
                )
            }
        }
    }
}
