package com.example.biomax.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsDashboardScreen(
    user: UserAccount?,
    orders: List<OrderTransaction>,
    reviews: List<PartnerReview>,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    modifier: Modifier = Modifier
) {
    val totalTradedTons = orders.sumOf { it.weightKg } / 1000.0 + (user?.totalWasteTradedTons ?: 0.0)
    val totalMwh = orders.sumOf { it.estimatedEnergyMwh } + (user?.greenEnergyGeneratedMwh ?: 0.0)
    val totalCo2AbatedTons = (orders.sumOf { it.co2AbatedKg } / 1000.0) + (user?.co2AbatedTons ?: 0.0)
    val totalFinancialVolume = orders.sumOf { it.totalAmount } + 12450.0

    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("analytics_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Section: KPI Summary Grid
        item {
            Text(
                text = "OPERATIONAL & SUSTAINABILITY ANALYTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyticsMetricCard(
                        title = "Feedstock Processed",
                        value = "${String.format("%.1f", totalTradedTons)} Metric Tons",
                        icon = Icons.Default.Recycling,
                        tint = BioGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Clean Renewable Power",
                        value = "${String.format("%.1f", totalMwh)} MWh Produced",
                        icon = Icons.Default.Bolt,
                        tint = BioAmberEnergy,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyticsMetricCard(
                        title = "GHG Landfill Avoidance",
                        value = "${String.format("%.1f", totalCo2AbatedTons)} T CO₂e",
                        icon = Icons.Default.Forest,
                        tint = BioTealAccent,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Total Escrow Volume",
                        value = LocalizationManager.formatPrice(totalFinancialVolume, currentCurrency),
                        icon = Icons.Default.AccountBalanceWallet,
                        tint = StatusSettled,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Feedstock Breakdown Chart / Visualizer
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Feedstock Composition & Biogas Yield",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BioGreenDark.copy(alpha = 0.15f)
                        ) {
                            Text(
                                "Avg: 220 m³ CH₄/T",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BioGreenDark,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Feedstock distribution bars
                    FeedstockProgressBar(name = "Cooked Kitchen Scraps", pct = 0.42f, yield = "180 m³/T", color = BioGreenPrimary)
                    FeedstockProgressBar(name = "Used Fryer Grease & Lipids", pct = 0.28f, yield = "520 m³/T", color = BioAmberEnergy)
                    FeedstockProgressBar(name = "Brewery Mash & Spent Grains", pct = 0.18f, yield = "210 m³/T", color = BioTealAccent)
                    FeedstockProgressBar(name = "Bakery Flour & Produce Trim", pct = 0.12f, yield = "110 m³/T", color = Color(0xFF8B5CF6))
                }
            }
        }

        // Section: Comprehensive Transaction History
        item {
            Text(
                text = "FINANCIAL SETTLEMENT & TRANSACTION HISTORY (${orders.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(orders) { order ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = order.id,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${order.restaurantName} → ${order.biogasPlantName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = LocalizationManager.formatPrice(order.totalAmount, currentCurrency),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = if (order.paymentStatus == PaymentStatus.SETTLED) StatusSettled else StatusInEscrow
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (order.paymentStatus == PaymentStatus.SETTLED) StatusSettled.copy(alpha = 0.15f) else StatusInEscrow.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = order.paymentStatus.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (order.paymentStatus == PaymentStatus.SETTLED) StatusSettled else StatusInEscrow,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Batch: ${order.weightKg.toInt()} kg (${order.feedstockCategory.displayName})",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Gateway: ${order.paymentMethod.label}",
                            fontSize = 10.sp,
                            color = BioTealAccent
                        )
                    }
                }
            }
        }

        // Section: Quality Reviews & Rating Ledger
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "PARTNER QUALITY REVIEWS & RATINGS (${reviews.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(reviews) { review ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "From: ${review.fromUserName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "To: ${review.toUserName}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (i <= review.overallRating) BioAmberEnergy else MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("${review.overallRating}.0", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "“${review.comment}”",
                        fontSize = 11.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Purity: ${review.purityScore}/5", fontSize = 9.sp, color = BioGreenDark, fontWeight = FontWeight.Bold)
                        Text("Moisture Acc: ${review.moistureAccuracyScore}/5", fontSize = 9.sp, color = BioTealAccent, fontWeight = FontWeight.Bold)
                        Text("Punctuality: ${review.punctualityScore}/5", fontSize = 9.sp, color = BioAmberEnergy, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun AnalyticsMetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Surface(
                shape = CircleShape,
                color = tint.copy(alpha = 0.15f),
                modifier = Modifier.size(28.dp)
            ) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.padding(5.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun FeedstockProgressBar(
    name: String,
    pct: Float,
    yield: String,
    color: Color
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Text("${(pct * 100).toInt()}% ($yield)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
