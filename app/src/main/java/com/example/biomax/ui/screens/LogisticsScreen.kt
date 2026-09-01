package com.example.biomax.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.model.*
import com.example.biomax.ui.components.LogisticsTrackerCard
import com.example.ui.theme.BioGreenDark

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
                label = { Text("All Fleet Routes (${orders.size})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BioGreenDark, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = selectedFilter == "ACTIVE",
                onClick = { selectedFilter = "ACTIVE" },
                label = { Text("Active In-Transit (${orders.count { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BioGreenDark, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = selectedFilter == "WEIGHBRIDGE",
                onClick = { selectedFilter = "WEIGHBRIDGE" },
                label = { Text("Weighbridge & Lab", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BioGreenDark, selectedLabelColor = Color.White)
            )
            FilterChip(
                selected = selectedFilter == "COMPLETED",
                onClick = { selectedFilter = "COMPLETED" },
                label = { Text("Digester Ingested (${orders.count { it.logisticsStatus == LogisticsStatus.DELIVERED_DIGESTING }})", fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BioGreenDark, selectedLabelColor = Color.White)
            )
        }

        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("No logistics transactions in this view", fontWeight = FontWeight.Bold)
                    Text("Procure a waste lot from the Marketplace to initiate dispatch.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
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
