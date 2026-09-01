package com.example.biomax.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.biomax.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserAccount::class,
        WasteListing::class,
        OrderTransaction::class,
        PartnerReview::class,
        SystemAlertNotification::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BiomaxDatabase : RoomDatabase() {
    abstract fun biomaxDao(): BiomaxDao

    companion object {
        @Volatile
        private var INSTANCE: BiomaxDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): BiomaxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BiomaxDatabase::class.java,
                    "biomax_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(BiomaxDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class BiomaxDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database.biomaxDao())
                }
            }
        }
    }
}

suspend fun populateInitialData(dao: BiomaxDao) {
    // 1. Initial Users
    val restaurantUser = UserAccount(
        id = "rest_01",
        name = "Chef Marco Laurent",
        organizationName = "Grand Bistro & Rotisserie",
        role = UserRole.RESTAURANT,
        email = "marco@grandbistro.com",
        location = "742 Evergreen Culinary District, Metro Area",
        coordinates = "37.7749,-122.4194",
        rating = 4.92,
        totalRatingsCount = 54,
        isVerifiedBadge = true,
        mfaEnabled = true,
        mfaMethod = "FIDO2 Passkey & TOTP",
        escrowWalletBalance = 6320.50,
        totalWasteTradedTons = 68.4,
        greenEnergyGeneratedMwh = 51.3,
        co2AbatedTons = 29.2
    )

    val biogasPlantUser = UserAccount(
        id = "plant_01",
        name = "Dr. Elena Rostova",
        organizationName = "EcoPower Anaerobic Digester Plant #4",
        role = UserRole.BIOGAS_PLANT,
        email = "operations@ecopower-biogas.org",
        location = "Sector 9 Green Energy & Biomass Hub",
        coordinates = "37.7850,-122.4080",
        rating = 4.98,
        totalRatingsCount = 112,
        isVerifiedBadge = true,
        mfaEnabled = true,
        mfaMethod = "Hardware Token & SMS OTP",
        escrowWalletBalance = 24890.00,
        totalWasteTradedTons = 480.0,
        greenEnergyGeneratedMwh = 360.5,
        co2AbatedTons = 205.8
    )

    dao.insertUser(restaurantUser)
    dao.insertUser(biogasPlantUser)

    // 2. Initial Waste Listings
    val listings = listOf(
        WasteListing(
            id = "list_101",
            restaurantId = "rest_01",
            restaurantName = "Grand Bistro & Rotisserie",
            title = "Fresh Bakery & Cooked Organic Scraps",
            category = FeedstockCategory.COOKED_KITCHEN_SCRAPS,
            weightKg = 450.0,
            moisturePercent = 68.0,
            estimatedBiogasM3 = 81.0,
            estimatedKwh = 855.0,
            pricePerKg = 0.08,
            isFreePickup = false,
            freshnessGrade = FreshnessGrade.GRADE_A,
            storageType = StorageContainerType.CHILLED_ORGANIC_DRUM,
            pickupAddress = "742 Evergreen Culinary District",
            distanceKm = 4.2,
            expiresHoursLeft = 6,
            phLevel = 6.9,
            temperatureC = 12.4,
            notes = "Segregated morning kitchen prep and surplus artisanal pastry. Zero plastic or inorganic contaminant."
        ),
        WasteListing(
            id = "list_102",
            restaurantId = "rest_02",
            restaurantName = "Golden Dragon Kitchens",
            title = "Spent Fryer Grease & Trap Lipids",
            category = FeedstockCategory.FRYER_GREASE_FATS,
            weightKg = 320.0,
            moisturePercent = 14.0,
            estimatedBiogasM3 = 166.4,
            estimatedKwh = 1856.0,
            pricePerKg = 0.16,
            isFreePickup = false,
            freshnessGrade = FreshnessGrade.GRADE_A,
            storageType = StorageContainerType.SEALED_STAINLESS_TANK,
            pickupAddress = "128 East Dragon Way, Downtown",
            distanceKm = 6.8,
            expiresHoursLeft = 18,
            phLevel = 6.2,
            temperatureC = 22.0,
            notes = "High-calorific vegetable frying oils. High methane surge factor."
        ),
        WasteListing(
            id = "list_103",
            restaurantId = "rest_03",
            restaurantName = "Barista Craft Roasters",
            title = "Pressed Espresso & Spent Coffee Grounds",
            category = FeedstockCategory.COFFEE_GROUNDS_SPENT,
            weightKg = 600.0,
            moisturePercent = 55.0,
            estimatedBiogasM3 = 90.0,
            estimatedKwh = 960.0,
            pricePerKg = 0.04,
            isFreePickup = true,
            freshnessGrade = FreshnessGrade.GRADE_B,
            storageType = StorageContainerType.HERMETIC_COMPOST_BIN,
            pickupAddress = "55 Artisan Alley, North District",
            distanceKm = 8.5,
            expiresHoursLeft = 24,
            phLevel = 5.8,
            temperatureC = 18.2,
            notes = "Pure organic coffee grounds packed in certified compostable bio-bags."
        ),
        WasteListing(
            id = "list_104",
            restaurantId = "rest_04",
            restaurantName = "Valley Fresh Salad Bar",
            title = "Organic Fruit & Vegetable Trimmings",
            category = FeedstockCategory.VEGGIE_PRODUCE_TRIMMINGS,
            weightKg = 850.0,
            moisturePercent = 82.0,
            estimatedBiogasM3 = 93.5,
            estimatedKwh = 935.0,
            pricePerKg = 0.05,
            isFreePickup = false,
            freshnessGrade = FreshnessGrade.GRADE_A,
            storageType = StorageContainerType.HERMETIC_COMPOST_BIN,
            pickupAddress = "990 Market Boulevard",
            distanceKm = 11.2,
            expiresHoursLeft = 10,
            phLevel = 6.5,
            temperatureC = 11.0,
            notes = "Kale stalks, citrus rinds, carrot shavings, apple cores. Cold stored."
        ),
        WasteListing(
            id = "list_105",
            restaurantId = "rest_05",
            restaurantName = "Apex Microbrewery & Pub",
            title = "Fresh Brewery Spent Grain Mash (Malt/Barley)",
            category = FeedstockCategory.BREWERY_SPENT_GRAINS,
            weightKg = 1200.0,
            moisturePercent = 72.0,
            estimatedBiogasM3 = 252.0,
            estimatedKwh = 2760.0,
            pricePerKg = 0.07,
            isFreePickup = false,
            freshnessGrade = FreshnessGrade.GRADE_A,
            storageType = StorageContainerType.BULK_VACUUM_HOPPER,
            pickupAddress = "41 Industrial Riverway",
            distanceKm = 14.8,
            expiresHoursLeft = 14,
            phLevel = 6.4,
            temperatureC = 28.5,
            notes = "Warm spent barley mash from today's brewing batch. Excellent C:N ratio for digester bacteria."
        )
    )

    listings.forEach { dao.insertListing(it) }

    // 3. Initial Active Order with Live Logistics
    val activeOrder = OrderTransaction(
        id = "ORD-2026-8891",
        listingId = "list_100_completed",
        listingTitle = "Organic Kitchen Food Waste & Scraps",
        restaurantId = "rest_01",
        restaurantName = "Grand Bistro & Rotisserie",
        biogasPlantId = "plant_01",
        biogasPlantName = "EcoPower Anaerobic Digester Plant #4",
        feedstockCategory = FeedstockCategory.COOKED_KITCHEN_SCRAPS,
        weightKg = 520.0,
        agreedPricePerKg = 0.08,
        feedstockTotalCost = 41.60,
        logisticsFleetFee = 15.00,
        platformFee = 2.50,
        totalAmount = 59.10,
        paymentMethod = PaymentMethod.ECO_ESCROW_WALLET,
        paymentStatus = PaymentStatus.IN_ESCROW,
        logisticsStatus = LogisticsStatus.DIGITAL_WEIGHBRIDGE,
        pickupAddress = "742 Evergreen Culinary District",
        facilityAddress = "Sector 9 Green Energy & Biomass Hub",
        driverName = "Marcus Vance (Biomax Fleet #14)",
        driverPhone = "+1 (555) 382-9014",
        driverVehiclePlate = "BIO-TRUCK-88",
        currentLat = 37.7812,
        currentLng = -122.4140,
        progressPercent = 0.65f,
        telemetryTempC = 13.8,
        telemetryMoisturePct = 69.2,
        telemetryCh4PotentialM3 = 93.6,
        estimatedEnergyMwh = 0.988,
        co2AbatedKg = 560.0,
        createdAt = System.currentTimeMillis() - 42 * 60 * 1000,
        updatedAt = System.currentTimeMillis() - 5 * 60 * 1000,
        e2eEncryptedHash = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069"
    )
    dao.insertOrder(activeOrder)

    // 4. Initial System Alerts
    val alerts = listOf(
        SystemAlertNotification(
            id = "alert_01",
            title = "Temperature Anomaly Pre-Alert Cleared",
            message = "Fleet #14 chilled chamber calibrated at 13.8°C. Feedstock freshness verified.",
            alertType = AlertType.LOGISTICS_DISPATCH,
            severity = AlertSeverity.INFO,
            timestamp = System.currentTimeMillis() - 15 * 60 * 1000,
            relatedOrderId = "ORD-2026-8891"
        ),
        SystemAlertNotification(
            id = "alert_02",
            title = "High Demand Surge for Fryer Lipids",
            message = "Biogas facilities in your district offering +15% premium for high-calorific fryer grease.",
            alertType = AlertType.SURGE_DEMAND,
            severity = AlertSeverity.MEDIUM,
            timestamp = System.currentTimeMillis() - 60 * 60 * 1000
        ),
        SystemAlertNotification(
            id = "alert_03",
            title = "Escrow Lock Confirmed",
            message = "$59.10 safely locked in Biomax Escrow for Order #ORD-2026-8891.",
            alertType = AlertType.ESCROW_SETTLED,
            severity = AlertSeverity.INFO,
            timestamp = System.currentTimeMillis() - 40 * 60 * 1000,
            relatedOrderId = "ORD-2026-8891"
        ),
        SystemAlertNotification(
            id = "alert_04",
            title = "MFA Security Handshake Verified",
            message = "Hardware Authenticator signed session from TLS 1.3 secured gateway.",
            alertType = AlertType.MFA_SECURITY,
            severity = AlertSeverity.INFO,
            timestamp = System.currentTimeMillis() - 120 * 60 * 1000
        )
    )
    alerts.forEach { dao.insertAlert(it) }

    // 5. Initial Reviews
    val reviews = listOf(
        PartnerReview(
            id = "rev_01",
            orderId = "ORD-2026-8710",
            fromUserId = "plant_01",
            fromUserName = "Dr. Elena Rostova (EcoPower Biogas)",
            toUserId = "rest_01",
            toUserName = "Grand Bistro & Rotisserie",
            targetRole = UserRole.RESTAURANT,
            overallRating = 5,
            purityScore = 5,
            moistureAccuracyScore = 5,
            punctualityScore = 5,
            comment = "Exceptional feedstock purity! Zero contaminants in the bakery mash, digester methane yield was 10% above theoretical benchmark."
        ),
        PartnerReview(
            id = "rev_02",
            orderId = "ORD-2026-8710",
            fromUserId = "rest_01",
            fromUserName = "Grand Bistro & Rotisserie",
            toUserId = "plant_01",
            toUserName = "EcoPower Anaerobic Digester Plant #4",
            targetRole = UserRole.BIOGAS_PLANT,
            overallRating = 5,
            purityScore = 5,
            moistureAccuracyScore = 5,
            punctualityScore = 5,
            comment = "Instant weighbridge clearance and immediate automated escrow payout. Driver arrived exactly on schedule."
        )
    )
    reviews.forEach { dao.insertReview(it) }

    // 6. Initial Audit Logs
    val auditLogs = listOf(
        AuditLog(
            id = "audit_01",
            timestamp = System.currentTimeMillis() - 45 * 60 * 1000,
            actorEmail = "operations@ecopower-biogas.org",
            actorRole = UserRole.BIOGAS_PLANT,
            action = "ESCROW_LOCK_INITIATED",
            entityAffected = "ORD-2026-8891 (Amount: $59.10 USD)",
            ipAddressHash = "SHA256:192.0.2.45->f3a2b1",
            securitySignature = "RSA4096:9a4f78bc02e",
            isTamperVerified = true
        ),
        AuditLog(
            id = "audit_02",
            timestamp = System.currentTimeMillis() - 30 * 60 * 1000,
            actorEmail = "dispatch@biomax.logistics.internal",
            actorRole = UserRole.ADMIN,
            action = "FLEET_DISPATCH_ROUTE_OPTIMIZED",
            entityAffected = "Truck Plate BIO-TRUCK-88 -> Pickup Route #4",
            ipAddressHash = "SHA256:10.240.0.12->c89df1",
            securitySignature = "RSA4096:3d1e57ab99c",
            isTamperVerified = true
        ),
        AuditLog(
            id = "audit_03",
            timestamp = System.currentTimeMillis() - 5 * 60 * 1000,
            actorEmail = "system@biomax.compliance.eu",
            actorRole = UserRole.ADMIN,
            action = "EU_RED_II_SUSTAINABILITY_LEDGER_CERTIFIED",
            entityAffected = "GHG Abatement Record #GHG-9982 (0.56 Tons CO2e)",
            ipAddressHash = "SHA256:198.51.100.77->bb301e",
            securitySignature = "RSA4096:77ae124ff09",
            isTamperVerified = true
        )
    )
    auditLogs.forEach { dao.insertAuditLog(it) }
}
