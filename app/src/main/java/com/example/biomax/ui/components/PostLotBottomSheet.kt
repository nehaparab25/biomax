package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostLotBottomSheet(
    currentCurrency: CurrencyUnit,
    currentLanguage: AppLanguage,
    onDismiss: () -> Unit,
    onSubmitListing: (
        title: String,
        category: FeedstockCategory,
        weightKg: Double,
        moisture: Double,
        pricePerKg: Double,
        isFreePickup: Boolean,
        grade: FreshnessGrade,
        storage: StorageContainerType,
        pickupAddress: String,
        notes: String
    ) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FeedstockCategory.COOKED_KITCHEN_SCRAPS) }
    var weightKg by remember { mutableFloatStateOf(450f) }
    var moisturePercent by remember { mutableFloatStateOf(65f) }
    var pricePerKg by remember { mutableDoubleStateOf(0.08) }
    var isFreePickup by remember { mutableStateOf(false) }
    var selectedGrade by remember { mutableStateOf(FreshnessGrade.GRADE_A) }
    var selectedStorage by remember { mutableStateOf(StorageContainerType.CHILLED_ORGANIC_DRUM) }
    var pickupAddress by remember { mutableStateOf("Loading Bay 2, Commercial Culinary District") }
    var notes by remember { mutableStateOf("") }

    val estMethaneM3 = (weightKg / 1000.0) * selectedCategory.typicalMethaneYieldM3PerTon * selectedGrade.qualityMultiplier
    val estKwh = weightKg * selectedCategory.calorificKwhPerKg * selectedGrade.qualityMultiplier

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
                .testTag("post_lot_bottom_sheet")
        ) {
            // Header
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
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Post Waste Batch",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Direct digester procurement",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                FluidCapsuleBadge(text = "KITCHEN LOT", color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Estimated Biogas Yield Live Preview Strip
            GlassmorphicSurface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FluidMetricGlyph(
                        icon = Icons.Default.LocalFireDepartment,
                        value = String.format("%.1f", estMethaneM3),
                        unit = "m³ CH₄",
                        accentColor = MaterialTheme.colorScheme.tertiary
                    )
                    FluidMetricGlyph(
                        icon = Icons.Default.Bolt,
                        value = "${estKwh.toInt()}",
                        unit = "kWh",
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                    FluidMetricGlyph(
                        icon = Icons.Default.MonetizationOn,
                        value = if (isFreePickup) "FREE" else LocalizationManager.formatPrice(weightKg * pricePerKg, currentCurrency),
                        accentColor = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Batch Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Batch Description") },
                placeholder = { Text("e.g. Prepared Kitchen Trimmings") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Fluid Weight Slider
            FluidSlider(
                value = weightKg,
                onValueChange = { weightKg = it },
                valueRange = 50f..2500f,
                label = "Batch Weight",
                valueDisplay = "${weightKg.toInt()}",
                unit = "kg",
                sliderStyle = SliderStyle.FLUID_GLOW,
                accentColor = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Price / Free Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.PriceCheck,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isFreePickup) "Zero-Cost Community Pickup" else "Price: ${LocalizationManager.formatPrice(pricePerKg, currentCurrency)}/kg",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Free?", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = isFreePickup,
                        onCheckedChange = { isFreePickup = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pickup Address
            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Pickup Location / Loading Bay") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    onSubmitListing(
                        title.ifEmpty { "Culinary Organic Feedstock" },
                        selectedCategory,
                        weightKg.toDouble(),
                        moisturePercent.toDouble(),
                        pricePerKg,
                        isFreePickup,
                        selectedGrade,
                        selectedStorage,
                        pickupAddress,
                        notes
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_post_lot_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(17.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Publish to Biogas Market", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
