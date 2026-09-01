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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class BiomaxViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BiomaxRepository
    val listings: StateFlow<List<WasteListing>>
    val orders: StateFlow<List<OrderTransaction>>
    val reviews: StateFlow<List<PartnerReview>>
    val alerts: StateFlow<List<SystemAlertNotification>>
    val auditLogs: StateFlow<List<AuditLog>>

    // User Session & Role
    private val _activeRole = MutableStateFlow(UserRole.BIOGAS_PLANT)
    val activeRole: StateFlow<UserRole> = _activeRole.asStateFlow()

    private val _currentUserId = MutableStateFlow("plant_01")
    val currentUser: StateFlow<UserAccount?>

    // Localization & Currency
    private val _currentLanguage = MutableStateFlow(AppLanguage.EN)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentCurrency = MutableStateFlow(CurrencyUnit.USD)
    val currentCurrency: StateFlow<CurrencyUnit> = _currentCurrency.asStateFlow()

    // UI Navigation & Dialog states
    private val _selectedTab = MutableStateFlow(0) // 0: Marketplace, 1: Kitchen Lots, 2: Logistics, 3: Analytics, 4: Security
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<String?>("ORD-2026-8891")
    val selectedOrderId: StateFlow<String?> = _selectedOrderId.asStateFlow()

    private val _selectedListing = MutableStateFlow<WasteListing?>(null)
    val selectedListing: StateFlow<WasteListing?> = _selectedListing.asStateFlow()

    // Dialogs
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

    private val _isMfaVerificationOpen = MutableStateFlow(false)
    val isMfaVerificationOpen: StateFlow<Boolean> = _isMfaVerificationOpen.asStateFlow()

    // Marketplace Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<FeedstockCategory?>(null)
    val selectedCategoryFilter: StateFlow<FeedstockCategory?> = _selectedCategoryFilter.asStateFlow()

    private val _selectedGradeFilter = MutableStateFlow<FreshnessGrade?>(null)
    val selectedGradeFilter: StateFlow<FreshnessGrade?> = _selectedGradeFilter.asStateFlow()

    // MFA Temporary state
    val demoMfaCode = MutableStateFlow("889900")
    val mfaInput = MutableStateFlow("")
    val mfaError = MutableStateFlow<String?>(null)

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

    fun selectTab(tab: Int) {
        _selectedTab.value = tab
    }

    fun setLanguage(lang: AppLanguage) {
        _currentLanguage.value = lang
    }

    fun setCurrency(curr: CurrencyUnit) {
        _currentCurrency.value = curr
    }

    fun switchRole(role: UserRole) {
        _activeRole.value = role
        if (role == UserRole.RESTAURANT) {
            _currentUserId.value = "rest_01"
            _selectedTab.value = 1 // Switch to Kitchen lots tab
        } else {
            _currentUserId.value = "plant_01"
            _selectedTab.value = 0 // Switch to Marketplace
        }
        logAuditAction("ROLE_SWITCHED", "Switched active console session to $role")
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
        _isPostLotSheetOpen.value = open
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

    fun generateDemoMfaCode(): String {
        val code = SecurityCryptoManager.generateTotpDemoCode()
        demoMfaCode.value = code
        return code
    }

    fun verifyMfaToken(input: String): Boolean {
        val isValid = SecurityCryptoManager.verifyTotpCode(input, demoMfaCode.value)
        if (isValid) {
            logAuditAction("MFA_AUTHENTICATION_SUCCESS", "Two-Factor TOTP verified successfully")
        } else {
            logAuditAction("MFA_AUTHENTICATION_FAILED", "Failed TOTP challenge code entry")
        }
        return isValid
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

    // --- Core Quick-Commerce Actions ---

    fun procureWasteLot(
        listing: WasteListing,
        paymentMethod: PaymentMethod = PaymentMethod.ECO_ESCROW_WALLET,
        onCompleted: ((OrderTransaction) -> Unit)? = null
    ) {
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

            // Mark listing reserved
            repository.updateListing(listing.copy(isReserved = true))
            // Insert order
            repository.insertOrder(order)
            _selectedOrderId.value = orderId

            // Notification
            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "Escrow Secured & Fleet Dispatched",
                message = "Order #$orderId created. ${LocalizationManager.formatPrice(grandTotal, _currentCurrency.value)} locked in Biomax Escrow. Fleet hauler scheduled.",
                alertType = AlertType.ESCROW_SETTLED,
                severity = AlertSeverity.INFO,
                relatedOrderId = orderId
            )
            repository.insertAlert(alert)

            logAuditAction(
                action = "ORDER_PROCURED_ESCROW_LOCKED",
                entity = "Order #$orderId (Listing: ${listing.title}, Weight: ${listing.weightKg}kg, Total: $grandTotal USD)"
            )

            _toastMessage.value = "Procurement Successful! Order #$orderId locked in Escrow. Fleet Dispatched."
            _selectedTab.value = 2 // Switch to Logistics

            launch(Dispatchers.Main) {
                onCompleted?.invoke(order)
            }
        }
    }

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
        viewModelScope.launch(Dispatchers.IO) {
            val user = currentUser.value
            val biogasM3 = (weightKg / 1000.0) * category.typicalMethaneYieldM3PerTon * freshnessGrade.qualityMultiplier
            val estimatedKwh = weightKg * category.calorificKwhPerKg * freshnessGrade.qualityMultiplier

            val listingId = "list_" + (200..999).random()
            val listing = WasteListing(
                id = listingId,
                restaurantId = user?.id ?: "rest_01",
                restaurantName = user?.organizationName ?: "Grand Bistro & Rotisserie",
                title = title.ifBlank { "${category.displayName} (${weightKg.toInt()} kg)" },
                category = category,
                weightKg = weightKg,
                moisturePercent = moisturePercent,
                estimatedBiogasM3 = biogasM3,
                estimatedKwh = estimatedKwh,
                pricePerKg = if (isFreePickup) 0.0 else pricePerKg,
                isFreePickup = isFreePickup,
                freshnessGrade = freshnessGrade,
                storageType = storageType,
                pickupAddress = pickupAddress.ifBlank { user?.location ?: "742 Evergreen Culinary District" },
                distanceKm = (25..150).random() / 10.0,
                expiresHoursLeft = freshnessGrade.maxStorageHours,
                notes = notes,
                temperatureC = 12.0 + (1..6).random(),
                phLevel = 6.4 + (1..5).random() * 0.1
            )

            repository.insertListing(listing)
            _isPostLotSheetOpen.value = false

            // System alert
            val alert = SystemAlertNotification(
                id = "alert_" + UUID.randomUUID().toString().take(6),
                title = "New Feedstock Lot Available",
                message = "Your waste food lot '${listing.title}' ($weightKg kg) is live on the Biomax regional marketplace.",
                alertType = AlertType.SURGE_DEMAND,
                severity = AlertSeverity.INFO
            )
            repository.insertAlert(alert)

            logAuditAction(
                action = "WASTE_LISTING_PUBLISHED",
                entity = "Listing #$listingId (${listing.title}, $weightKg kg, Grade: $freshnessGrade)"
            )

            _toastMessage.value = "Waste Lot Published! Local Biogas facilities notified."
        }
    }

    fun advanceLogisticsStep(
        order: OrderTransaction,
        onStepAdvanced: ((OrderTransaction) -> Unit)? = null
    ) {
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
                LogisticsStatus.SCHEDULED -> 0.10f
                LogisticsStatus.EN_ROUTE_PICKUP -> 0.30f
                LogisticsStatus.LOADING_INSPECTION -> 0.50f
                LogisticsStatus.DIGITAL_WEIGHBRIDGE -> 0.70f
                LogisticsStatus.EN_ROUTE_FACILITY -> 0.85f
                LogisticsStatus.DELIVERED_DIGESTING -> 1.00f
            }

            // Auto-settle payment if delivered to digester
            val nextPaymentStatus = if (nextStatus == LogisticsStatus.DELIVERED_DIGESTING) {
                PaymentStatus.SETTLED
            } else {
                order.paymentStatus
            }

            val tempVariation = ((-5..5).random() / 10.0)
            val updatedOrder = order.copy(
                logisticsStatus = nextStatus,
                progressPercent = nextProgress,
                paymentStatus = nextPaymentStatus,
                updatedAt = System.currentTimeMillis(),
                telemetryTempC = order.telemetryTempC + tempVariation
            )

            repository.updateOrder(updatedOrder)

            val alertMsg = when (nextStatus) {
                LogisticsStatus.EN_ROUTE_PICKUP -> "Fleet hauler is en route to ${order.restaurantName} for collection."
                LogisticsStatus.LOADING_INSPECTION -> "Feedstock inspected and loaded into sealed anaerobic transport container."
                LogisticsStatus.DIGITAL_WEIGHBRIDGE -> "Digital weighbridge & spectrophotometer certified: ${order.weightKg} kg pure organic mass."
                LogisticsStatus.EN_ROUTE_FACILITY -> "Hauler en route to Biogas Anaerobic Digester facility."
                LogisticsStatus.DELIVERED_DIGESTING -> "Feedstock ingested into Digester Unit. Escrow payment of $${order.totalAmount} settled to ${order.restaurantName}."
                else -> ""
            }

            repository.insertAlert(
                SystemAlertNotification(
                    id = "alert_" + UUID.randomUUID().toString().take(6),
                    title = "Logistics Update: ${nextStatus.label}",
                    message = alertMsg,
                    alertType = if (nextStatus == LogisticsStatus.DELIVERED_DIGESTING) AlertType.ESCROW_SETTLED else AlertType.LOGISTICS_DISPATCH,
                    severity = AlertSeverity.INFO,
                    relatedOrderId = order.id
                )
            )

            logAuditAction(
                action = "LOGISTICS_STATUS_UPDATED",
                entity = "Order #${order.id} moved to ${nextStatus.name} (Payment: ${nextPaymentStatus.name})"
            )

            _toastMessage.value = "Logistics status updated to: ${nextStatus.label}"

            launch(Dispatchers.Main) {
                onStepAdvanced?.invoke(updatedOrder)
            }
        }
    }

    fun settleOrderEscrow(
        order: OrderTransaction,
        onSettled: (() -> Unit)? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val updated = order.copy(
                paymentStatus = PaymentStatus.SETTLED,
                logisticsStatus = LogisticsStatus.DELIVERED_DIGESTING,
                progressPercent = 1.0f,
                updatedAt = System.currentTimeMillis()
            )
            repository.updateOrder(updated)

            repository.insertAlert(
                SystemAlertNotification(
                    id = "alert_" + UUID.randomUUID().toString().take(6),
                    title = "Escrow Settlement Released",
                    message = "Financial settlement of ${LocalizationManager.formatPrice(order.totalAmount, _currentCurrency.value)} completed for Order #${order.id}.",
                    alertType = AlertType.ESCROW_SETTLED,
                    severity = AlertSeverity.INFO,
                    relatedOrderId = order.id
                )
            )

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
            val isCurrentRoleBiogas = _activeRole.value == UserRole.BIOGAS_PLANT
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
            val role = _activeRole.value
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
