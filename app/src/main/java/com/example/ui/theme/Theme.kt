package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.biomax.model.AppThemeMode
import com.example.biomax.model.ThemePalette

// ==========================================
// 1. BIO EMERALD (Eco-Tech Circular)
// ==========================================
private val DarkBioEmeraldScheme = darkColorScheme(
    primary = BioEmeraldBright,
    onPrimary = Color(0xFF042111),
    primaryContainer = Color(0xFF083C21),
    onPrimaryContainer = Color(0xFF86F3B1),
    secondary = BioCyanCyber,
    onSecondary = Color(0xFF003541),
    secondaryContainer = Color(0xFF004E5F),
    onSecondaryContainer = Color(0xFFB8EFFF),
    tertiary = BioAmberMethane,
    onTertiary = Color(0xFF3E2800),
    tertiaryContainer = Color(0xFF5A3C00),
    onTertiaryContainer = Color(0xFFFFDF9E),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    errorContainer = Color(0xFF580B0F),
    onErrorContainer = Color(0xFFFFB4AB),
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkBorder,
    outlineVariant = DarkBorderHighlight
)

private val LightBioEmeraldScheme = lightColorScheme(
    primary = BioEmeraldDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB6F7CE),
    onPrimaryContainer = Color(0xFF00391A),
    secondary = Color(0xFF00687A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBFF),
    onSecondaryContainer = Color(0xFF001F26),
    tertiary = Color(0xFF825A00),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE0A0),
    onTertiaryContainer = Color(0xFF291A00),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightBorder,
    outlineVariant = LightBorderHighlight
)

// ==========================================
// 2. CYBER CYAN (Clean Power Grid)
// ==========================================
private val DarkCyberCyanScheme = darkColorScheme(
    primary = BioCyanCyber,
    onPrimary = Color(0xFF003541),
    primaryContainer = Color(0xFF004D60),
    onPrimaryContainer = Color(0xFFB8EFFF),
    secondary = BioEmeraldBright,
    onSecondary = Color(0xFF042111),
    secondaryContainer = Color(0xFF083C21),
    onSecondaryContainer = Color(0xFF86F3B1),
    tertiary = Color(0xFF38BDF8),
    onTertiary = Color(0xFF003544),
    tertiaryContainer = Color(0xFF004E64),
    onTertiaryContainer = Color(0xFFBEE9FF),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    errorContainer = Color(0xFF580B0F),
    onErrorContainer = Color(0xFFFFB4AB),
    background = Color(0xFF070E13),
    onBackground = Color(0xFFF0F6FA),
    surface = Color(0xFF0E1A22),
    onSurface = Color(0xFFF0F6FA),
    surfaceVariant = Color(0xFF162733),
    onSurfaceVariant = Color(0xFFA2B6C4),
    outline = Color(0xFF203B4D),
    outlineVariant = Color(0xFF305770)
)

private val LightCyberCyanScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBAE6FD),
    onPrimaryContainer = Color(0xFF003544),
    secondary = Color(0xFF008744),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB6F7CE),
    onSecondaryContainer = Color(0xFF00391A),
    tertiary = Color(0xFF00668B),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFC2E8FF),
    onTertiaryContainer = Color(0xFF001E2C),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFF3F7FA),
    onBackground = Color(0xFF0F181F),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F181F),
    surfaceVariant = Color(0xFFE4EDF2),
    onSurfaceVariant = Color(0xFF40535F),
    outline = Color(0xFFCAD7E0),
    outlineVariant = Color(0xFFA6BDCC)
)

