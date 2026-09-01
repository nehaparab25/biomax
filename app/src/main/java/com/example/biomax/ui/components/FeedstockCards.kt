package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.ui.theme.*

@Composable
fun FeedstockListingCard(
    listing: WasteListing,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    onProcureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCost = listing.weightKg * listing.pricePerKg

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("feedstock_card_${listing.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category tag + Distance + Freshness
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BioGreenDark.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = null,
                            tint = BioGreenDark,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = listing.category.displayName,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BioGreenDark
                        )
                    }
                }

                // Freshness & Expiry Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (listing.freshnessGrade) {
                        FreshnessGrade.GRADE_A -> BioGreenPrimary.copy(alpha = 0.15f)
                        FreshnessGrade.GRADE_B -> BioAmberEnergy.copy(alpha = 0.15f)
                        FreshnessGrade.GRADE_C -> BioOrangeUrgent.copy(alpha = 0.15f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = when (listing.freshnessGrade) {
                                FreshnessGrade.GRADE_A -> BioGreenPrimary
                                FreshnessGrade.GRADE_B -> BioAmberEnergy
                                FreshnessGrade.GRADE_C -> BioOrangeUrgent
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${listing.expiresHoursLeft}h left (${listing.freshnessGrade.name.replace("_", " ")})",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (listing.freshnessGrade) {
                                FreshnessGrade.GRADE_A -> BioGreenPrimary
                                FreshnessGrade.GRADE_B -> BioAmberEnergy
                                FreshnessGrade.GRADE_C -> BioOrangeUrgent
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title & Restaurant name
            Text(
                text = listing.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = listing.restaurantName,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "• ${String.format("%.1f", listing.distanceKm)} km away",
                    fontSize = 11.sp,
                    color = BioTealAccent,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Biogas Energy Yield Meter Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Batch Weight
                YieldMetricItem(
                    label = "Batch Weight",
                    value = LocalizationManager.formatWeight(listing.weightKg),
                    icon = Icons.Default.Scale,
                    tint = BioGreenDark
                )

                // Est Methane m3
                YieldMetricItem(
                    label = "Biogas Yield",
                    value = "${String.format("%.1f", listing.estimatedBiogasM3)} m³ CH₄",
                    icon = Icons.Default.LocalFireDepartment,
                    tint = BioAmberEnergy
                )

                // Power Generation kWh
                YieldMetricItem(
                    label = "Clean Power",
                    value = "${String.format("%.0f", listing.estimatedKwh)} kWh",
                    icon = Icons.Default.Bolt,
                    tint = BioTealAccent
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // IoT Telemetry Strip (Moisture %, Temp C, Storage container)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = BioTealAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format("%.0f", listing.moisturePercent)}% Moisture",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Thermostat,
                        contentDescription = null,
                        tint = BioGreenPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "${String.format("%.1f", listing.temperatureC)}°C",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Inventory2,
                        contentDescription = null,
                        tint = BioAmberEnergy,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = listing.storageType.title,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            if (listing.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "“${listing.notes}”",
                    fontSize = 11.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Action: Price & Escrow Lock Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (listing.isFreePickup) "FREE PICKUP" else LocalizationManager.formatPrice(totalCost, currentCurrency),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = if (listing.isFreePickup) BioGreenPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    if (!listing.isFreePickup) {
                        Text(
                            text = "${LocalizationManager.formatPrice(listing.pricePerKg, currentCurrency)} / kg",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "+ Green ESG Credit",
                            fontSize = 11.sp,
                            color = BioGreenDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onProcureClick,
                    enabled = !listing.isReserved,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BioGreenPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("procure_button_${listing.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (listing.isReserved) "Reserved" else LocalizationManager.getString("instant_buy", currentLanguage),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun YieldMetricItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
