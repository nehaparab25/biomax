package com.example.biomax.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .testTag("analytics_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp)
    ) {
        // Section: KPI Summary Grid
        item {
            Text(
                text = "OPERATIONAL & SUSTAINABILITY ANALYTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyticsMetricCard(
                        title = "Feedstock Ingested",
                        value = "${String.format("%.1f", totalTradedTons)} Tons",
                        icon = Icons.Default.Recycling,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Renewable Power Output",
                        value = "${String.format("%.1f", totalMwh)} MWh",
                        icon = Icons.Default.Bolt,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AnalyticsMetricCard(
                        title = "Landfill Methane Avoidance",
                        value = "${String.format("%.1f", totalCo2AbatedTons)} T CO₂",
                        icon = Icons.Default.Forest,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                    AnalyticsMetricCard(
                        title = "Escrow Transacted",
                        value = LocalizationManager.formatPrice(totalFinancialVolume, currentCurrency),
                        icon = Icons.Default.AccountBalanceWallet,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Section: Energy Conversion Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Anaerobic Digestion Conversion Efficiency",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Continuous biogas turbine output & methane yields by category",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FeedstockProgressRow("Used Cooking Oils & Lipids", 0.88f, "520 m³/T (High CH₄)", MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FeedstockProgressRow("Bakery Flour & Pastry Scraps", 0.65f, "240 m³/T (Rapid digest)", MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FeedstockProgressRow("Cooked Food Scraps", 0.52f, "180 m³/T (Balanced)", MaterialTheme.colorScheme.tertiary)
                    Spacer(modifier = Modifier.height(8.dp))
                    FeedstockProgressRow("Brewery Spent Grains", 0.60f, "210 m³/T (High protein)", MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Section: Partner Quality Ratings & Reviews
        item {
            Text(
                text = "VERIFIED PARTNER REVIEWS (${reviews.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 0.8.sp
            )
        }

        if (reviews.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No verified reviews recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(reviews, key = { it.id }) { review ->
                PartnerReviewCard(review = review, dateFormat = dateFormat)
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
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FeedstockProgressRow(
    category: String,
    fraction: Float,
    yieldNote: String,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
            Text(yieldNote, fontSize = 10.sp, color = color, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun PartnerReviewCard(
    review: PartnerReview,
    dateFormat: SimpleDateFormat
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${review.fromUserName} → ${review.toUserName}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dateFormat.format(Date(review.createdAt)),
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(review.overallRating) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "\"${review.comment}\"",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ScoreBadge("Purity", review.purityScore)
                ScoreBadge("Moisture Accuracy", review.moistureAccuracyScore)
                ScoreBadge("Punctuality", review.punctualityScore)
            }
        }
    }
}

@Composable
private fun ScoreBadge(label: String, score: Int) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$label: $score/5",
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}
