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

/**
 * Fluid Glassmorphic Logistics Tracker
 * Modern streamlined tracking with minimal text and rich telemetry icons.
 */
@Composable
fun LogisticsTrackerCard(
    order: OrderTransaction,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    onAdvanceStep: () -> Unit,
    onSettleEscrow: () -> Unit,
    onRatePartner: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicSurface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("logistics_order_card_${order.id}"),
        shape = RoundedCornerShape(20.dp),
        borderGlowColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: ID + Escrow Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = order.id,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = order.listingTitle,
                            fontSize = 11.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                val escrowColor = when (order.paymentStatus) {
                    PaymentStatus.SETTLED -> MaterialTheme.colorScheme.primary
                    PaymentStatus.IN_ESCROW -> MaterialTheme.colorScheme.secondary
                    PaymentStatus.PROCESSING -> MaterialTheme.colorScheme.tertiary
                    PaymentStatus.REFUNDED -> MaterialTheme.colorScheme.error
                }

                FluidCapsuleBadge(
                    text = order.paymentStatus.label,
                    icon = when (order.paymentStatus) {
                        PaymentStatus.SETTLED -> Icons.Default.CheckCircle
                        PaymentStatus.IN_ESCROW -> Icons.Default.Lock
                        PaymentStatus.PROCESSING -> Icons.Default.Sync
                        PaymentStatus.REFUNDED -> Icons.Default.Replay
                    },
                    color = escrowColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fluid Stage Progress Bar
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = order.logisticsStatus.label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${(order.progressPercent * 100).toInt()}%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                LinearProgressIndicator(
                    progress = { order.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (order.logisticsStatus == LogisticsStatus.DELIVERED_DIGESTING) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fluid Telemetry Strip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FluidMetricGlyph(
                    icon = Icons.Default.Thermostat,
                    value = String.format("%.1f", order.telemetryTempC),
                    unit = "°C",
                    accentColor = MaterialTheme.colorScheme.primary
                )
                FluidMetricGlyph(
                    icon = Icons.Default.WaterDrop,
                    value = "${order.telemetryMoisturePct}",
                    unit = "%",
                    accentColor = MaterialTheme.colorScheme.secondary
                )
                FluidMetricGlyph(
                    icon = Icons.Default.Bolt,
                    value = String.format("%.2f", order.estimatedEnergyMwh),
                    unit = "MWh",
                    accentColor = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Driver & Payout Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${order.driverName} • ${order.driverVehiclePlate}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = LocalizationManager.formatPrice(order.totalAmount, currentCurrency),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fluid Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (order.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING) {
                    Button(
                        onClick = onAdvanceStep,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("advance_logistics_btn_${order.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Filled.FastForward, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Next Phase", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (order.paymentStatus == PaymentStatus.IN_ESCROW) {
                    Button(
                        onClick = onSettleEscrow,
                        modifier = Modifier
                            .weight(1f)
                            .height(38.dp)
                            .testTag("settle_escrow_btn_${order.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Release Escrow", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (!order.qualityRatingSubmitted) {
                    OutlinedButton(
                        onClick = onRatePartner,
                        modifier = Modifier
                            .height(38.dp)
                            .testTag("rate_partner_btn_${order.id}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.tertiary),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Rate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
