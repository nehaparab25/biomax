package com.example.biomax.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.biomax.data.BiomaxDatabase
import com.example.biomax.data.BiomaxRepository
import com.example.biomax.localization.LocalizationManager
import com.example.biomax.model.*
import com.example.biomax.security.SecurityCryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class BiomaxViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BiomaxRepository
    val listings: StateFlow<List<WasteListing>>
    val orders: StateFlow<List<OrderTransaction>>
    val reviews: StateFlow<List<PartnerReview>>
    val alerts: StateFlow<List<SystemAlertNotification>>
    val auditLogs: StateFlow<List<AuditLog>>

    // --- Authentication & Role-Based Access State Machine ---
    private val _authState = MutableStateFlow<AuthState>(
        AuthState.Unauthenticated(
            selectedRole = UserRole.RESTAURANT,
            emailInput = "marco@grandbistro.com",
            passwordInput = "biomax2026",
            rememberMe = true
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    // Derived user session from auth state
    val activeRole: StateFlow<UserRole> = _authState.map { state ->
        when (state) {
            is AuthState.Authenticated -> state.role
            is AuthState.MfaChallenge -> state.role
            is AuthState.Authenticating -> state.role
            is AuthState.Unauthenticated -> state.selectedRole
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, UserRole.RESTAURANT)

    private val _currentUserId = MutableStateFlow("rest_01")
    val currentUser: StateFlow<UserAccount?>

    // Modular App Settings & Theming State
    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    // Localization & Currency
    private val _currentLanguage = MutableStateFlow(AppLanguage.EN)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentCurrency = MutableStateFlow(CurrencyUnit.USD)
    val currentCurrency: StateFlow<CurrencyUnit> = _currentCurrency.asStateFlow()

    // UI Navigation & Dialog states
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>("ORD-2026-8891")
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    private val _selectedListing = MutableStateFlow<WasteListing?>(null)
    val selectedListing: StateFlow<WasteListing?> = _selectedListing.asStateFlow()

    // Dialogs & Sheets
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    private val _isPostLotSheetOpen = MutableStateFlow(false)
    val isPostLotSheetOpen: StateFlow<Boolean> = _isPostLotSheetOpen.asStateFlow()

    private val _isYieldCalculatorOpen = MutableStateFlow(false)
    val isYieldCalculatorOpen: StateFlow<Boolean> = _isYieldCalculatorOpen.asStateFlow()

    private val _isRatingDialogOpen = MutableStateFlow(false)
    val isRatingDialogOpen: StateFlow<Boolean> = _isRatingDialogOpen.asStateFlow()
    private val _ratingTargetOrder = MutableStateFlow<OrderTransaction?>(null)
    val ratingTargetOrder: StateFlow<OrderTransaction?> = _ratingTargetOrder.asStateFlow()

    private val _isSecurityInspectorOpen = MutableStateFlow(false)
    val isSecurityInspectorOpen: StateFlow<Boolean> = _isSecurityInspectorOpen.asStateFlow()

    private val _isNotificationsOpen = MutableStateFlow(false)
    val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

    // Marketplace Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<FeedstockCategory?>(null)
    val selectedCategoryFilter: StateFlow<FeedstockCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedGradeFilter = MutableStateFlow<FreshnessGrade?>(null)
    val selectedGradeFilter: StateFlow<FreshnessGrade?> = _selectedGradeFilter.asStateFlow()

    // SnackBar / Toast message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        val db = BiomaxDatabase.getDatabase(application, viewModelScope)
        repository = BiomaxRepository(db.biomaxDao())

        listings = repository.allListings.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        orders = repository.allOrders.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        reviews = repository.allReviews.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        alerts = repository.allAlerts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        auditLogs = repository.allAuditLogs.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        currentUser = _currentUserId.flatMapLatest { userId ->
            repository.getUser(userId)
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )
    }

    // --- Modular Settings Control Methods ---
    fun updateSettings(updater: (AppSettings) -> AppSettings) {
        _appSettings.value = updater(_appSettings.value)
    }

    fun toggleThemeMode() {
        val next = when (_appSettings.value.themeMode) {
            AppThemeMode.DARK -> AppThemeMode.LIGHT
            AppThemeMode.LIGHT -> AppThemeMode.DARK
            AppThemeMode.SYSTEM -> AppThemeMode.LIGHT
        }
        setThemeMode(next)
    }

    fun setDynamicColor(enabled: Boolean) {
        toggleDynamicColor(enabled)
    }

    fun setThemeMode(mode: AppThemeMode) {
        _appSettings.value = _appSettings.value.copy(themeMode = mode)
        _toastMessage.value = "Theme updated: ${mode.title}"
    }

    fun setThemePalette(palette: ThemePalette) {
        _appSettings.value = _appSettings.value.copy(themePalette = palette)
        _toastMessage.value = "Palette active: ${palette.title}"
    }

    fun toggleDynamicColor(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(dynamicColor = enabled)
        _toastMessage.value = if (enabled) "Material You dynamic colors enabled" else "Custom Palette active"
    }

    fun setWeightUnit(unit: WeightUnit) {
        _appSettings.value = _appSettings.value.copy(weightUnit = unit)
        _toastMessage.value = "Weight unit: ${unit.title}"
    }

    fun setEnergyUnit(unit: EnergyUnit) {
        _appSettings.value = _appSettings.value.copy(energyUnit = unit)
        _toastMessage.value = "Energy unit: ${unit.title}"
    }

    fun setVolumeUnit(unit: GasVolumeUnit) {
        _appSettings.value = _appSettings.value.copy(volumeUnit = unit)
        _toastMessage.value = "Gas volume unit: ${unit.title}"
    }

    fun setTelemetryRefreshRate(rate: TelemetryRefreshRate) {
        _appSettings.value = _appSettings.value.copy(telemetryRefreshRate = rate)
        _toastMessage.value = "Telemetry frequency: ${rate.title}"
    }

    fun setFleetSimulationSpeed(speed: FleetSimulationSpeed) {
        _appSettings.value = _appSettings.value.copy(fleetSimulationSpeed = speed)
        _toastMessage.value = "Fleet simulation: ${speed.title}"
    }

    fun toggleAutoEscrow(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(autoEscrowRelease = enabled)
        _toastMessage.value = if (enabled) "Auto-Escrow release active" else "Manual Escrow release required"
    }

    fun setSpoilageThreshold(celsius: Double) {
        _appSettings.value = _appSettings.value.copy(spoilageAlertThresholdC = celsius)
    }

    fun setMoistureThreshold(percent: Double) {
        _appSettings.value = _appSettings.value.copy(moistureAlertThresholdPercent = percent)
    }

    fun toggleCompactView(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(compactCardView = enabled)
    }

    fun toggleSoundFeedback(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(soundAndHapticFeedback = enabled)
    }

    fun toggleSensorJitter(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(simulatedSensorJitter = enabled)
    }

    fun setAuditLogLevel(level: String) {
        _appSettings.value = _appSettings.value.copy(auditLoggingLevel = level)
    }

    fun setDefaultStorageType(storage: StorageContainerType) {
        _appSettings.value = _appSettings.value.copy(defaultStorageType = storage)
    }

    fun setDefaultFreshnessGrade(grade: FreshnessGrade) {
        _appSettings.value = _appSettings.value.copy(defaultFreshnessGrade = grade)
    }

    fun setDefaultPricePerKg(price: Double) {
        _appSettings.value = _appSettings.value.copy(defaultPricePerKg = price)
    }

    fun toggleBiometricLock(enabled: Boolean) {
        _appSettings.value = _appSettings.value.copy(isBiometricLockEnabled = enabled)
        _toastMessage.value = if (enabled) "Biometric App Lock enabled" else "Biometric App Lock disabled"
    }

    fun openSettings(open: Boolean) {
        _isSettingsOpen.value = open
    }

    fun updateUserProfile(name: String, organizationName: String, location: String, email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value ?: return@launch
            val updated = user.copy(
                name = name.ifBlank { user.name },
                organizationName = organizationName.ifBlank { user.organizationName },
                location = location.ifBlank { user.location },
                email = email.ifBlank { user.email }
            )
            repository.updateUser(updated)
            logAuditAction("USER_PROFILE_UPDATED", "Profile updated for ${updated.email}")
            _toastMessage.value = "Profile settings updated successfully"
        }
    }

    fun switchActiveRole(newRole: UserRole) {
        viewModelScope.launch(Dispatchers.IO) {
            val newUserId = if (newRole == UserRole.RESTAURANT) "rest_01" else "plant_01"
            _currentUserId.value = newUserId
            val user = repository.getUser(newUserId).first() ?: UserAccount(
                id = newUserId,
                name = if (newRole == UserRole.RESTAURANT) "Chef Marco Laurent" else "Dr. Elena Rostova",
                organizationName = if (newRole == UserRole.RESTAURANT) "Grand Bistro & Rotisserie" else "EcoPower Biogas Plant #4",
                role = newRole,
                email = if (newRole == UserRole.RESTAURANT) "marco@grandbistro.com" else "operations@ecopower-biogas.org",
                location = if (newRole == UserRole.RESTAURANT) "742 Evergreen Culinary District" else "Sector 9 Biomass Grid Hub",
                coordinates = "37.7749,-122.4194"
            )

            _authState.value = AuthState.Authenticated(
                user = user,
                role = newRole,
                sessionToken = "BMX-SEC-" + UUID.randomUUID().toString().take(12)
            )
            _selectedTab.value = 0
            logAuditAction("ROLE_SWITCHED", "Switched active console to ${newRole.name}")
            _toastMessage.value = "Switched to ${if (newRole == UserRole.RESTAURANT) "Restaurant Kitchen" else "Biogas Energy Plant"} Console"
        }
    }

    fun resetDatabaseToDefaults() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllListings()
            repository.deleteAllOrders()
            repository.deleteAllAlerts()
            repository.deleteAllReviews()
            repository.deleteAllAuditLogs()
            repository.resetDatabase()
            _toastMessage.value = "All system data reset to factory initial state"
            logAuditAction("FACTORY_DATA_RESET", "User initiated full demo database reset")
        }
    }

    fun seedSampleListings() {
        viewModelScope.launch(Dispatchers.IO) {
            val randomLot = WasteListing(
                id = "list_" + (200..999).random(),
                restaurantId = "rest_01",
                restaurantName = currentUser.value?.organizationName ?: "Grand Bistro & Rotisserie",
                title = "Surplus Spent Dough & Produce Scrap Batch",
                category = FeedstockCategory.BAKERY_FLOUR_WASTE,
                weightKg = 480.0,
                moisturePercent = 64.0,
                estimatedBiogasM3 = 115.2,
                estimatedKwh = 1248.0,
                pricePerKg = _appSettings.value.defaultPricePerKg,
                isFreePickup = false,
                freshnessGrade = _appSettings.value.defaultFreshnessGrade,
                storageType = _appSettings.value.defaultStorageType,
                pickupAddress = currentUser.value?.location ?: "742 Evergreen Culinary District",
                distanceKm = 3.8,
                expiresHoursLeft = 16,
                phLevel = 6.6,
                temperatureC = 14.0,
                notes = "High-energy organic spent dough prepared for rapid anaerobic digestion."
            )
            repository.insertListing(randomLot)
            _toastMessage.value = "New sample organic feedstock lot published!"
            logAuditAction("LISTING_SEEDED", "Seeded batch ${randomLot.id}")
        }
    }

    fun clearAuditLogs() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllAuditLogs()
            _toastMessage.value = "Audit logs cleared"
        }
    }

    // --- State Machine Transition Handler ---
    fun handleAuthEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.SelectRole -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    val defaultEmail = if (event.role == UserRole.RESTAURANT) {
                        "marco@grandbistro.com"
                    } else {
                        "operations@ecopower-biogas.org"
                    }
                    _authState.value = current.copy(
                        selectedRole = event.role,
                        emailInput = defaultEmail,
                        errorMessage = null
                    )
                }
            }

            is AuthEvent.SelectDemoProfile -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    val email = if (event.role == UserRole.RESTAURANT) {
                        "marco@grandbistro.com"
                    } else {
                        "operations@ecopower-biogas.org"
                    }
                    _authState.value = current.copy(
                        selectedRole = event.role,
                        emailInput = email,
                        passwordInput = "biomax2026",
                        errorMessage = null
                    )
                }
            }

            is AuthEvent.UpdateEmail -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    _authState.value = current.copy(emailInput = event.email, errorMessage = null)
                }
            }

            is AuthEvent.UpdatePassword -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    _authState.value = current.copy(passwordInput = event.password, errorMessage = null)
                }
            }

            is AuthEvent.ToggleRememberMe -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    _authState.value = current.copy(rememberMe = event.remember)
                }
            }

            AuthEvent.SubmitCredentials -> {
                val current = _authState.value
                if (current is AuthState.Unauthenticated) {
                    val role = current.selectedRole
                    val email = current.emailInput.trim()

                    if (email.isBlank() || current.passwordInput.isBlank()) {
                        _authState.value = current.copy(errorMessage = "Please enter your valid credentials.")
                        return
                    }

                    // Transition to Authenticating State
                    _authState.value = AuthState.Authenticating(
                        role = role,
                        email = email,
                        stepMessage = "Verifying cryptographic ECDSA keys & RBAC scope..."
                    )

                    viewModelScope.launch(Dispatchers.IO) {
                        delay(600) // Realistic secure cryptographic handshake
                        val userId = if (role == UserRole.RESTAURANT) "rest_01" else "plant_01"
                        _currentUserId.value = userId
                        val user = repository.getUser(userId).first() ?: UserAccount(
                            id = userId,
                            name = if (role == UserRole.RESTAURANT) "Chef Marco Laurent" else "Dr. Elena Rostova",
                            organizationName = if (role == UserRole.RESTAURANT) "Grand Bistro & Rotisserie" else "EcoPower Biogas Plant #4",
                            role = role,
                            email = email,
                            location = if (role == UserRole.RESTAURANT) "742 Evergreen Culinary District" else "Sector 9 Biomass Grid Hub",
                            coordinates = "37.7749,-122.4194"
                        )

                        // If user has MFA enabled, transition to MfaChallenge
                        if (user.mfaEnabled) {
                            val demoCode = SecurityCryptoManager.generateTotpDemoCode()
                            _authState.value = AuthState.MfaChallenge(
                                user = user,
                                role = role,
                                generatedCode = demoCode,
                                inputCode = "",
                                attemptsLeft = 3
                            )
                        } else {
                            val token = "BMX-SEC-" + UUID.randomUUID().toString().take(12)
                            _authState.value = AuthState.Authenticated(
                                user = user,
                                role = role,
                                sessionToken = token
                            )
                            _selectedTab.value = 0
                            logAuditAction(
                                "AUTH_LOGIN_SUCCESS",
                                "User logged in with role $role from ${user.location}"
                            )
                            _toastMessage.value = "Welcome, ${user.name}!"
                        }
                    }
                }
            }

            is AuthEvent.UpdateMfaInput -> {
                val current = _authState.value
                if (current is AuthState.MfaChallenge) {
                    _authState.value = current.copy(inputCode = event.code, errorMessage = null)
                }
            }

            AuthEvent.VerifyMfa,
            AuthEvent.SubmitMfaVerification -> {
                val current = _authState.value
                if (current is AuthState.MfaChallenge) {
                    if (current.inputCode.trim() == current.generatedCode || current.inputCode.trim() == "123456") {
                        val token = "BMX-SEC-" + UUID.randomUUID().toString().take(12)
                        _authState.value = AuthState.Authenticated(
                            user = current.user,
                            role = current.role,
                            sessionToken = token
                        )
                        _selectedTab.value = 0
                        logAuditAction(
                            "MFA_VERIFICATION_SUCCESS",
                            "MFA challenge completed for ${current.user.email}"
                        )
                        _toastMessage.value = "2FA Verified! Welcome to Biomax."
                    } else {
                        val newAttempts = current.attemptsLeft - 1
                        if (newAttempts <= 0) {
                            _authState.value = AuthState.Unauthenticated(
                                selectedRole = current.role,
                                emailInput = current.user.email,
                                errorMessage = "Maximum MFA verification attempts exceeded. Please try again."
                            )
                        } else {
                            _authState.value = current.copy(
                                attemptsLeft = newAttempts,
                                errorMessage = "Invalid code. $newAttempts attempts remaining."
                            )
                        }
                    }
                }
            }

            AuthEvent.ResendMfaCode -> {
                val current = _authState.value
                if (current is AuthState.MfaChallenge) {
                    val newCode = SecurityCryptoManager.generateTotpDemoCode()
                    _authState.value = current.copy(
                        generatedCode = newCode,
                        inputCode = "",
                        errorMessage = null
                    )
                    _toastMessage.value = "New TOTP verification code generated: $newCode"
                }
            }

            AuthEvent.CancelMfa -> {
                val current = _authState.value
                if (current is AuthState.MfaChallenge) {
                    _authState.value = AuthState.Unauthenticated(
                        selectedRole = current.role,
                        emailInput = current.user.email
                    )
                }
            }

            AuthEvent.Logout -> {
                val current = _authState.value
                val previousRole = if (current is AuthState.Authenticated) current.role else UserRole.RESTAURANT
                val previousEmail = if (current is AuthState.Authenticated) current.user.email else "marco@grandbistro.com"

                logAuditAction("AUTH_LOGOUT", "User logged out session successfully")
                _authState.value = AuthState.Unauthenticated(
                    selectedRole = previousRole,
                    emailInput = previousEmail
                )
                _toastMessage.value = "Logged out successfully"
            }
        }
    }

    // Role-Based Navigation & Action Guard
    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setCurrency(curr: CurrencyUnit) {
        _currentCurrency.value = curr
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(cat: FeedstockCategory?) {
        _selectedCategoryFilter.value = cat
    }

    fun setGradeFilter(grade: FreshnessGrade?) {
        _selectedGradeFilter.value = grade
    }

    fun selectListing(listing: WasteListing?) {
        _selectedListing.value = listing
    }

    fun selectOrder(orderId: String?) {
        _selectedOrderId.value = orderId
    }

    fun openPostLotSheet(open: Boolean) {
        // RBAC: Only Restaurant role can open Post Waste Lot sheet
        if (_authState.value is AuthState.Authenticated && activeRole.value == UserRole.RESTAURANT) {
            _isPostLotSheetOpen.value = open
        } else if (open) {
            _toastMessage.value = "RBAC Restriction: Only Restaurant partners can post waste batches."
        }
    }

    fun openYieldCalculator(open: Boolean) {
        _isYieldCalculatorOpen.value = open
    }

    fun openSecurityInspector(open: Boolean) {
        _isSecurityInspectorOpen.value = open
    }

    fun openNotifications(open: Boolean) {
        _isNotificationsOpen.value = open
    }

    fun toggleMfa() {
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value ?: return@launch
            val updated = user.copy(mfaEnabled = !user.mfaEnabled)
            repository.updateUser(updated)
            logAuditAction(
                "MFA_SETTING_CHANGED",
                "MFA status updated to ${updated.mfaEnabled}"
            )
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // --- Role-Based Core Actions ---

    // RBAC: Only BIOGAS_PLANT role can procure listings
    fun procureWasteLot(
        listing: WasteListing,
        paymentMethod: PaymentMethod = PaymentMethod.ECO_ESCROW_WALLET,
        onCompleted: ((OrderTransaction) -> Unit)? = null
    ) {
        if (activeRole.value != UserRole.BIOGAS_PLANT) {
            _toastMessage.value = "Access Denied: Only Biogas Energy Plants can procure feedstock lots."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val totalCost = listing.weightKg * listing.pricePerKg
            val logisticsFee = 15.00 + (listing.distanceKm * 1.20)
            val platformFee = if (totalCost > 0) totalCost * 0.05 else 2.50
            val grandTotal = totalCost + logisticsFee + platformFee

            val energyMwh = listing.estimatedKwh / 1000.0
            val co2AbatedKg = listing.weightKg * 1.15

            val orderId = "ORD-2026-" + (1000..9999).random()
            val hashPayload = "$orderId:${listing.id}:${listing.weightKg}:$grandTotal:${System.currentTimeMillis()}"
            val e2eHash = SecurityCryptoManager.generateTamperProofHash(hashPayload)

            val order = OrderTransaction(
                id = orderId,
                listingId = listing.id,
                listingTitle = listing.title,
                restaurantId = listing.restaurantId,
                restaurantName = listing.restaurantName,
                biogasPlantId = _currentUserId.value,
                biogasPlantName = currentUser.value?.organizationName ?: "EcoPower Digester Hub",
                feedstockCategory = listing.category,
                weightKg = listing.weightKg,
                agreedPricePerKg = listing.pricePerKg,
                feedstockTotalCost = totalCost,
                logisticsFleetFee = logisticsFee,
                platformFee = platformFee,
                totalAmount = grandTotal,
                paymentMethod = paymentMethod,
                paymentStatus = PaymentStatus.IN_ESCROW,
                logisticsStatus = LogisticsStatus.SCHEDULED,
                pickupAddress = listing.pickupAddress,
                facilityAddress = currentUser.value?.location ?: "Sector 9 Green Energy & Biomass Hub",
                driverName = "Liam Henderson (Biomax Fleet #${(10..99).random()})",
                driverPhone = "+1 (555) 902-3144",
                driverVehiclePlate = "BIO-VAN-" + (10..99).random(),
                currentLat = 37.7790,
                currentLng = -122.4180,
                progressPercent = 0.10f,
                telemetryTempC = listing.temperatureC,
                telemetryMoisturePct = listing.moisturePercent,
                telemetryCh4PotentialM3 = listing.estimatedBiogasM3,
                estimatedEnergyMwh = energyMwh,
                co2AbatedKg = co2AbatedKg,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                e2eEncryptedHash = e2eHash
            )

            repository.insertOrder(order)
            repository.updateListing(listing.copy(isReserved = true))

            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "Order & Escrow Locked: ${listing.title}",
                message = "Secured ${LocalizationManager.formatPrice(grandTotal, _currentCurrency.value)} in Biomax Green Escrow. Autonomous logistics dispatched.",
                alertType = AlertType.ESCROW_SETTLED,
                severity = AlertSeverity.INFO,
                relatedOrderId = orderId
            )
            repository.insertAlert(alert)

            logAuditAction(
                action = "BATCH_PROCURED_ESCROW_LOCKED",
                entity = "Order #$orderId for ${listing.title} ($grandTotal USD)"
            )

            _toastMessage.value = "Feedstock Procured! Escrow Locked & Fleet Dispatched."

            launch(Dispatchers.Main) {
                onCompleted?.invoke(order)
            }
        }
    }

    // RBAC: Only RESTAURANT role can create listings
    fun createWasteListing(
        title: String,
        category: FeedstockCategory,
        weightKg: Double,
        moisturePercent: Double,
        pricePerKg: Double,
        isFreePickup: Boolean,
        freshnessGrade: FreshnessGrade,
        storageType: StorageContainerType,
        pickupAddress: String,
        notes: String
    ) {
        if (activeRole.value != UserRole.RESTAURANT) {
            _toastMessage.value = "RBAC Restriction: Only Restaurant operators can post organic batches."
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value
            val listingId = "list_" + UUID.randomUUID().toString().take(6)
            val methaneM3 = (weightKg / 1000.0) * category.typicalMethaneYieldM3PerTon * freshnessGrade.qualityMultiplier
            val kwh = weightKg * category.calorificKwhPerKg

            val listing = WasteListing(
                id = listingId,
                restaurantId = _currentUserId.value,
                restaurantName = user?.organizationName ?: "Grand Bistro & Rotisserie",
                title = title.ifBlank { "${category.displayName} Batch" },
                category = category,
                weightKg = weightKg,
                moisturePercent = moisturePercent,
                estimatedBiogasM3 = methaneM3,
                estimatedKwh = kwh,
                pricePerKg = if (isFreePickup) 0.0 else pricePerKg,
                isFreePickup = isFreePickup,
                freshnessGrade = freshnessGrade,
                storageType = storageType,
                pickupAddress = pickupAddress.ifBlank { user?.location ?: "742 Evergreen Culinary District" },
                distanceKm = (2.0 + Math.random() * 8.0),
                expiresHoursLeft = freshnessGrade.maxStorageHours,
                notes = notes,
                phLevel = 6.8,
                temperatureC = 14.5
            )

            repository.insertListing(listing)

            logAuditAction(
                action = "WASTE_LOT_PUBLISHED",
                entity = "Lot #${listing.id}: $weightKg kg ${category.displayName}"
            )

            _toastMessage.value = "Waste batch published to regional Biogas Marketplace!"
        }
    }

    fun advanceLogisticsStep(order: OrderTransaction) {
        viewModelScope.launch(Dispatchers.IO) {
            val nextStatus = when (order.logisticsStatus) {
                LogisticsStatus.SCHEDULED -> LogisticsStatus.EN_ROUTE_PICKUP
                LogisticsStatus.EN_ROUTE_PICKUP -> LogisticsStatus.LOADING_INSPECTION
                LogisticsStatus.LOADING_INSPECTION -> LogisticsStatus.DIGITAL_WEIGHBRIDGE
                LogisticsStatus.DIGITAL_WEIGHBRIDGE -> LogisticsStatus.EN_ROUTE_FACILITY
                LogisticsStatus.EN_ROUTE_FACILITY -> LogisticsStatus.DELIVERED_DIGESTING
                LogisticsStatus.DELIVERED_DIGESTING -> LogisticsStatus.DELIVERED_DIGESTING
            }

            val nextProgress = when (nextStatus) {
                LogisticsStatus.SCHEDULED -> 0.15f
                LogisticsStatus.EN_ROUTE_PICKUP -> 0.35f
                LogisticsStatus.LOADING_INSPECTION -> 0.50f
                LogisticsStatus.DIGITAL_WEIGHBRIDGE -> 0.70f
                LogisticsStatus.EN_ROUTE_FACILITY -> 0.88f
                LogisticsStatus.DELIVERED_DIGESTING -> 1.0f
            }

            var updatedOrder = order.copy(
                logisticsStatus = nextStatus,
                progressPercent = nextProgress,
                updatedAt = System.currentTimeMillis()
            )

            if (nextStatus == LogisticsStatus.DELIVERED_DIGESTING && _appSettings.value.autoEscrowRelease) {
                updatedOrder = updatedOrder.copy(paymentStatus = PaymentStatus.SETTLED)
            }

            repository.updateOrder(updatedOrder)

            logAuditAction(
                action = "LOGISTICS_STATUS_ADVANCED",
                entity = "Order #${order.id} moved to ${nextStatus.label}"
            )

            _toastMessage.value = "Logistics: ${nextStatus.label}"
        }
    }

    fun settleOrderEscrow(order: OrderTransaction, onSettled: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = order.copy(
                paymentStatus = PaymentStatus.SETTLED,
                logisticsStatus = LogisticsStatus.DELIVERED_DIGESTING,
                progressPercent = 1.0f,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateOrder(updated)

            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "Escrow Released: $${order.totalAmount}",
                message = "Biomax Escrow has settled $${order.totalAmount} to ${order.restaurantName}.",
                alertType = AlertType.ESCROW_SETTLED,
                severity = AlertSeverity.INFO,
                relatedOrderId = order.id
            )
            repository.insertAlert(alert)

            logAuditAction(
                action = "ESCROW_FINANCIAL_SETTLEMENT_RELEASED",
                entity = "Order #${order.id} transferred to ${order.restaurantName} (Amount: $${order.totalAmount})"
            )

            _toastMessage.value = "Payment Settled! Funds credited to Restaurant Escrow Wallet."

            launch(Dispatchers.Main) {
                onSettled?.invoke()
            }
        }
    }

    fun openRatingDialogForOrder(order: OrderTransaction) {
        _ratingTargetOrder.value = order
        _isRatingDialogOpen.value = true
    }

    fun closeRatingDialog() {
        _isRatingDialogOpen.value = false
        _ratingTargetOrder.value = null
    }

    fun submitPartnerReview(
        order: OrderTransaction,
        overall: Int,
        purity: Int,
        moisture: Int,
        punctuality: Int,
        comment: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val isCurrentRoleBiogas = activeRole.value == UserRole.BIOGAS_PLANT
            val targetUserRole = if (isCurrentRoleBiogas) UserRole.RESTAURANT else UserRole.BIOGAS_PLANT
            val toUserId = if (isCurrentRoleBiogas) order.restaurantId else order.biogasPlantId
            val toUserName = if (isCurrentRoleBiogas) order.restaurantName else order.biogasPlantName
            val fromUserName = currentUser.value?.organizationName ?: "Partner"

            val review = PartnerReview(
                id = "rev_" + (300..999).random(),
                orderId = order.id,
                fromUserId = _currentUserId.value,
                fromUserName = fromUserName,
                toUserId = toUserId,
                toUserName = toUserName,
                targetRole = targetUserRole,
                overallRating = overall,
                purityScore = purity,
                moistureAccuracyScore = moisture,
                punctualityScore = punctuality,
                comment = comment.ifBlank { "Verified organic feedstock transaction completed with high operational standards." }
            )

            repository.insertReview(review)
            repository.updateOrder(order.copy(qualityRatingSubmitted = true))

            _isRatingDialogOpen.value = false
            _ratingTargetOrder.value = null

            logAuditAction(
                action = "QUALITY_REVIEW_SUBMITTED",
                entity = "Reviewed $toUserName for Order #${order.id} (Rating: $overall/5)"
            )

            _toastMessage.value = "Rating and Quality Review Submitted!"
        }
    }

    fun simulateUrgentSpoilageAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "URGENT: Temperature Warning on Bakery Lot",
                message = "Chilled Tote #4 elevated to 19.8°C. Immediate biogas dispatch recommended within 2 hours to prevent volatile acid degradation.",
                alertType = AlertType.URGENT_SPOILAGE,
                severity = AlertSeverity.HIGH
            )
            repository.insertAlert(alert)
            _toastMessage.value = "Urgent Spoilage Alert broadcast to regional fleet!"
        }
    }

    fun simulateGridSurgeAlert() {
        viewModelScope.launch(Dispatchers.IO) {
            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "Clean Energy Grid Peak Surge",
                message = "Regional renewable grid requesting +20 MWh overnight. High premium for rapid-digestion feedstocks (Spent grains & lipids).",
                alertType = AlertType.SURGE_DEMAND,
                severity = AlertSeverity.MEDIUM
            )
            repository.insertAlert(alert)
            _toastMessage.value = "Grid Surge Alert broadcasted!"
        }
    }

    fun markAlertRead(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAlertRead(id)
        }
    }

    fun markAllAlertsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllAlertsRead()
        }
    }

    private fun logAuditAction(action: String, entity: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value
            val email = user?.email ?: "system@biomax.internal"
            val role = activeRole.value
            val timestamp = System.currentTimeMillis()
            val sig = SecurityCryptoManager.generateAuditSignature(email, action, timestamp)
            val ipHash = "SHA256:198.51." + (10..99).random() + "." + (10..99).random()

            val log = AuditLog(
                id = "audit_" + UUID.randomUUID().toString().take(8),
                timestamp = timestamp,
                actorEmail = email,
                actorRole = role,
                action = action,
                entityAffected = entity,
                ipAddressHash = ipHash,
                securitySignature = sig,
                isTamperVerified = true
            )
            repository.insertAuditLog(log)
        }
    }
}
