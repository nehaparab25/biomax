package com.example.biomax.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Glassmorphic & Fluid Styling Extensions and Components
 * Creates translucent, frosted glass aesthetics with soft glows,
 * hairline specular highlights, and fluid minimalist layouts.
 */

fun Modifier.glassmorphic(
    shape: Shape = RoundedCornerShape(20.dp),
    elevation: Dp = 0.dp
): Modifier = this
    .then(if (elevation > 0.dp) Modifier.shadow(elevation, shape) else Modifier)
    .clip(shape)

@Composable
fun GlassmorphicSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    borderGlowColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    tonalAlpha: Float = 0.72f,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = MaterialTheme.colorScheme.background.red < 0.5f
    val surfaceColor = MaterialTheme.colorScheme.surface
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    val surfaceBase = if (isDark) {
        surfaceColor.copy(alpha = tonalAlpha)
    } else {
        surfaceColor.copy(alpha = tonalAlpha + 0.15f)
    }

    val glassGradient = remember(isDark, surfaceBase, surfaceVariantColor) {
        if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    surfaceBase,
                    surfaceVariantColor.copy(alpha = tonalAlpha * 0.7f),
                    surfaceBase.copy(alpha = tonalAlpha * 0.85f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.85f),
                    surfaceBase,
                    surfaceVariantColor.copy(alpha = 0.65f)
                )
            )
        }
    }

    val specularBorder = remember(isDark, borderGlowColor, outlineVariantColor) {
        if (isDark) {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.22f),
                    borderGlowColor,
                    Color.White.copy(alpha = 0.05f)
                )
            )
        } else {
            Brush.linearGradient(
                colors = listOf(
                    Color.White.copy(alpha = 0.9f),
                    borderGlowColor,
                    outlineVariantColor.copy(alpha = 0.4f)
                )
            )
        }
    }

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(),
            onClick = onClick
        )
    } else Modifier

    Box(
        modifier = modifier
            .clip(shape)
            .background(glassGradient)
            .border(BorderStroke(1.dp, specularBorder), shape)
            .then(clickModifier)
    ) {
        content()
    }
}

@Composable
fun FluidMetricGlyph(
    icon: ImageVector,
    value: String,
    unit: String = "",
    accentColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.10f))
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (unit.isNotEmpty()) {
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = unit,
                fontSize = 9.sp,
                fontWeight = FontWeight.SemiBold,
                color = accentColor
            )
        }
    }
}

@Composable
fun FluidCapsuleBadge(
    text: String,
    icon: ImageVector? = null,
    color: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.28f), CircleShape)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(11.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}
