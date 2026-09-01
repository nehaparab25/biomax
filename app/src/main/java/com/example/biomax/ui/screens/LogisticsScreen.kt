package com.example.biomax.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.*
import com.example.biomax.ui.components.GlassmorphicSurface
import com.example.biomax.ui.components.LogisticsTrackerCard

@Composable
fun LogisticsScreen(
    orders: List<OrderTransaction>,
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    onAdvanceStep: (OrderTransaction) -> Unit,
    onSettleEscrow: (OrderTransaction) -> Unit,
    onRatePartner: (OrderTransaction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredOrders = remember(orders, selectedFilter) {
        when (selectedFilter) {
            "ACTIVE" -> orders.filter { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }
            "WEIGHBRIDGE" -> orders.filter { it.logisticsStatus == LogisticsStatus.DIGITAL_WEIGHBRIDGE || it.logisticsStatus == LogisticsStatus.LOADING_INSPECTION }
            "COMPLETED" -> orders.filter { it.logisticsStatus == LogisticsStatus.DELIVERED_DIGESTING }
            else -> orders
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("logistics_screen")
    ) {
        // Status Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            FilterChip(
                selected = selectedFilter == "ALL",
                onClick = { selectedFilter = "ALL" },
                label = { Text("All Routes (${orders.size})", fontSize = 10.5.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == "ACTIVE",
                onClick = { selectedFilter = "ACTIVE" },
                label = { Text("In-Transit (${orders.count { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }})", fontSize = 10.5.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == "WEIGHBRIDGE",
                onClick = { selectedFilter = "WEIGHBRIDGE" },
                label = { Text("Weighbridge & Lab", fontSize = 10.5.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
            FilterChip(
                selected = selectedFilter == "COMPLETED",
                onClick = { selectedFilter = "COMPLETED" },
                label = { Text("Digested", fontSize = 10.5.sp) },
                shape = RoundedCornerShape(20.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                GlassmorphicSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No logistics transits in this phase", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Procure a waste lot to initiate tracking.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    LogisticsTrackerCard(
                        order = order,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        onAdvanceStep = { onAdvanceStep(order) },
                        onSettleEscrow = { onSettleEscrow(order) },
                        onRatePartner = { onRatePartner(order) }
                    )
                }
            }
        }
    }
}
