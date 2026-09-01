package com.example.biomax.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.ui.components.*
import com.example.biomax.ui.screens.*
import com.example.biomax.viewmodel.BiomaxViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiomaxApp(
    viewModel: BiomaxViewModel
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val activeRole by viewModel.activeRole.collectAsStateWithLifecycle()
    val listings by viewModel.listings.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val reviews by viewModel.reviews.collectAsStateWithLifecycle()
    val alerts by viewModel.alerts.collectAsStateWithLifecycle()
    val auditLogs by viewModel.auditLogs.collectAsStateWithLifecycle()
    val currentLanguage by viewModel.currentLanguage.collectAsStateWithLifecycle()
    val currentCurrency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()

    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableIntStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<FeedstockCategory?>(null) }
    var selectedGradeFilter by remember { mutableStateOf<FreshnessGrade?>(null) }

    // Dialog & Sheet States
    var showYieldCalcDialog by remember { mutableStateOf(false) }
    var showPostLotSheet by remember { mutableStateOf(false) }
    var showRatingDialogForOrder by remember { mutableStateOf<OrderTransaction?>(null) }
    var showSecuritySheet by remember { mutableStateOf(false) }
    var showNotificationsSheet by remember { mutableStateOf(false) }
    var showSettingsModal by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle toast messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    // --- Top-Level Authentication & RBAC Router ---
    if (authState !is AuthState.Authenticated) {
        LoginScreen(
            authState = authState,
            onAuthEvent = { viewModel.handleAuthEvent(it) }
        )
    } else {
        val unreadAlertsCount = alerts.count { !it.isRead }

        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = true,
            drawerContent = {
                BiomaxSidebarDrawer(
                    user = currentUser,
                    activeRole = activeRole,
                    appSettings = appSettings,
                    unreadAlertsCount = unreadAlertsCount,
                    currentLanguage = currentLanguage,
                    currentCurrency = currentCurrency,
                    onSwitchRole = { newRole ->
                        viewModel.switchActiveRole(newRole)
                        selectedTab = 0
                        coroutineScope.launch { drawerState.close() }
                    },
                    onToggleThemeMode = { viewModel.toggleThemeMode() },
                    onSelectPalette = { viewModel.setThemePalette(it) },
                    onToggleDynamicColor = { viewModel.setDynamicColor(it) },
                    onSelectLanguage = { viewModel.setLanguage(it) },
                    onSelectCurrency = { viewModel.setCurrency(it) },
                    onOpenCalculator = { showYieldCalcDialog = true },
                    onOpenSecurity = { showSecuritySheet = true },
                    onOpenNotifications = { showNotificationsSheet = true },
                    onOpenSettings = {
                        selectedTab = 4
                        coroutineScope.launch { drawerState.close() }
                    },
                    onLogout = { viewModel.handleAuthEvent(AuthEvent.Logout) },
                    onCloseDrawer = { coroutineScope.launch { drawerState.close() } }
                )
            }
        ) {
            Scaffold(
                topBar = {
                    BiomaxTopBar(
                        user = currentUser,
                        activeRole = activeRole,
                        unreadAlertsCount = unreadAlertsCount,
                        onOpenDrawer = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onQuickSwitchRole = {
                            val nextRole = if (activeRole == UserRole.RESTAURANT) UserRole.BIOGAS_PLANT else UserRole.RESTAURANT
                            viewModel.switchActiveRole(nextRole)
                            selectedTab = 0
                        }
                    )
                },
                snackbarHost = { SnackbarHost(snackbarHostState) },
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 4.dp,
                        modifier = Modifier.testTag("biomax_bottom_nav")
                    ) {
                        if (activeRole == UserRole.RESTAURANT) {
                            // --- Restaurant Navigation ---
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 0) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2,
                                        contentDescription = "Kitchen Lots"
                                    )
                                },
                                label = { Text("Lots", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_restaurant_lots")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    BadgedBox(badge = {
                                        val activePickups = orders.count { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }
                                        if (activePickups > 0) {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Text(activePickups.toString(), color = MaterialTheme.colorScheme.onPrimary, fontSize = 9.sp)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (selectedTab == 1) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                                            contentDescription = "Fleet Pickups"
                                        )
                                    }
                                },
                                label = { Text("Pickups", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_restaurant_pickups")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.BarChart else Icons.Outlined.BarChart,
                                        contentDescription = "Impact"
                                    )
                                },
                                label = { Text("Impact", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_restaurant_impact")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 3) Icons.Filled.Shield else Icons.Outlined.Shield,
                                        contentDescription = "Security"
                                    )
                                },
                                label = { Text("Security", fontSize = 10.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_restaurant_security")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 4) Icons.Filled.Tune else Icons.Outlined.Tune,
                                        contentDescription = "Controls"
                                    )
                                },
                                label = { Text("Controls", fontSize = 10.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_restaurant_settings")
                            )
                        } else {
                            // --- Biogas Energy Plant Navigation ---
                            NavigationBarItem(
                                selected = selectedTab == 0,
                                onClick = { selectedTab = 0 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 0) Icons.Filled.Storefront else Icons.Outlined.Storefront,
                                        contentDescription = "Marketplace"
                                    )
                                },
                                label = { Text("Market", fontSize = 10.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_biogas_marketplace")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 1,
                                onClick = { selectedTab = 1 },
                                icon = {
                                    BadgedBox(badge = {
                                        val inTransit = orders.count { it.logisticsStatus != LogisticsStatus.DELIVERED_DIGESTING }
                                        if (inTransit > 0) {
                                            Badge(containerColor = MaterialTheme.colorScheme.secondary) {
                                                Text(inTransit.toString(), color = MaterialTheme.colorScheme.onSecondary, fontSize = 9.sp)
                                            }
                                        }
                                    }) {
                                        Icon(
                                            imageVector = if (selectedTab == 1) Icons.Filled.LocalShipping else Icons.Outlined.LocalShipping,
                                            contentDescription = "Fleet Logistics"
                                        )
                                    }
                                },
                                label = { Text("Fleet", fontSize = 10.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_biogas_logistics")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 2,
                                onClick = { selectedTab = 2 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 2) Icons.Filled.ElectricBolt else Icons.Outlined.ElectricBolt,
                                        contentDescription = "Power Output"
                                    )
                                },
                                label = { Text("Power", fontSize = 10.sp, fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_biogas_power")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 3,
                                onClick = { selectedTab = 3 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 3) Icons.Filled.Shield else Icons.Outlined.Shield,
                                        contentDescription = "Security"
                                    )
                                },
                                label = { Text("Security", fontSize = 10.sp, fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_biogas_security")
                            )

                            NavigationBarItem(
                                selected = selectedTab == 4,
                                onClick = { selectedTab = 4 },
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == 4) Icons.Filled.Tune else Icons.Outlined.Tune,
                                        contentDescription = "Controls"
                                    )
                                },
                                label = { Text("Controls", fontSize = 10.sp, fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                ),
                                modifier = Modifier.testTag("nav_biogas_settings")
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding)
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Fluid Glassmorphic Hero Ribbon (shown on overview tabs)
                        if (selectedTab != 4) {
                            HeroBanner(
                                user = currentUser,
                                activeRole = activeRole,
                                currentLanguage = currentLanguage,
                                currentCurrency = currentCurrency
                            )
                        }

                        // Screen Routing
                        if (activeRole == UserRole.RESTAURANT) {
                            when (selectedTab) {
                                0 -> {
                                    RestaurantScreen(
                                        user = currentUser,
                                        listings = listings,
                                        orders = orders,
                                        currentCurrency = currentCurrency,
                                        currentLanguage = currentLanguage,
                                        onOpenPostLot = { showPostLotSheet = true }
                                    )
                                }
                                1 -> {
                                    LogisticsScreen(
                                        orders = orders,
                                        currentCurrency = currentCurrency,
                                        currentLanguage = currentLanguage,
                                        onAdvanceStep = { order ->
                                            viewModel.advanceLogisticsStep(order)
                                        },
                                        onSettleEscrow = { order ->
                                            viewModel.settleOrderEscrow(order)
                                        },
                                        onRatePartner = { order ->
                                            showRatingDialogForOrder = order
                                        }
                                    )
                                }
                                2 -> {
                                    AnalyticsDashboardScreen(
                                        user = currentUser,
                                        orders = orders,
                                        reviews = reviews,
                                        currentCurrency = currentCurrency,
                                        currentLanguage = currentLanguage
                                    )
                                }
                                3 -> {
                                    SecurityComplianceScreen(
                                        user = currentUser,
                                        auditLogs = auditLogs,
                                        onOpenMfaChallenge = {
                                            showSecuritySheet = true
                                        }
                                    )
                                }
                                4 -> {
                                    SettingsScreen(
                                        viewModel = viewModel,
                                        appSettings = appSettings,
                                        user = currentUser,
                                        activeRole = activeRole,
                                        currentLanguage = currentLanguage,
                                        currentCurrency = currentCurrency,
                                        onClose = { selectedTab = 0 }
                                    )
                                }
                            }
                        } else {
                            when (selectedTab) {
                                0 -> {
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
                                                onCompleted = {
                                                    selectedTab = 1
                                                }
                                            )
                                        }
                                    )
                                }
                                1 -> {
                                    LogisticsScreen(
                                        orders = orders,
                                        currentCurrency = currentCurrency,
                                        currentLanguage = currentLanguage,
                                        onAdvanceStep = { order ->
                                            viewModel.advanceLogisticsStep(order)
                                        },
                                        onSettleEscrow = { order ->
                                            viewModel.settleOrderEscrow(order)
                                        },
                                        onRatePartner = { order ->
                                            showRatingDialogForOrder = order
                                        }
                                    )
                                }
                                2 -> {
                                    AnalyticsDashboardScreen(
                                        user = currentUser,
                                        orders = orders,
                                        reviews = reviews,
                                        currentCurrency = currentCurrency,
                                        currentLanguage = currentLanguage
                                    )
                                }
                                3 -> {
                                    SecurityComplianceScreen(
                                        user = currentUser,
                                        auditLogs = auditLogs,
                                        onOpenMfaChallenge = {
                                            showSecuritySheet = true
                                        }
                                    )
                                }
                                4 -> {
                                    SettingsScreen(
                                        viewModel = viewModel,
                                        appSettings = appSettings,
                                        user = currentUser,
                                        activeRole = activeRole,
                                        currentLanguage = currentLanguage,
                                        currentCurrency = currentCurrency,
                                        onClose = { selectedTab = 0 }
                                    )
                                }
                            }
                        }
                    }

                    // Floating Quick Actions Speed Dial Button
                    BiomaxFloatingMenu(
                        unreadAlertsCount = unreadAlertsCount,
                        onOpenCalculator = { showYieldCalcDialog = true },
                        onOpenNotifications = { showNotificationsSheet = true },
                        onOpenSecurity = { showSecuritySheet = true },
                        onToggleTheme = { viewModel.toggleThemeMode() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 16.dp, bottom = 16.dp)
                    )
                }
            }

            // --- Modal Dialogs & Sheets ---
            if (showYieldCalcDialog) {
                YieldCalculatorDialog(
                    currentCurrency = currentCurrency,
                    onDismiss = { showYieldCalcDialog = false }
                )
            }

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
                        selectedTab = 0
                    }
                )
            }

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
                    }
                )
            }

            if (showSecuritySheet) {
                SecurityInspectorSheet(
                    auditLogs = auditLogs,
                    mfaEnabled = currentUser?.mfaEnabled ?: true,
                    onToggleMfa = { viewModel.toggleMfa() },
                    onDismiss = { showSecuritySheet = false }
                )
            }

            if (showNotificationsSheet) {
                NotificationsSheet(
                    alerts = alerts,
                    onMarkRead = { viewModel.markAlertRead(it) },
                    onMarkAllRead = { viewModel.markAllAlertsRead() },
                    onTriggerSpoilageSim = { viewModel.simulateUrgentSpoilageAlert() },
                    onTriggerSurgeSim = { viewModel.simulateGridSurgeAlert() },
                    onDismiss = { showNotificationsSheet = false }
                )
            }
        }
    }
}
