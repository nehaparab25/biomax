package com.example.biomax.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    RESTAURANT,
    BIOGAS_PLANT,
    ADMIN
}

enum class FeedstockCategory(val displayName: String, val typicalMethaneYieldM3PerTon: Double, val calorificKwhPerKg: Double) {
    COOKED_KITCHEN_SCRAPS("Cooked Food & Kitchen Scraps", 180.0, 1.9),
    BAKERY_FLOUR_WASTE("Bakery Dough & Spent Flour", 240.0, 2.6),
    COFFEE_GROUNDS_SPENT("Spent Coffee Grounds", 150.0, 1.6),
    FRYER_GREASE_FATS("Used Fryer Grease & Trap Fats", 520.0, 5.8),
    VEGGIE_PRODUCE_TRIMMINGS("Fruit & Vegetable Trimmings", 110.0, 1.1),
    DAIRY_CHEESE_WHEY("Dairy Whey & Spoiled Dairy", 280.0, 3.1),
    BREWERY_SPENT_GRAINS("Brewery Spent Grains", 210.0, 2.3),
    MEAT_ORGANIC_TRIMMINGS("Organic Meat Trim & Fats", 390.0, 4.2)
}

enum class FreshnessGrade(val label: String, val qualityMultiplier: Double, val maxStorageHours: Int) {
    GRADE_A("Grade A - Ultra Fresh (<12h)", 1.25, 24),
    GRADE_B("Grade B - Standard Organic (<36h)", 1.0, 48),
    GRADE_C("Grade C - Bulk Digestible (<72h)", 0.8, 72)
}

enum class StorageContainerType(val title: String) {
    SEALED_STAINLESS_TANK("Sealed Stainless Tank"),
    CHILLED_ORGANIC_DRUM("Chilled Organic Drum"),
    HERMETIC_COMPOST_BIN("Hermetic Bio-Tote"),
    BULK_VACUUM_HOPPER("Bulk Vacuum Hopper")
}

enum class LogisticsStatus(val label: String, val stepIndex: Int) {
    SCHEDULED("Scheduled & Fleet Assigned", 0),
    EN_ROUTE_PICKUP("Hauler En Route to Restaurant", 1),
    LOADING_INSPECTION("Waste Inspection & Loaded", 2),
    DIGITAL_WEIGHBRIDGE("Weighbridge & Quality Lab Check", 3),
    EN_ROUTE_FACILITY("En Route to Biogas Facility", 4),
    DELIVERED_DIGESTING("Digester Feedstock Ingested", 5)
}

enum class PaymentMethod(val label: String) {
    ECO_ESCROW_WALLET("Biomax Green Escrow Wallet"),
    BANK_DIRECT_SEPA_ACH("Direct Corporate Wire (SEPA/ACH)"),
    CARBON_ENERGY_CREDITS("Clean Energy MWh Credits"),
    INSTANT_CARD_SETTLEMENT("Corporate Green Fleet Card")
}

enum class PaymentStatus(val label: String) {
    IN_ESCROW("Funds Secured in Escrow"),
    PROCESSING("Processing Settlement"),
    SETTLED("Settled & Transferred"),
    REFUNDED("Refunded")
}

enum class AlertSeverity {
    HIGH,
    MEDIUM,
    INFO
}

enum class AlertType {
    URGENT_SPOILAGE,
    SURGE_DEMAND,
    LOGISTICS_DISPATCH,
    ESCROW_SETTLED,
    MFA_SECURITY,
    COMPLIANCE_ALERT
}

@Entity(tableName = "users")
data class UserAccount(
    @PrimaryKey val id: String,
    val name: String,
    val organizationName: String,
    val role: UserRole,
    val email: String,
    val location: String,
    val coordinates: String,
    val rating: Double = 4.9,
    val totalRatingsCount: Int = 38,
    val isVerifiedBadge: Boolean = true,
    val mfaEnabled: Boolean = true,
    val mfaMethod: String = "TOTP Authenticator",
    val escrowWalletBalance: Double = 4850.00,
    val totalWasteTradedTons: Double = 42.5,
    val greenEnergyGeneratedMwh: Double = 31.8,
    val co2AbatedTons: Double = 18.2
)

@Entity(tableName = "listings")
data class WasteListing(
    @PrimaryKey val id: String,
    val restaurantId: String,
    val restaurantName: String,
    val title: String,
    val category: FeedstockCategory,
    val weightKg: Double,
    val moisturePercent: Double,
    val estimatedBiogasM3: Double,
    val estimatedKwh: Double,
    val pricePerKg: Double,
    val isFreePickup: Boolean,
    val freshnessGrade: FreshnessGrade,
    val storageType: StorageContainerType,
    val pickupAddress: String,
    val distanceKm: Double,
    val expiresHoursLeft: Int,
    val isReserved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val phLevel: Double = 6.8,
    val temperatureC: Double = 14.5,
    val notes: String = ""
)

@Entity(tableName = "orders")
data class OrderTransaction(
    @PrimaryKey val id: String,
    val listingId: String,
    val listingTitle: String,
    val restaurantId: String,
    val restaurantName: String,
    val biogasPlantId: String,
    val biogasPlantName: String,
    val feedstockCategory: FeedstockCategory,
    val weightKg: Double,
    val agreedPricePerKg: Double,
    val feedstockTotalCost: Double,
    val logisticsFleetFee: Double,
    val platformFee: Double,
    val totalAmount: Double,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val logisticsStatus: LogisticsStatus,
    val pickupAddress: String,
    val facilityAddress: String,
    val driverName: String,
    val driverPhone: String,
    val driverVehiclePlate: String,
    val currentLat: Double,
    val currentLng: Double,
    val progressPercent: Float,
    val telemetryTempC: Double,
    val telemetryMoisturePct: Double,
    val telemetryCh4PotentialM3: Double,
    val estimatedEnergyMwh: Double,
    val co2AbatedKg: Double,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val e2eEncryptedHash: String,
    val qualityRatingSubmitted: Boolean = false
)

@Entity(tableName = "reviews")
data class PartnerReview(
    @PrimaryKey val id: String,
    val orderId: String,
    val fromUserId: String,
    val fromUserName: String,
    val toUserId: String,
    val toUserName: String,
    val targetRole: UserRole,
    val overallRating: Int, // 1-5
    val purityScore: Int, // 1-5
    val moistureAccuracyScore: Int, // 1-5
    val punctualityScore: Int, // 1-5
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "system_alerts")
data class SystemAlertNotification(
    @PrimaryKey val id: String,
    val title: String,
    val message: String,
    val alertType: AlertType,
    val severity: AlertSeverity,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val relatedOrderId: String? = null
)

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actorEmail: String,
    val actorRole: UserRole,
    val action: String,
    val entityAffected: String,
    val ipAddressHash: String,
    val securitySignature: String,
    val isTamperVerified: Boolean = true
)

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    EN("en", "English", "🇺🇸"),
    ES("es", "Español", "🇪🇸"),
    DE("de", "Deutsch", "🇩🇪"),
    FR("fr", "Français", "🇫🇷"),
    HI("hi", "हिन्दी", "🇮🇳"),
    ZH("zh", "中文 (Mandarin)", "🇨🇳"),
    JA("ja", "日本語", "🇯🇵")
}

enum class CurrencyUnit(val symbol: String, val rateFromUsd: Double, val code: String) {
    USD("$", 1.0, "USD"),
    EUR("€", 0.92, "EUR"),
    INR("₹", 86.5, "INR"),
    GBP("£", 0.79, "GBP"),
    JPY("¥", 152.0, "JPY")
}
