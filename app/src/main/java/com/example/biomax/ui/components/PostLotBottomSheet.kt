package com.example.biomax.ui.components

import androidx.compose.foundation.background
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
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.ui.theme.*

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
    var weightKg by remember { mutableStateOf(350.0) }
    var moisturePercent by remember { mutableStateOf(65.0) }
    var pricePerKg by remember { mutableStateOf(0.08) }
    var isFreePickup by remember { mutableStateOf(false) }
    var selectedGrade by remember { mutableStateOf(FreshnessGrade.GRADE_A) }
    var selectedStorage by remember { mutableStateOf(StorageContainerType.CHILLED_ORGANIC_DRUM) }
    var pickupAddress by remember { mutableStateOf("742 Evergreen Culinary District") }
    var notes by remember { mutableStateOf("") }

    // Instant Yield Preview
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
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Post Food Waste Batch",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "List kitchen waste for instant biogas facility procurement",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BioGreenDark.copy(alpha = 0.15f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = BioGreenDark,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Listing Title
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Listing Title / Batch Name") },
                placeholder = { Text("e.g. Morning Bakery Surplus & Cooked Scraps") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("post_lot_title_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Picker
            Text("Feedstock Classification", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
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
                        Text(selectedCategory.displayName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
                DropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    FeedstockCategory.values().forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.displayName, fontSize = 13.sp) },
                            onClick = {
                                selectedCategory = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Batch Weight & Moisture Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = weightKg.toInt().toString(),
                    onValueChange = { weightKg = it.toDoubleOrNull() ?: weightKg },
                    label = { Text("Batch Weight (kg)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("post_lot_weight_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = moisturePercent.toInt().toString(),
                    onValueChange = { moisturePercent = it.toDoubleOrNull() ?: moisturePercent },
                    label = { Text("Moisture (%)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("post_lot_moisture_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Freshness & Storage Type Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Grade
                Column(modifier = Modifier.weight(1f)) {
                    Text("Freshness Grade", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    var expGrade by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expGrade = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(selectedGrade.name.replace("_", " "), fontSize = 11.sp, maxLines = 1)
                        }
                        DropdownMenu(expanded = expGrade, onDismissRequest = { expGrade = false }) {
                            FreshnessGrade.values().forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g.label, fontSize = 12.sp) },
                                    onClick = {
                                        selectedGrade = g
                                        expGrade = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Storage
                Column(modifier = Modifier.weight(1f)) {
                    Text("Storage Method", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    var expStorage by remember { mutableStateOf(false) }
                    Box {
                        OutlinedButton(
                            onClick = { expStorage = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(selectedStorage.title, fontSize = 11.sp, maxLines = 1)
                        }
                        DropdownMenu(expanded = expStorage, onDismissRequest = { expStorage = false }) {
                            StorageContainerType.values().forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.title, fontSize = 12.sp) },
                                    onClick = {
                                        selectedStorage = s
                                        expStorage = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pricing Options (Free ESG pickup vs Price per kg)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isFreePickup,
                        onCheckedChange = { isFreePickup = it },
                        modifier = Modifier.testTag("free_pickup_checkbox")
                    )
                    Text("Offer for Free Zero-Waste ESG Pickup", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }

            if (!isFreePickup) {
                OutlinedTextField(
                    value = String.format("%.2f", pricePerKg),
                    onValueChange = { pricePerKg = it.toDoubleOrNull() ?: pricePerKg },
                    label = { Text("Price per kg (${currentCurrency.symbol})") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("post_lot_price_input"),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pickup Address
            OutlinedTextField(
                value = pickupAddress,
                onValueChange = { pickupAddress = it },
                label = { Text("Kitchen / Loading Dock Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Organic Quality Notes / Segregation details") },
                placeholder = { Text("e.g. 100% organic bakery waste, chilled in stainless drums.") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Instant Energy Yield Calculation Summary Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Instant Biogas Potential:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            "${String.format("%.1f", estMethaneM3)} m³ CH₄ (~${String.format("%.0f", estKwh)} kWh)",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp,
                            color = BioGreenDark
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Estimated Batch Value:", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            if (isFreePickup) "ESG Green Credit" else LocalizationManager.formatPrice(weightKg * pricePerKg, currentCurrency),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = if (isFreePickup) BioGreenPrimary else BioAmberEnergy
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Publish Button
            Button(
                onClick = {
                    onSubmitListing(
                        title,
                        selectedCategory,
                        weightKg,
                        moisturePercent,
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BioGreenPrimary)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Publish to Biogas Regional Marketplace", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
