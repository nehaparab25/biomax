package com.example.biomax.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.viewmodel.BiomaxViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: BiomaxViewModel,
    appSettings: AppSettings,
    user: UserAccount?,
    activeRole: UserRole,
    currentLanguage: AppLanguage,
    currentCurrency: CurrencyUnit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var editName by remember(user) { mutableStateOf(user?.name ?: "") }
    var editOrg by remember(user) { mutableStateOf(user?.organizationName ?: "") }
    var editLocation by remember(user) { mutableStateOf(user?.location ?: "") }
    var editEmail by remember(user) { mutableStateOf(user?.email ?: "") }
    var showResetConfirmDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("settings_screen"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "SYSTEM CONTROL & SETTINGS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Customize theme, units, IoT sensors & credentials",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("close_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close Settings",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Section 1: Appearance & Material Design 3 Theming
        item {
            SettingsCategoryCard(
                title = "APPEARANCE & MATERIAL DESIGN 3",
                icon = Icons.Filled.Palette
            ) {
                // Theme Mode Selector
                Text(
                    text = "Theme Display Mode",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.values().forEach { mode ->
                        val isSelected = appSettings.themeMode == mode
                        val icon = when (mode) {
                            AppThemeMode.DARK -> Icons.Filled.DarkMode
                            AppThemeMode.LIGHT -> Icons.Filled.LightMode
                            AppThemeMode.SYSTEM -> Icons.Filled.SettingsBrightness
                        }

                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { viewModel.setThemeMode(mode) }
                                .testTag("theme_mode_${mode.name}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = mode.title,
                                    tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Color (Material You) Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dynamic Color (Material You)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Derive vibrant M3 palette from device wallpaper (Android 12+)",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = appSettings.dynamicColor,
                        onCheckedChange = { viewModel.toggleDynamicColor(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Theme Accent Palettes (when not dynamic)
                Text(
                    text = "Curated Bio-Tech Color Palettes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                val palettePairs = remember { ThemePalette.values().toList().chunked(2) }
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (pair in palettePairs) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            for (palette in pair) {
                                val isSelected = appSettings.themePalette == palette && !appSettings.dynamicColor
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.toggleDynamicColor(false)
                                            viewModel.setThemePalette(palette)
                                        }
                                        .testTag("palette_${palette.name}"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surfaceVariant
                                    ),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isSelected) Color(palette.primaryHex) else MaterialTheme.colorScheme.outline
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(Color(palette.primaryHex), Color(palette.secondaryHex))
                                                    )
                                                )
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = palette.title,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = palette.subtitle,
                                                fontSize = 9.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Compact Card View Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Compact Card Density",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fit more feedstock batches and fleet items on screen",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = appSettings.compactCardView,
                        onCheckedChange = { viewModel.toggleCompactView(it) }
                    )
                }
            }
        }

        // Section 2: Role & Organization Profile Control
        item {
            SettingsCategoryCard(
                title = "ROLE SWITCHER & FACILITY PROFILE",
                icon = Icons.Filled.AccountCircle
            ) {
                Text(
                    text = "Active RBAC Operator Persona",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Restaurant Option
                    val isRest = activeRole == UserRole.RESTAURANT
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.switchActiveRole(UserRole.RESTAURANT) }
                            .testTag("switch_role_restaurant"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isRest) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isRest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Restaurant,
                                contentDescription = null,
                                tint = if (isRest) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Kitchen Role", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Post Lots & Escrow", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Biogas Plant Option
                    val isPlant = activeRole == UserRole.BIOGAS_PLANT
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.switchActiveRole(UserRole.BIOGAS_PLANT) }
                            .testTag("switch_role_biogas"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isPlant) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isPlant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Factory,
                                contentDescription = null,
                                tint = if (isPlant) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Biogas Plant", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                Text("Procure & Digester", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Editable Fields
                Text(
                    text = "Edit Facility & Profile Metadata",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Account Holder / Operator Name") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editOrg,
                    onValueChange = { editOrg = it },
                    label = { Text("Organization / Facility Name") },
                    leadingIcon = { Icon(Icons.Default.Business, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editLocation,
                    onValueChange = { editLocation = it },
                    label = { Text("Pickup / Facility Address") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = editEmail,
                    onValueChange = { editEmail = it },
                    label = { Text("Official Contact Email") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        viewModel.updateUserProfile(editName, editOrg, editLocation, editEmail)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_profile_changes_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Profile Changes", fontWeight = FontWeight.Bold)
                }
            }
        }

        // Section 3: Currency, Units & Localization
        item {
            SettingsCategoryCard(
                title = "CURRENCY, UNITS & LOCALIZATION",
                icon = Icons.Filled.Language
            ) {
                // Currency Selector
                Text("Operating Currency", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CurrencyUnit.values().forEach { curr ->
                        val isSel = currentCurrency == curr
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setCurrency(curr) },
                            label = { Text("${curr.symbol} ${curr.code}") },
                            leadingIcon = if (isSel) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Language Selector
                Text("App Language", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AppLanguage.values().forEach { lang ->
                        val isSel = currentLanguage == lang
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setLanguage(lang) },
                            label = { Text(lang.displayName) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Weight Unit Selection
                Text("Weight Measurement Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    WeightUnit.values().forEach { unit ->
                        val isSel = appSettings.weightUnit == unit
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setWeightUnit(unit) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = unit.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Energy Unit Selection
                Text("Energy Output Unit", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    EnergyUnit.values().forEach { unit ->
                        val isSel = appSettings.energyUnit == unit
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setEnergyUnit(unit) },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
                        ) {
                            Text(
                                text = unit.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section 4: Circular Economy & Trading Rules
        item {
            SettingsCategoryCard(
                title = "CIRCULAR TRADING & ESCROW PRESETS",
                icon = Icons.Filled.CurrencyExchange
            ) {
                // Auto Escrow Release
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Automated Escrow Release",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Release funds instantly upon digital weighbridge & digester intake clearance",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = appSettings.autoEscrowRelease,
                        onCheckedChange = { viewModel.toggleAutoEscrow(it) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Default Price per kg
                Text(
                    text = "Default Organic Scraps Price: ${LocalizationManager.formatPrice(appSettings.defaultPricePerKg, currentCurrency)} / kg",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = appSettings.defaultPricePerKg.toFloat(),
                    onValueChange = { viewModel.setDefaultPricePerKg(Math.round(it * 100.0) / 100.0) },
                    valueRange = 0.0f..0.50f,
                    steps = 50
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Default Freshness Grade Preset
                Text("Default Freshness Grade Preset", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FreshnessGrade.values().forEach { grade ->
                        val isSel = appSettings.defaultFreshnessGrade == grade
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setDefaultFreshnessGrade(grade) },
                            label = { Text(grade.label.substringBefore(" -")) }
                        )
                    }
                }
            }
        }

        // Section 5: IoT Telemetry & Fleet Simulation
        item {
            SettingsCategoryCard(
                title = "IOT TELEMETRY & SMART SIMULATION",
                icon = Icons.Filled.Sensors
            ) {
                // Refresh Rate
                Text("Sensor Polling Frequency", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TelemetryRefreshRate.values().forEach { rate ->
                        val isSel = appSettings.telemetryRefreshRate == rate
                        FilterChip(
                            selected = isSel,
                            onClick = { viewModel.setTelemetryRefreshRate(rate) },
                            label = { Text(rate.title.substringBefore(" (")) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Spoilage Threshold Slider
                Text(
                    text = "Critical Spoilage Alert Temperature: ${appSettings.spoilageAlertThresholdC.toInt()}°C",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Slider(
                    value = appSettings.spoilageAlertThresholdC.toFloat(),
                    onValueChange = { viewModel.setSpoilageThreshold(it.toDouble()) },
                    valueRange = 10.0f..35.0f,
                    steps = 25
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sensor Jitter Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Simulate Live IoT Sensor Noise", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Realistic live fluctuation for chamber temperature and pH", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = appSettings.simulatedSensorJitter,
                        onCheckedChange = { viewModel.toggleSensorJitter(it) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Seed Button
                OutlinedButton(
                    onClick = { viewModel.seedSampleListings() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("seed_sample_lot_btn"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Addchart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Seed New Sample Marketplace Lot")
                }
            }
        }

        // Section 6: Security, Cryptography & Audit Ledger
        item {
            SettingsCategoryCard(
                title = "SECURITY, CRYPTOGRAPHY & AUDIT",
                icon = Icons.Filled.Security
            ) {
                // MFA Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Two-Factor Authentication (TOTP)", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Require time-based one-time passwords on login", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = user?.mfaEnabled ?: true,
                        onCheckedChange = { viewModel.toggleMfa() }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Biometric App Lock
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric Security Enclave Lock", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text("Hardware-backed fingerprint / face authentication", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = appSettings.isBiometricLockEnabled,
                        onCheckedChange = { viewModel.toggleBiometricLock(it) }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = { viewModel.clearAuditLogs() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.DeleteSweep, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Clear Tamper-Proof Audit History")
                }
            }
        }

        // Section 7: Factory Reset & Data Management
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.RestartAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SYSTEM DATA MANAGEMENT",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.error,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Reset all mock batches, orders, and ratings back to their pristine factory demonstration state.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showResetConfirmDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("reset_all_data_btn"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reset All Data to Factory Defaults", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Confirmation Dialog for Reset
    if (showResetConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showResetConfirmDialog = false },
            title = { Text("Reset Biomax Demonstration Data?") },
            text = { Text("This will reinitialize all feedstock lots, active fleet dispatches, and circular economy metrics to initial defaults.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetDatabaseToDefaults()
                        showResetConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Confirm Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsCategoryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 0.6.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}
