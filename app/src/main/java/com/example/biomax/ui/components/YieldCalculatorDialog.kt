package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun YieldCalculatorDialog(
    currentCurrency: CurrencyUnit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf(FeedstockCategory.COOKED_KITCHEN_SCRAPS) }
    var weightKg by remember { mutableFloatStateOf(650f) }
    var moisturePercent by remember { mutableFloatStateOf(65f) }
    var sliderStyle by remember { mutableStateOf(SliderStyle.FLUID_GLOW) }

    val methaneM3 = (weightKg / 1000.0) * selectedCategory.typicalMethaneYieldM3PerTon * (1.0 - ((moisturePercent - 60f) * 0.005))
    val electricalKwh = weightKg * selectedCategory.calorificKwhPerKg * (1.0 - ((moisturePercent - 60f) * 0.004))
    val homesPoweredDays = electricalKwh / 30.0
    val co2AbatedKg = weightKg * 1.18
    val estimatedEconomicValue = (weightKg * 0.08) + (electricalKwh * 0.12)

    Dialog(onDismissRequest = onDismiss) {
        GlassmorphicSurface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("yield_calculator_dialog"),
            shape = RoundedCornerShape(24.dp),
            borderGlowColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Yield Estimator",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Style Switcher (Fluid vs Solid Bar)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = { sliderStyle = SliderStyle.FLUID_GLOW },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                Icons.Default.LinearScale,
                                contentDescription = "Fluid Slider Mode",
                                tint = if (sliderStyle == SliderStyle.FLUID_GLOW) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        IconButton(
                            onClick = { sliderStyle = SliderStyle.SOLID_BAR },
                            modifier = Modifier.size(26.dp)
                        ) {
                            Icon(
                                Icons.Default.HorizontalRule,
                                contentDescription = "Solid Bar Mode",
                                tint = if (sliderStyle == SliderStyle.SOLID_BAR) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive Fluid Sliders
                FluidSlider(
                    value = weightKg,
                    onValueChange = { weightKg = it },
                    valueRange = 50f..3000f,
                    label = "Organic Feedstock Volume",
                    valueDisplay = "${weightKg.toInt()}",
                    unit = "kg",
                    sliderStyle = sliderStyle,
                    accentColor = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                FluidSlider(
                    value = moisturePercent,
                    onValueChange = { moisturePercent = it },
                    valueRange = 30f..95f,
                    label = "Moisture Content",
                    valueDisplay = "${moisturePercent.toInt()}",
                    unit = "%",
                    sliderStyle = sliderStyle,
                    accentColor = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Minimalist Fluid Yield Outputs Strip
                Text(
                    text = "ESTIMATED GENERATION OUTPUT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MinimalResultRow(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Methane Potential",
                        value = "${String.format("%.1f", methaneM3)} m³ CH₄",
                        color = MaterialTheme.colorScheme.tertiary
                    )

                    MinimalResultRow(
                        icon = Icons.Default.Bolt,
                        label = "Electrical Energy",
                        value = "${electricalKwh.toInt()} kWh",
                        color = MaterialTheme.colorScheme.primary
                    )

                    MinimalResultRow(
                        icon = Icons.Default.Home,
                        label = "Home Power Days",
                        value = "${String.format("%.1f", homesPoweredDays)} Days",
                        color = MaterialTheme.colorScheme.secondary
                    )

                    MinimalResultRow(
                        icon = Icons.Default.Park,
                        label = "CO₂ Abatement",
                        value = "${co2AbatedKg.toInt()} kg",
                        color = MaterialTheme.colorScheme.primary
                    )

                    MinimalResultRow(
                        icon = Icons.Default.MonetizationOn,
                        label = "Estimated Value",
                        value = LocalizationManager.formatPrice(estimatedEconomicValue, currentCurrency),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Apply & Return", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun MinimalResultRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 11.5.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(value, fontSize = 12.sp, fontWeight = FontWeight.Black, color = color)
    }
}
