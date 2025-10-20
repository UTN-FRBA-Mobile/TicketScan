package com.example.ticketscan.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

/**
 * Returns a copy of this typography with all font sizes scaled by [scale].
 */
internal fun Typography.withScale(scale: Float): Typography {
    fun TextUnit.scaled(): TextUnit = when {
        this == TextUnit.Unspecified -> this
        else -> TextUnit(value * scale, type)
    }

    fun TextStyle.scaleStyle(): TextStyle = copy(
        fontSize = fontSize.scaled(),
        lineHeight = lineHeight.scaled(),
        letterSpacing = letterSpacing.scaled()
    )

    return Typography(
        displayLarge = displayLarge.scaleStyle(),
        displayMedium = displayMedium.scaleStyle(),
        displaySmall = displaySmall.scaleStyle(),
        headlineLarge = headlineLarge.scaleStyle(),
        headlineMedium = headlineMedium.scaleStyle(),
        headlineSmall = headlineSmall.scaleStyle(),
        titleLarge = titleLarge.scaleStyle(),
        titleMedium = titleMedium.scaleStyle(),
        titleSmall = titleSmall.scaleStyle(),
        bodyLarge = bodyLarge.scaleStyle(),
        bodyMedium = bodyMedium.scaleStyle(),
        bodySmall = bodySmall.scaleStyle(),
        labelLarge = labelLarge.scaleStyle(),
        labelMedium = labelMedium.scaleStyle(),
        labelSmall = labelSmall.scaleStyle()
    )
}