// ==========================================
// 3. SOLAR AMBER (Methane & Thermal Flame)
// ==========================================
private val DarkSolarAmberScheme = darkColorScheme(
    primary = BioAmberMethane,
    onPrimary = Color(0xFF3E2800),
    primaryContainer = Color(0xFF5B3C00),
    onPrimaryContainer = Color(0xFFFFDF9E),
    secondary = BioOrangeThermal,
    onSecondary = Color(0xFF421C00),
    secondaryContainer = Color(0xFF632B00),
    onSecondaryContainer = Color(0xFFFFDBC9),
    tertiary = BioEmeraldBright,
    onTertiary = Color(0xFF042111),
    tertiaryContainer = Color(0xFF083C21),
    onTertiaryContainer = Color(0xFF86F3B1),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    background = Color(0xFF120E08),
    onBackground = Color(0xFFFAF5ED),
    surface = Color(0xFF1E170E),
    onSurface = Color(0xFFFAF5ED),
    surfaceVariant = Color(0xFF2C2216),
    onSurfaceVariant = Color(0xFFC7B8A6),
    outline = Color(0xFF463724),
    outlineVariant = Color(0xFF675238)
)

private val LightSolarAmberScheme = lightColorScheme(
    primary = BioAmberDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0A0),
    onPrimaryContainer = Color(0xFF291A00),
    secondary = Color(0xFFC2410C),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBC9),
    onSecondaryContainer = Color(0xFF3B1000),
    tertiary = Color(0xFF008744),
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFFAF7F2),
    onBackground = Color(0xFF1E160C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E160C),
    surfaceVariant = Color(0xFFF2EAE0),
    onSurfaceVariant = Color(0xFF5A4D3E),
    outline = Color(0xFFE2D6C6),
    outlineVariant = Color(0xFFC8B8A2)
)

// ==========================================
// 4. FOREST MOSS (Organic Agronomy)
// ==========================================
private val DarkForestMossScheme = darkColorScheme(
    primary = BioMossGreen,
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF005321),
    onPrimaryContainer = Color(0xFF8BF5A8),
    secondary = BioLimeNeon,
    onSecondary = Color(0xFF223600),
    secondaryContainer = Color(0xFF344F00),
    onSecondaryContainer = Color(0xFFBEF376),
    tertiary = BioCyanCyber,
    onTertiary = Color(0xFF003541),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    background = Color(0xFF080F0A),
    onBackground = Color(0xFFF1F7F2),
    surface = Color(0xFF101C14),
    onSurface = Color(0xFFF1F7F2),
    surfaceVariant = Color(0xFF1A2A20),
    onSurfaceVariant = Color(0xFFA5B8AC),
    outline = Color(0xFF273E30),
    outlineVariant = Color(0xFF395946)
)

private val LightForestMossScheme = lightColorScheme(
    primary = Color(0xFF15803D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB5F4C7),
    onPrimaryContainer = Color(0xFF00210A),
    secondary = Color(0xFF4D7C0F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD6F59D),
    onSecondaryContainer = Color(0xFF162500),
    tertiary = Color(0xFF0284C7),
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFF3F8F4),
    onBackground = Color(0xFF0E1A11),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0E1A11),
    surfaceVariant = Color(0xFFE2EFE6),
    onSurfaceVariant = Color(0xFF425649),
    outline = Color(0xFFC8DEC9),
    outlineVariant = Color(0xFFA4C4A6)
)

// ==========================================
// 5. DEEP VIOLET (Cryptographic Escrow)
// ==========================================
private val DarkDeepVioletScheme = darkColorScheme(
    primary = Color(0xFFC084FC),
    onPrimary = Color(0xFF3B0764),
    primaryContainer = Color(0xFF581C87),
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = BioCyanCyber,
    onSecondary = Color(0xFF003541),
    secondaryContainer = Color(0xFF004D60),
    onSecondaryContainer = Color(0xFFB8EFFF),
    tertiary = BioEmeraldBright,
    onTertiary = Color(0xFF042111),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    background = Color(0xFF0D0A14),
    onBackground = Color(0xFFF5F1FA),
    surface = Color(0xFF161122),
    onSurface = Color(0xFFF5F1FA),
    surfaceVariant = Color(0xFF221A33),
    onSurfaceVariant = Color(0xFFB3A8C6),
    outline = Color(0xFF352B4D),
    outlineVariant = Color(0xFF4E4070)
)

