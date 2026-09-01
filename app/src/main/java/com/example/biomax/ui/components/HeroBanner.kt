package com.example.biomax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*

/**
 * Fluid Glassmorphic Hero Ribbon
 * Replaces boxy bento cards with a sleek, minimalist horizontal telemetry stream.
 */
@Composable
fun HeroBanner(
    user: UserAccount?,
    activeRole: UserRole,
    currentLanguage: AppLanguage,
    currentCurrency: CurrencyUnit,
    modifier: Modifier = Modifier
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f

    GlassmorphicSurface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 4.dp)
            .testTag("biomax_hero_banner"),
        shape = RoundedCornerShape(18.dp),
        borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Role Stream Header with Live Status Dot
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (activeRole == UserRole.RESTAURANT) "CIRCULAR KITCHEN STREAM" else "METHANE REACTOR CONSOLE",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.8.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Verified,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "99.8% ESG PURITY",
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Fluid Metrics Strip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (activeRole == UserRole.RESTAURANT) {
                    FluidHeroMetric(
                        icon = Icons.Default.AccountBalanceWallet,
                        label = "ESCROW",
                        value = LocalizationManager.formatPrice(user?.escrowWalletBalance ?: 6320.50, currentCurrency),
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1.1f)
                    )

                    FluidHeroMetric(
                        icon = Icons.Default.Recycling,
                        label = "DIVERTED",
                        value = "${user?.totalWasteTradedTons ?: 68.4} T",
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    FluidHeroMetric(
                        icon = Icons.Default.Bolt,
                        label = "ENERGY",
                        value = "${user?.greenEnergyGeneratedMwh ?: 51.3} MWh",
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                } else {
                    FluidHeroMetric(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "INGESTED",
                        value = "${user?.totalWasteTradedTons ?: 480.0} T",
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )

                    FluidHeroMetric(
                        icon = Icons.Default.ElectricBolt,
                        label = "POWER",
                        value = "${user?.greenEnergyGeneratedMwh ?: 360.5} MWh",
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )

                    FluidHeroMetric(
                        icon = Icons.Default.Park,
                        label = "CO₂ OFFSET",
                        value = "${user?.co2AbatedTons ?: 205.8} T",
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun FluidHeroMetric(
    icon: ImageVector,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.08f))
            .border(1.dp, accentColor.copy(alpha = 0.22f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.16f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(13.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Column {
            Text(
                text = label,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.5.sp,
                maxLines = 1
            )
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
        }
    }
}
