package com.example.biomax.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.ui.components.*
import com.example.biomax.ui.screens.*
import com.example.biomax.viewmodel.BiomaxViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.launch

enum class BiomaxNavigationTab(
    val titleKey: String,
    val iconFilled: androidx.compose.ui.graphics.vector.ImageVector,
    val iconOutlined: androidx.compose.ui.graphics.vector.ImageVector
) {
    MARKETPLACE("nav_marketplace", Icons.Filled.Storefront, Icons.Outlined.Storefront),
    RESTAURANT_LOTS("role_restaurant", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    LOGISTICS("nav_logistics", Icons.Filled.LocalShipping, Icons.Outlined.LocalShipping),
    ANALYTICS("nav_analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart),
    SECURITY("nav_security", Icons.Filled.Shield, Icons.Outlined.Shield)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomaxApp(
    viewModel: BiomaxViewModel
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(BiomaxNavigationTab.MARKETPLACE) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<FeedstockCategory?>(null) }
    var selectedGradeFilter by remember { mutableStateOf<FreshnessGrade?>(null) }

    // Dialog & Sheet States
    var showYieldCalcDialog by remember { mutableStateOf(false) }
    var showPostLotSheet by remember { mutableStateOf(false) }
    var showRatingDialogForOrder by remember { mutableStateOf<OrderTransaction?>(null) }
    var showMfaDialog by remember { mutableStateOf(false) }
    var showSecuritySheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }

    // MFA input state
    var mfaInputCode by remember { mutableStateOf("") }
    var mfaErrorMessage by remember { mutableStateOf<String?>(null) }
    val demoMfaCode = remember { viewModel.generateDemoMfaCode() }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val unreadAlertsCount = alerts.count { !it.isRead }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp,
                modifier = Modifier.testTag("biomax_bottom_nav")
            ) {
                // Tab 1: Marketplace
                NavigationBarItem(
                    selected = selectedTab == BiomaxNavigationTab.MARKETPLACE,
                    onClick = { selectedTab = BiomaxNavigationTab.MARKETPLACE },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == BiomaxNavigationTab.MARKETPLACE) BiomaxNavigationTab.MARKETPLACE.iconFilled else BiomaxNavigationTab.MARKETPLACE.iconOutlined,
                            contentDescription = "Marketplace"
                        )
                    },
                    label = {
                        Text(
                            text = if (activeRole == UserRole.BIOGAS_PLANT) LocalizationManager.getString("nav_marketplace", currentLanguage) else "Marketplace",
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioGreenDark,
                        selectedTextColor = BioGreenDark,
                        indicatorColor = BioGreenLight.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("nav_tab_marketplace")
                )

                // Tab 2: Restaurant Kitchen Lots
                NavigationBarItem(
                    selected = selectedTab == BiomaxNavigationTab.RESTAURANT_LOTS,
                    onClick = { selectedTab = BiomaxNavigationTab.RESTAURANT_LOTS },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == BiomaxNavigationTab.RESTAURANT_LOTS) BiomaxNavigationTab.RESTAURANT_LOTS.iconFilled else BiomaxNavigationTab.RESTAURANT_LOTS.iconOutlined,
                            contentDescription = "Kitchen Lots"
                        )
                    },
                    label = {
                        Text(
                            text = "Kitchen Lots",
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioGreenDark,
                        selectedTextColor = BioGreenDark,
                        indicatorColor = BioGreenLight.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("nav_tab_kitchen_lots")
                )

                // Tab 3: Logistics
                NavigationBarItem(
                    selected = selectedTab == BiomaxNavigationTab.LOGISTICS,
                    onClick = { selectedTab = BiomaxNavigationTab.LOGISTICS },
                    icon = {
                        BadgedBox(badge = {
                            val activeTransitCount = orders.count { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }
                            if (activeTransitCount > 0) {
                                Badge(containerColor = BioGreenPrimary) {
                                    Text(activeTransitCount.toString(), color = Color.White, fontSize = 9.sp)
                                }
                            }
                        }) {
                            Icon(
                                imageVector = if (selectedTab == BiomaxNavigationTab.LOGISTICS) BiomaxNavigationTab.LOGISTICS.iconFilled else BiomaxNavigationTab.LOGISTICS.iconOutlined,
                                contentDescription = "Logistics"
                            )
                        }
                    },
                    label = {
                        Text(
                            text = LocalizationManager.getString("nav_logistics", currentLanguage),
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioGreenDark,
                        selectedTextColor = BioGreenDark,
                        indicatorColor = BioGreenLight.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("nav_tab_logistics")
                )

                // Tab 4: Analytics
                NavigationBarItem(
                    selected = selectedTab == BiomaxNavigationTab.ANALYTICS,
                    onClick = { selectedTab = BiomaxNavigationTab.ANALYTICS },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == BiomaxNavigationTab.ANALYTICS) BiomaxNavigationTab.ANALYTICS.iconFilled else BiomaxNavigationTab.ANALYTICS.iconOutlined,
                            contentDescription = "Analytics"
                        )
                    },
                    label = {
                        Text(
                            text = LocalizationManager.getString("nav_analytics", currentLanguage),
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioGreenDark,
                        selectedTextColor = BioGreenDark,
                        indicatorColor = BioGreenLight.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("nav_tab_analytics")
                )

                // Tab 5: Security & Compliance
                NavigationBarItem(
                    selected = selectedTab == BiomaxNavigationTab.SECURITY,
                    onClick = { selectedTab = BiomaxNavigationTab.SECURITY },
                    icon = {
                        Icon(
                            imageVector = if (selectedTab == BiomaxNavigationTab.SECURITY) BiomaxNavigationTab.SECURITY.iconFilled else BiomaxNavigationTab.SECURITY.iconOutlined,
                            contentDescription = "Security"
                        )
                    },
                    label = {
                        Text(
                            text = LocalizationManager.getString("nav_security", currentLanguage),
                            fontSize = 10.sp
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BioGreenDark,
                        selectedTextColor = BioGreenDark,
                        indicatorColor = BioGreenLight.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.testTag("nav_tab_security")
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Persistent Brand Hero Banner with Role Switcher & Live KPIs
            HeroBanner(
                user = currentUser,
                activeRole = activeRole,
                currentLanguage = currentLanguage,
                currentCurrency = currentCurrency,
                unreadAlertsCount = unreadAlertsCount,
                onSwitchRole = { newRole ->
                    viewModel.switchRole(newRole)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Switched active view to ${newRole.name.replace("_", " ")}")
                    }
                },
                onOpenCalculator = { showYieldCalcDialog = true },
                onOpenNotifications = { showNotificationsSheet = true },
                onOpenSecurity = { showSecuritySheet = true },
                onLanguageChange = { viewModel.setLanguage(it) },
                onCurrencyChange = { viewModel.setCurrency(it) }
            )

            // Screen Content Routing
            when (selectedTab) {
                BiomaxNavigationTab.MARKETPLACE -> {
                    MarketplaceScreen(
                        listings = listings,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        searchQuery = searchQuery,
                        selectedCategory = selectedCategoryFilter,
                        selectedGrade = selectedGradeFilter,
                        onSearchChange = { searchQuery = it },
                        onCategorySelect = { selectedCategoryFilter = it },
                        onGradeSelect = { selectedGradeFilter = it },
                        onProcureListing = { listing ->
                            viewModel.procureWasteLot(
                                listing = listing,
                                onCompleted = { order ->
                                    selectedTab = BiomaxNavigationTab.LOGISTICS
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Procured ${listing.weightKg.toInt()} kg lot! ${LocalizationManager.formatPrice(order.totalAmount, currentCurrency)} held securely in Escrow.")
                                    }
                                }
                            )
                        }
                    )
                }
                BiomaxNavigationTab.RESTAURANT_LOTS -> {
                    RestaurantScreen(
                        user = currentUser,
                        listings = listings,
                        orders = orders,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        onOpenPostLot = { showPostLotSheet = true }
                    )
                }
                BiomaxNavigationTab.LOGISTICS -> {
                    LogisticsScreen(
                        orders = orders,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage,
                        onAdvanceStep = { order ->
                            viewModel.advanceLogisticsStep(
                                order = order,
                                onStepAdvanced = { updated ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Order ${updated.id}: Advanced to ${updated.logisticsStatus.label}")
                                    }
                                }
                            )
                        },
                        onSettleEscrow = { order ->
                            viewModel.settleOrderEscrow(
                                order = order,
                                onSettled = {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Escrow Settled: ${LocalizationManager.formatPrice(order.totalAmount, currentCurrency)} paid out securely.")
                                    }
                                }
                            )
                        },
                        onRatePartner = { order ->
                            showRatingDialogForOrder = order
                        }
                    )
                }
                BiomaxNavigationTab.ANALYTICS -> {
                    AnalyticsDashboardScreen(
                        user = currentUser,
                        orders = orders,
                        reviews = reviews,
                        currentCurrency = currentCurrency,
                        currentLanguage = currentLanguage
                    )
                }
                BiomaxNavigationTab.SECURITY -> {
                    SecurityComplianceScreen(
                        user = currentUser,
                        auditLogs = auditLogs,
                        onOpenMfaChallenge = {
                            mfaErrorMessage = null
                            mfaInputCode = ""
                            showMfaDialog = true
                        }
                    )
                }
            }
        }

        // --- Dialogs & Sheets ---

        // 1. Yield Calculator Dialog
        if (showYieldCalcDialog) {
            YieldCalculatorDialog(
                currentCurrency = currentCurrency,
                onDismiss = { showYieldCalcDialog = false }
            )
        }

        // 2. Post Waste Lot Bottom Sheet
        if (showPostLotSheet) {
            PostLotBottomSheet(
                currentCurrency = currentCurrency,
                currentLanguage = currentLanguage,
                onDismiss = { showPostLotSheet = false },
                onSubmitListing = { title, category, weightKg, moisture, pricePerKg, isFreePickup, grade, storage, pickupAddress, notes ->
                    viewModel.createWasteListing(
                        title = title,
                        category = category,
                        weightKg = weightKg,
                        moisturePercent = moisture,
                        pricePerKg = pricePerKg,
                        isFreePickup = isFreePickup,
                        freshnessGrade = grade,
                        storageType = storage,
                        pickupAddress = pickupAddress,
                        notes = notes
                    )
                    showPostLotSheet = false
                    selectedTab = BiomaxNavigationTab.MARKETPLACE
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Published waste lot successfully to Biomax marketplace!")
                    }
                }
            )
        }

        // 3. Partner Quality Rating Dialog
        if (showRatingDialogForOrder != null) {
            val order = showRatingDialogForOrder!!
            RatingDialog(
                order = order,
                onDismiss = { showRatingDialogForOrder = null },
                onSubmitRating = { overall, purity, moisture, punctuality, comment ->
                    viewModel.submitPartnerReview(
                        order = order,
                        overall = overall,
                        purity = purity,
                        moisture = moisture,
                        punctuality = punctuality,
                        comment = comment
                    )
                    showRatingDialogForOrder = null
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Quality rating submitted and logged to partner reputation ledger.")
                    }
                }
            )
        }

        // 4. MFA Challenge Modal
        if (showMfaDialog) {
            MfaDialog(
                demoCode = demoMfaCode,
                inputCode = mfaInputCode,
                errorMessage = mfaErrorMessage,
                onInputChange = {
                    mfaInputCode = it
                    mfaErrorMessage = null
                },
                onVerify = {
                    val verified = viewModel.verifyMfaToken(mfaInputCode)
                    if (verified) {
                        showMfaDialog = false
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Multi-Factor Authentication Verified! FIPS 140-3 Hardware Token Synced.")
                        }
                    } else {
                        mfaErrorMessage = "Invalid 6-digit TOTP code. Please enter the current active code."
                    }
                },
                onDismiss = { showMfaDialog = false }
            )
        }

        // 5. Security & Cryptographic Inspector Sheet
        if (showSecuritySheet) {
            SecurityInspectorSheet(
                auditLogs = auditLogs,
                mfaEnabled = currentUser?.mfaEnabled ?: true,
                onToggleMfa = {
                    viewModel.toggleMfa()
                },
                onDismiss = { showSecuritySheet = false }
            )
        }

        // 6. System Alerts Notifications Sheet
        if (showNotificationsSheet) {
            NotificationsSheet(
                alerts = alerts,
                onMarkRead = { viewModel.markAlertRead(it) },
                onMarkAllRead = { viewModel.markAllAlertsRead() },
                onTriggerSpoilageSim = {
                    viewModel.simulateUrgentSpoilageAlert()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Simulated IoT Spoilage Alert dispatched!")
                    }
                },
                onTriggerSurgeSim = {
                    viewModel.simulateGridSurgeAlert()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Simulated Biomax Regional Demand Surge Alert dispatched!")
                    }
                },
                onDismiss = { showNotificationsSheet = false }
            )
        }
    }
}
