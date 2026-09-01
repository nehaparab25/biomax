package com.example.biomax.model

enum class AppThemeMode(val title: String) {
    DARK("Dark Obsidian"),
    LIGHT("Light Pearl"),
    SYSTEM("System Default")
}

enum class ThemePalette(
    val title: String,
    val subtitle: String,
    val primaryHex: Long,
    val secondaryHex: Long
) {
    BIO_EMERALD("Bio Emerald", "Eco-Tech Circular", 0xFF00E575, 0xFF00D2FF),
    CYBER_CYAN("Cyber Cyan", "Clean Power Grid", 0xFF00D2FF, 0xFF10E57A),
    SOLAR_AMBER("Solar Amber", "Thermal & Methane", 0xFFFFB800, 0xFFFF6B00),
    FOREST_MOSS("Forest Moss", "Organic Agronomy", 0xFF4ADE80, 0xFFA3E635),
    DEEP_VIOLET("Deep Violet", "Cryptographic Escrow", 0xFFC084FC, 0xFF38BDF8),
    NEON_LIME("Neon Lime", "High Kinetic Energy", 0xFFA3E635, 0xFF00D2FF)
}

enum class WeightUnit(val title: String, val smallUnit: String, val bulkUnit: String, val toKgRatio: Double) {
    METRIC_KG_TONS("Metric (kg / Ton)", "kg", "Tons", 1.0),
    IMPERIAL_LBS_TONS("Imperial (lbs / US Ton)", "lbs", "US Tons", 0.453592)
}

enum class EnergyUnit(val title: String, val smallUnit: String, val bulkUnit: String, val toKwhRatio: Double) {
    KWH_MWH("Clean Electrical (kWh / MWh)", "kWh", "MWh", 1.0),
    BTU_THERMS("Thermal Heat (kBTU / Therms)", "kBTU", "Therms", 0.293071)
}

enum class GasVolumeUnit(val title: String, val unitSymbol: String, val toM3Ratio: Double) {
    M3_CUBIC_METERS("Cubic Meters (m³)", "m³", 1.0),
    FT3_CUBIC_FEET("Cubic Feet (ft³)", "ft³", 0.0283168)
}

enum class TelemetryRefreshRate(val title: String, val intervalSeconds: Int) {
    REALTIME_2S("Realtime High Frequency (2s)", 2),
    STANDARD_5S("Standard Telemetry (5s)", 5),
    POWERSAVE_15S("Power Saver (15s)", 15)
}

enum class FleetSimulationSpeed(val title: String, val speedMultiplier: Double) {
    FAST_2X("Accelerated Dispatch (2x)", 2.0),
    REALISTIC_1X("Standard Speed (1x)", 1.0),
    MANUAL_STEP("Manual Step Only", 0.0)
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.DARK,
    val themePalette: ThemePalette = ThemePalette.BIO_EMERALD,
    val dynamicColor: Boolean = false,
    val weightUnit: WeightUnit = WeightUnit.METRIC_KG_TONS,
    val energyUnit: EnergyUnit = EnergyUnit.KWH_MWH,
    val volumeUnit: GasVolumeUnit = GasVolumeUnit.M3_CUBIC_METERS,
    val telemetryRefreshRate: TelemetryRefreshRate = TelemetryRefreshRate.STANDARD_5S,
    val fleetSimulationSpeed: FleetSimulationSpeed = FleetSimulationSpeed.REALISTIC_1X,
    val autoEscrowRelease: Boolean = true,
    val spoilageAlertThresholdC: Double = 24.0,
    val moistureAlertThresholdPercent: Double = 85.0,
    val compactCardView: Boolean = false,
    val soundAndHapticFeedback: Boolean = true,
    val simulatedSensorJitter: Boolean = true,
    val auditLoggingLevel: String = "Verbose (All Transactions)",
    val defaultStorageType: StorageContainerType = StorageContainerType.CHILLED_ORGANIC_DRUM,
    val defaultFreshnessGrade: FreshnessGrade = FreshnessGrade.GRADE_A,
    val defaultPricePerKg: Double = 0.18,
    val isBiometricLockEnabled: Boolean = false
)
