package com.example.biomax.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class SliderStyle {
    FLUID_GLOW,
    SOLID_BAR
}

/**
 * Modern Fluid & Solid Bar Custom Slider
 * Eliminates standard clunky sliders in favor of fluid glowing gradient bars
 * or tactile solid segmented bars with smooth draggable interaction and live value badges.
 */
@Composable
fun FluidSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float> = 0f..100f,
    label: String = "",
    valueDisplay: String = "",
    unit: String = "",
    sliderStyle: SliderStyle = SliderStyle.FLUID_GLOW,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    trackHeight: Dp = 16.dp,
    modifier: Modifier = Modifier,
    testTag: String = "fluid_slider"
) {
    val normalizedValue = remember(value, valueRange) {
        val rangeSpan = valueRange.endInclusive - valueRange.start
        if (rangeSpan <= 0f) 0f
        else ((value - valueRange.start) / rangeSpan).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = normalizedValue,
        label = "slider_progress_anim"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
    ) {
        // Label & Current Value Row
        if (label.isNotEmpty() || valueDisplay.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (label.isNotEmpty()) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.12f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = valueDisplay.ifEmpty { "${value.toInt()}" },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor
                    )
                    if (unit.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = unit,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = accentColor.copy(alpha = 0.85f)
                        )
                    }
                }
            }
        }

        // Draggable Fluid Track
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight + 8.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val totalWidthPx = constraints.maxWidth.toFloat()

            fun calculateValueFromOffset(x: Float) {
                if (totalWidthPx > 0) {
                    val fraction = (x / totalWidthPx).coerceIn(0f, 1f)
                    val newValue = valueRange.start + fraction * (valueRange.endInclusive - valueRange.start)
                    onValueChange(newValue)
                }
            }

            val isDark = MaterialTheme.colorScheme.background.red < 0.5f
            val trackBackground = if (isDark) {
                Color.White.copy(alpha = 0.08f)
            } else {
                Color.Black.copy(alpha = 0.06f)
            }

            // Outer Base Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(RoundedCornerShape(trackHeight / 2))
                    .background(trackBackground)
                    .border(
                        1.dp,
                        if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f),
                        RoundedCornerShape(trackHeight / 2)
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            calculateValueFromOffset(offset.x)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            calculateValueFromOffset(change.position.x)
                        }
                    }
            ) {
                if (sliderStyle == SliderStyle.FLUID_GLOW) {
                    // Fluid glowing gradient fill
                    val fluidGradient = Brush.horizontalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.7f),
                            accentColor,
                            MaterialTheme.colorScheme.tertiary
                        )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = animatedProgress.coerceIn(0.01f, 1f))
                            .clip(RoundedCornerShape(trackHeight / 2))
                            .background(fluidGradient)
                    )
                } else {
                    // Solid bar with sleek clean fill
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = animatedProgress.coerceIn(0.01f, 1f))
                            .clip(RoundedCornerShape(trackHeight / 2))
                            .background(accentColor)
                    )
                }
            }

            // Tactile Fluid Glowing Thumb
            val thumbSize = trackHeight + 6.dp
            val thumbOffset = ((maxWidth - thumbSize) * animatedProgress).coerceAtLeast(0.dp)

            Box(
                modifier = Modifier
                    .offset(x = thumbOffset)
                    .size(thumbSize)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(2.5.dp, accentColor, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            val currentX = (thumbOffset.toPx() + change.position.x)
                            calculateValueFromOffset(currentX)
                        }
                    }
            )
        }
    }
}
