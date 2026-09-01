package com.example.biomax.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.ui.theme.*

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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("logistics_order_card_${order.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Order ID + Status Badge + Escrow badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = BioGreenPrimary.copy(alpha = 0.15f),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalShipping,
                                contentDescription = null,
                                tint = BioGreenPrimary,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = order.id,
                            fontWeight = FontWeight.Black,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = order.listingTitle,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Payment Escrow Status Tag
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (order.paymentStatus) {
                        PaymentStatus.SETTLED -> StatusSettled.copy(alpha = 0.15f)
                        PaymentStatus.IN_ESCROW -> StatusInEscrow.copy(alpha = 0.15f)
                        PaymentStatus.PROCESSING -> BioAmberEnergy.copy(alpha = 0.15f)
                        PaymentStatus.REFUNDED -> BioOrangeUrgent.copy(alpha = 0.15f)
                    }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = when (order.paymentStatus) {
                                PaymentStatus.SETTLED -> Icons.Default.CheckCircle
                                PaymentStatus.IN_ESCROW -> Icons.Default.Lock
                                PaymentStatus.PROCESSING -> Icons.Default.Sync
                                PaymentStatus.REFUNDED -> Icons.Default.Replay
                            },
                            contentDescription = null,
                            tint = when (order.paymentStatus) {
                                PaymentStatus.SETTLED -> StatusSettled
                                PaymentStatus.IN_ESCROW -> StatusInEscrow
                                PaymentStatus.PROCESSING -> BioAmberEnergy
                                PaymentStatus.REFUNDED -> BioOrangeUrgent
                            },
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = order.paymentStatus.label,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (order.paymentStatus) {
                                PaymentStatus.SETTLED -> StatusSettled
                                PaymentStatus.IN_ESCROW -> StatusInEscrow
                                PaymentStatus.PROCESSING -> BioAmberEnergy
                                PaymentStatus.REFUNDED -> BioOrangeUrgent
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Parties: Restaurant <-> Biogas Digester
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ORIGIN RESTAURANT",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.restaurantName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = order.pickupAddress,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }

                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = BioGreenPrimary,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(18.dp)
                )

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "DESTINATION BIOGAS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = order.biogasPlantName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = order.facilityAddress,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Logistics Step Progress Bar
            Text(
                text = "Current Fleet Phase: ${order.logisticsStatus.label}",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = BioGreenDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { order.progressPercent },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (order.logisticsStatus == LogisticsStatus.DELIVERED_DIGESTING) StatusSettled else BioGreenPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Visual Step Timeline Indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                LogisticsStatus.values().forEach { step ->
                    val isPassed = order.logisticsStatus.stepIndex >= step.stepIndex
                    val isCurrent = order.logisticsStatus == step

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = when {
                                isCurrent -> BioGreenPrimary
                                isPassed -> BioGreenDark
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.size(18.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                if (isPassed) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(11.dp)
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when (step) {
                                LogisticsStatus.SCHEDULED -> "Fleet"
                                LogisticsStatus.EN_ROUTE_PICKUP -> "Transit"
                                LogisticsStatus.LOADING_INSPECTION -> "Loaded"
                                LogisticsStatus.DIGITAL_WEIGHBRIDGE -> "Weigh"
                                LogisticsStatus.EN_ROUTE_FACILITY -> "Facility"
                                LogisticsStatus.DELIVERED_DIGESTING -> "Digested"
                            },
                            fontSize = 8.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) BioGreenPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time IoT Telemetry Stream (Temperature, Moisture, CH4 Potential, GHG Abatement)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryChip(
                    title = "Temperature",
                    value = "${String.format("%.1f", order.telemetryTempC)}°C",
                    icon = Icons.Default.Thermostat,
                    tint = if (order.telemetryTempC > 18.0) BioOrangeUrgent else BioGreenPrimary
                )
                TelemetryChip(
                    title = "Moisture Lab",
                    value = "${String.format("%.1f", order.telemetryMoisturePct)}%",
                    icon = Icons.Default.WaterDrop,
                    tint = BioTealAccent
                )
                TelemetryChip(
                    title = "Biogas Yield",
                    value = "${String.format("%.1f", order.telemetryCh4PotentialM3)} m³",
                    icon = Icons.Default.LocalFireDepartment,
                    tint = BioAmberEnergy
                )
                TelemetryChip(
                    title = "CO₂ Abated",
                    value = "${String.format("%.0f", order.co2AbatedKg)} kg",
                    icon = Icons.Default.Forest,
                    tint = BioGreenDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Driver & Vehicle Card
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = BioTealAccent.copy(alpha = 0.15f),
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = BioTealAccent,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = order.driverName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Plate: ${order.driverVehiclePlate} • ${order.driverPhone}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Financial Total
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = LocalizationManager.formatPrice(order.totalAmount, currentCurrency),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Weight: ${order.weightKg.toInt()} kg",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // E2E Encryption & Security Signature Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.05f))
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint = BioGreenPrimary,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Encrypted Tamper-Proof Hash: ${order.e2eEncryptedHash.take(24)}...",
                    fontSize = 9.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Advance Logistics, Release Escrow, Rate Quality)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (order.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING) {
                    // Advance step simulation button
                    Button(
                        onClick = onAdvanceStep,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BioGreenPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("advance_logistics_button_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.FastForward,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Advance Fleet Phase", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Direct Settle button
                    OutlinedButton(
                        onClick = onSettleEscrow,
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("settle_escrow_button_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriceCheck,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp),
                            tint = StatusSettled
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Direct Settle", fontSize = 11.sp, color = StatusSettled, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // Rating & Quality Review Button
                    Button(
                        onClick = onRatePartner,
                        enabled = !order.qualityRatingSubmitted,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (order.qualityRatingSubmitted) Color.Gray else BioAmberEnergy
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rate_partner_button_${order.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (order.qualityRatingSubmitted) "Quality Rating Submitted" else "Submit Partner Quality Review & Ratings",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryChip(
    title: String,
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
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = title,
                fontSize = 8.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