private val LightDeepVioletScheme = lightColorScheme(
    primary = Color(0xFF7E22CE),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8FF),
    onPrimaryContainer = Color(0xFF3B0764),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBAE6FD),
    onSecondaryContainer = Color(0xFF003544),
    tertiary = Color(0xFF008744),
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFF8F5FB),
    onBackground = Color(0xFF181223),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF181223),
    surfaceVariant = Color(0xFFECE4F5),
    onSurfaceVariant = Color(0xFF524467),
    outline = Color(0xFFD8CADF),
    outlineVariant = Color(0xFFBBA7C7)
)

// ==========================================
// 6. NEON LIME (High Kinetic Grid)
// ==========================================
private val DarkNeonLimeScheme = darkColorScheme(
    primary = BioLimeNeon,
    onPrimary = Color(0xFF223600),
    primaryContainer = Color(0xFF344F00),
    onPrimaryContainer = Color(0xFFD6F59D),
    secondary = BioCyanCyber,
    onSecondary = Color(0xFF003541),
    secondaryContainer = Color(0xFF004D60),
    onSecondaryContainer = Color(0xFFB8EFFF),
    tertiary = BioAmberMethane,
    onTertiary = Color(0xFF3E2800),
    error = BioRedAlert,
    onError = Color(0xFF380002),
    background = Color(0xFF0A0F06),
    onBackground = Color(0xFFF4F8F0),
    surface = Color(0xFF121B0D),
    onSurface = Color(0xFFF4F8F0),
    surfaceVariant = Color(0xFF1C2A15),
    onSurfaceVariant = Color(0xFFA9BBA0),
    outline = Color(0xFF2B3F21),
    outlineVariant = Color(0xFF405D32)
)

private val LightNeonLimeScheme = lightColorScheme(
    primary = Color(0xFF4D7C0F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6F59D),
    onPrimaryContainer = Color(0xFF162500),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBAE6FD),
    onSecondaryContainer = Color(0xFF003544),
    tertiary = Color(0xFFB45309),
    onTertiary = Color.White,
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    background = Color(0xFFF5F9F3),
    onBackground = Color(0xFF10190C),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF10190C),
    surfaceVariant = Color(0xFFE5EFE0),
    onSurfaceVariant = Color(0xFF45573E),
    outline = Color(0xFFCCDDC5),
    outlineVariant = Color(0xFFA8C49F)
)

@Composable
fun BiomaxTheme(
    themeMode: AppThemeMode = AppThemeMode.DARK,
    themePalette: ThemePalette = ThemePalette.BIO_EMERALD,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
        AppThemeMode.SYSTEM -> isSystemDark
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        else -> {
            when (themePalette) {
                ThemePalette.BIO_EMERALD -> if (isDark) DarkBioEmeraldScheme else LightBioEmeraldScheme
                ThemePalette.CYBER_CYAN -> if (isDark) DarkCyberCyanScheme else LightCyberCyanScheme
                ThemePalette.SOLAR_AMBER -> if (isDark) DarkSolarAmberScheme else LightSolarAmberScheme
                ThemePalette.FOREST_MOSS -> if (isDark) DarkForestMossScheme else LightForestMossScheme
                ThemePalette.DEEP_VIOLET -> if (isDark) DarkDeepVioletScheme else LightDeepVioletScheme
                ThemePalette.NEON_LIME -> if (isDark) DarkNeonLimeScheme else LightNeonLimeScheme
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    BiomaxTheme(
        themeMode = if (darkTheme) AppThemeMode.DARK else AppThemeMode.LIGHT,
        themePalette = ThemePalette.BIO_EMERALD,
        dynamicColor = dynamicColor,
        content = content
    )
}
