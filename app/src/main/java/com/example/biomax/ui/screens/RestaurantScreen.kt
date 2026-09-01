package com.example.biomax.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.ui.theme.*

@Composable
fun RestaurantScreen(
    user: UserAccount?,
    listings: List<WasteListing>,
    orders: List<OrderTransaction>,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    onOpenPostLot: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myRestaurantListings = listings.filter { it.restaurantId == (user?.id ?: "rest_01") }
    val myRestaurantOrders = orders.filter { it.restaurantId == (user?.id ?: "rest_01") }

    val activeAvailable = myRestaurantListings.filter { !it.isReserved }
    val inTransitOrReserved = myRestaurantListings.filter { it.isReserved }
    val totalPendingEscrow = myRestaurantOrders.filter { it.paymentStatus == PaymentStatus.IN_ESCROW }.sumOf { it.totalAmount }
    val totalSettledEarnings = myRestaurantOrders.filter { it.paymentStatus == PaymentStatus.SETTLED }.sumOf { it.totalAmount }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
            .testTag("restaurant_screen")
    ) {
        Spacer(modifier = Modifier.height(6.dp))

        // Top Restaurant Earnings & Bins Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Restaurant Food Waste Management",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = user?.organizationName ?: "Grand Bistro & Rotisserie",
                            fontSize = 12.sp,
                            color = BioGreenDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Post Lot FAB / Button
                    Button(
                        onClick = onOpenPostLot,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BioGreenPrimary),
                        modifier = Modifier.testTag("fab_post_waste_lot")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Waste Lot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricMiniBox(
                        title = "In Escrow",
                        value = LocalizationManager.formatPrice(totalPendingEscrow, currentCurrency),
                        icon = Icons.Default.Lock,
                        tint = StatusInEscrow,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        title = "Settled Earnings",
                        value = LocalizationManager.formatPrice(totalSettledEarnings, currentCurrency),
                        icon = Icons.Default.CheckCircle,
                        tint = StatusSettled,
                        modifier = Modifier.weight(1f)
                    )
                    MetricMiniBox(
                        title = "Active Bins",
                        value = "${activeAvailable.size} Lots",
                        icon = Icons.Default.Inventory2,
                        tint = BioAmberEnergy,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "YOUR ACTIVE KITCHEN WASTE LOTS (${myRestaurantListings.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (myRestaurantListings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No active waste lots listed.", fontWeight = FontWeight.Bold)
                    Text("Tap '+ Post Waste Lot' above to sell food scraps.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(myRestaurantListings, key = { it.id }) { listing ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = listing.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (listing.isReserved) BioAmberEnergy.copy(alpha = 0.2f) else BioGreenPrimary.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (listing.isReserved) "PROCURED & IN ESCROW" else "AVAILABLE ON MARKET",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (listing.isReserved) BioAmberEnergy else BioGreenDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Weight: ${listing.weightKg.toInt()} kg (${listing.category.displayName})",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = if (listing.isFreePickup) "FREE ESG PICKUP" else LocalizationManager.formatPrice(listing.weightKg * listing.pricePerKg, currentCurrency),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BioGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Energy Potential: ${String.format("%.1f", listing.estimatedBiogasM3)} m³ CH₄ (~${String.format("%.0f", listing.estimatedKwh)} kWh)",
                                    fontSize = 10.sp,
                                    color = BioTealAccent,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Storage: ${listing.storageType.title}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricMiniBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(3.dp))
                Text(title, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}
