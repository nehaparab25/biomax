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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*

/**
 * Fluid Glassmorphic Feedstock & Waste Lot Cards
 * Stripped of text overload in favor of clear iconography,
 * fluid metric capsules, and sleek glassmorphic surfaces.
 */
@Composable
fun FeedstockListingCard(
    listing: WasteListing,
    currentCurrency: CurrencyUnit,
    onProcure: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalCost = listing.weightKg * listing.pricePerKg

    GlassmorphicSurface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("feedstock_card_${listing.id}"),
        shape = RoundedCornerShape(20.dp),
        borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top Row: Category Pill & Freshness Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FluidCapsuleBadge(
                    text = listing.category.displayName,
                    icon = Icons.Default.Eco,
                    color = MaterialTheme.colorScheme.primary
                )

                val badgeColor = when (listing.freshnessGrade) {
                    FreshnessGrade.GRADE_A -> MaterialTheme.colorScheme.primary
                    FreshnessGrade.GRADE_B -> MaterialTheme.colorScheme.tertiary
                    FreshnessGrade.GRADE_C -> MaterialTheme.colorScheme.error
                }

                FluidCapsuleBadge(
                    text = "${listing.expiresHoursLeft}h • Grade ${listing.freshnessGrade.name.last()}",
                    icon = Icons.Default.Timer,
                    color = badgeColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Location Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = listing.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${listing.restaurantName} • ${listing.distanceKm} km",
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fluid Metrics Row with Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FluidMetricGlyph(
                    icon = Icons.Default.Scale,
                    value = "${listing.weightKg.toInt()}",
                    unit = "kg",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                FluidMetricGlyph(
                    icon = Icons.Default.LocalFireDepartment,
                    value = String.format("%.1f", listing.estimatedBiogasM3),
                    unit = "m³",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1.1f)
                )
                FluidMetricGlyph(
                    icon = Icons.Default.Bolt,
                    value = "${listing.estimatedKwh.toInt()}",
                    unit = "kWh",
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // IoT Sensor Telemetry Capsule
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SensorPill(Icons.Default.WaterDrop, "${listing.moisturePercent}%")
                    SensorPill(Icons.Default.Thermostat, "${listing.temperatureC}°C")
                    SensorPill(Icons.Default.Science, "pH ${listing.phLevel}")
                }

                Text(
                    text = listing.storageType.title.take(14),
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Pricing & Procure Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (listing.isFreePickup) "FREE PICKUP" else LocalizationManager.formatPrice(totalCost, currentCurrency),
                        color = if (listing.isFreePickup) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = if (listing.isFreePickup) "Community Feedstock" else "${LocalizationManager.formatPrice(listing.pricePerKg, currentCurrency)}/kg + Escrow",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = onProcure,
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("procure_lot_btn_${listing.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Filled.ShoppingCartCheckout,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Procure", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun WasteLotCard(
    listing: WasteListing,
    currentCurrency: CurrencyUnit,
    onDelete: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    GlassmorphicSurface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("kitchen_lot_${listing.id}"),
        shape = RoundedCornerShape(18.dp),
        borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FluidCapsuleBadge(
                    text = listing.category.displayName,
                    icon = Icons.Default.Restaurant,
                    color = MaterialTheme.colorScheme.primary
                )

                FluidCapsuleBadge(
                    text = if (listing.isReserved) "IN LOGISTICS" else "ACTIVE DISPATCH",
                    icon = if (listing.isReserved) Icons.Default.LocalShipping else Icons.Default.CheckCircle,
                    color = if (listing.isReserved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = listing.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FluidMetricGlyph(
                    icon = Icons.Default.Scale,
                    value = "${listing.weightKg.toInt()}",
                    unit = "kg",
                    accentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f)
                )
                FluidMetricGlyph(
                    icon = Icons.Default.Bolt,
                    value = "${listing.estimatedKwh.toInt()}",
                    unit = "kWh",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                FluidMetricGlyph(
                    icon = Icons.Default.MonetizationOn,
                    value = if (listing.isFreePickup) "FREE" else LocalizationManager.formatPrice(listing.weightKg * listing.pricePerKg, currentCurrency),
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SensorPill(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(11.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = value,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
