package com.example.biomax.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.ui.components.FluidCapsuleBadge
import com.example.biomax.ui.components.FluidMetricGlyph
import com.example.biomax.ui.components.GlassmorphicSurface

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
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 14.dp)
            .testTag("restaurant_screen")
    ) {
        Spacer(modifier = Modifier.height(4.dp))

        // Fluid Inventory Control Bar
        GlassmorphicSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
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
                            text = user?.organizationName ?: "Commercial Kitchen",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Organic Waste Dispatch Console",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = onOpenPostLot,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.testTag("fab_post_waste_lot")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Post Lot", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fluid Summary Metric Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FluidMetricGlyph(
                        icon = Icons.Default.Lock,
                        value = LocalizationManager.formatPrice(totalPendingEscrow, currentCurrency),
                        accentColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    FluidMetricGlyph(
                        icon = Icons.Default.AccountBalanceWallet,
                        value = LocalizationManager.formatPrice(totalSettledEarnings, currentCurrency),
                        accentColor = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    FluidMetricGlyph(
                        icon = Icons.Default.CheckCircle,
                        value = "${activeAvailable.size} Live",
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(0.9f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Kitchen Postings Stream
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 2.dp, bottom = 96.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "POSTED LOTS (${myRestaurantListings.size})",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "${inTransitOrReserved.size} in transit",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            if (myRestaurantListings.isEmpty()) {
                item {
                    GlassmorphicSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Outlined.Inventory2,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No waste scrap batches listed", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Tap 'Post Lot' above to list kitchen scraps.", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                items(myRestaurantListings, key = { it.id }) { item ->
                    RestaurantListingRowCard(
                        listing = item,
                        currentCurrency = currentCurrency
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantListingRowCard(
    listing: WasteListing,
    currentCurrency: CurrencyUnit
) {
    GlassmorphicSurface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("restaurant_lot_row_${listing.id}"),
        shape = RoundedCornerShape(16.dp),
        borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.22f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (listing.isReserved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = listing.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                FluidCapsuleBadge(
                    text = if (listing.isReserved) "EN ROUTE" else "AVAILABLE",
                    color = if (listing.isReserved) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                )
            }

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
                    icon = Icons.Default.LocalFireDepartment,
                    value = String.format("%.1f", listing.estimatedBiogasM3),
                    unit = "m³",
                    accentColor = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.weight(1f)
                )
                FluidMetricGlyph(
                    icon = Icons.Default.MonetizationOn,
                    value = if (listing.isFreePickup) "FREE" else LocalizationManager.formatPrice(listing.weightKg * listing.pricePerKg, currentCurrency),
                    accentColor = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.weight(1.1f)
                )
            }
        }
    }
}
