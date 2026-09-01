package com.example.biomax.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.CurrencyUnit
import com.example.biomax.model.FeedstockCategory
import com.example.ui.theme.*

@Composable
fun YieldCalculatorDialog(
    currentCurrency: CurrencyUnit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(FeedstockCategory.COOKED_KITCHEN_SCRAPS) }
    var weightKg by remember { mutableStateOf(500f) }
    var moisturePercent by remember { mutableStateOf(65f) }

    // Calculated Energy Yields
    val methaneM3 = (weightKg / 1000.0) * selectedCategory.typicalMethaneYieldM3PerTon * (1.0 - ((moisturePercent - 60f) * 0.005))
    val electricalKwh = weightKg * selectedCategory.calorificKwhPerKg * (1.0 - ((moisturePercent - 60f) * 0.004))
    val homesPoweredDays = electricalKwh / 30.0 // Average home uses ~30 kWh/day
    val co2AbatedKg = weightKg * 1.18
    val estimatedEconomicValue = (weightKg * 0.08) + (electricalKwh * 0.12)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .testTag("yield_calculator_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = BioGreenDark.copy(alpha = 0.15f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = BioGreenDark,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Biogas Yield Simulator",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Feedstock to Clean Energy Conversion",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Select Feedstock Category
                Text(
                    text = "Feedstock Type",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                var expandedCategory by remember { mutableStateOf(false) }
                Box {
                    OutlinedButton(
                        onClick = { expandedCategory = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = selectedCategory.displayName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }

                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        FeedstockCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(cat.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(
                                            "Yield: ~${cat.typicalMethaneYieldM3PerTon.toInt()} m³ CH₄ / Ton",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                onClick = {
                                    selectedCategory = cat
                                    expandedCategory = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weight Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Batch Weight",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${weightKg.toInt()} kg (${String.format("%.2f", weightKg / 1000f)} Tons)",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = BioGreenDark
                    )
                }
                Slider(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    valueRange = 50f..5000f,
                    steps = 99,
                    colors = SliderDefaults.colors(
                        thumbColor = BioGreenPrimary,
                        activeTrackColor = BioGreenPrimary
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Moisture Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Moisture Content",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${moisturePercent.toInt()}%",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp,
                        color = BioTealAccent
                    )
                }
                Slider(
                    value = moisturePercent,
                    onValueChange = { moisturePercent = it },
                    valueRange = 10f..90f,
                    steps = 79,
                    colors = SliderDefaults.colors(
                        thumbColor = BioTealAccent,
                        activeTrackColor = BioTealAccent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Yield Output Results Box
                Text(
                    text = "ESTIMATED RENEWABLE OUTPUTS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ResultOutputRow(
                        label = "Biogas Methane (CH₄)",
                        value = "${String.format("%.1f", methaneM3)} m³",
                        icon = Icons.Default.LocalFireDepartment,
                        tint = BioAmberEnergy
                    )
                    ResultOutputRow(
                        label = "Clean Electricity Output",
                        value = "${String.format("%.0f", electricalKwh)} kWh (${String.format("%.3f", electricalKwh / 1000.0)} MWh)",
                        icon = Icons.Default.Bolt,
                        tint = BioGreenPrimary
                    )
                    ResultOutputRow(
                        label = "Home Power Equivalent",
                        value = "${String.format("%.1f", homesPoweredDays)} Days of 1-Home Electricity",
                        icon = Icons.Default.Home,
                        tint = BioTealAccent
                    )
                    ResultOutputRow(
                        label = "CO₂ Landfill Emissions Prevented",
                        value = "${String.format("%.0f", co2AbatedKg)} kg CO₂e",
                        icon = Icons.Default.Forest,
                        tint = BioGreenDark
                    )
                    ResultOutputRow(
                        label = "Combined Economic Value",
                        value = LocalizationManager.formatPrice(estimatedEconomicValue, currentCurrency),
                        icon = Icons.Default.AccountBalanceWallet,
                        tint = BioAmberEnergy
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BioGreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text("Apply Yield Parameters", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ResultOutputRow(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = value,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
