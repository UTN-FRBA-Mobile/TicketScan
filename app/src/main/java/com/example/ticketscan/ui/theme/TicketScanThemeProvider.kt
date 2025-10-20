package com.example.ticketscan.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.ticketscan.domain.model.AppearancePreferences
import com.example.ticketscan.domain.model.ThemeMode

// Use centralized color tokens from Color.kt
private val LightColors = TicketScanColors(
    primary = Primary,
    secondary = Secondary,
    surface = Surface,
    onPrimary = Color.White,
    onSurface = OnSurface,
    background = Background,
    onBackground = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    primaryContainer = PrimaryContainer,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    error = Error,
    success = Success,
    successContainer = SuccessContainer,
    onSuccessContainer = OnSuccessContainer,
    info = Info,
    infoContainer = InfoContainer,
    onInfoContainer = OnInfoContainer
)

private val DarkColors = TicketScanColors(
    primary = Color(0xFF4AD9BF),
    secondary = Color(0xFF94A3B8),
    surface = Color(0xFF071A18),
    onPrimary = Color(0xFF00352E),
    onSurface = Color(0xFFECFDF9),
    background = Color(0xFF04100E),
    onBackground = Color(0xFFECFDF9),
    surfaceVariant = Color(0xFF0D2420),
    onSurfaceVariant = Color(0xFF77ADA2),
    outline = Color(0xFF244640),
    primaryContainer = Color(0xFF0B4F45),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFB4A9),
    error = Error,
    success = Color(0xFF4AD9BF),
    successContainer = Color(0xFF12584D),
    onSuccessContainer = Color(0xFF9AEED9),
    info = Color(0xFF5CA8FF),
    infoContainer = Color(0xFF123B7A),
    onInfoContainer = Color(0xFFCEE0FF)
)

private val BaseTypography = TicketScanTypography
private val DefaultSpacing = TicketScanSpacing()
// More prominent rounded shapes for a modern, friendly look
private val DefaultShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp)
)

private val LightMaterialColorScheme = lightColorScheme(
    primary = LightColors.primary,
    onPrimary = LightColors.onPrimary,
    primaryContainer = LightColors.primaryContainer,
    onPrimaryContainer = LightColors.onSurface,
    secondary = LightColors.secondary,
    secondaryContainer = LightColors.surfaceVariant,
    onSecondaryContainer = LightColors.onSurfaceVariant,
    surface = LightColors.surface,
    onSurface = LightColors.onSurface,
    surfaceVariant = LightColors.surfaceVariant,
    onSurfaceVariant = LightColors.onSurfaceVariant,
    background = LightColors.background,
    onBackground = LightColors.onBackground,
    error = LightColors.error,
    errorContainer = LightColors.errorContainer,
    onErrorContainer = LightColors.onErrorContainer,
    outline = LightColors.outline
)

private val DarkMaterialColorScheme = darkColorScheme(
    primary = DarkColors.primary,
    onPrimary = DarkColors.onPrimary,
    primaryContainer = DarkColors.primaryContainer,
    onPrimaryContainer = DarkColors.onSurface,
    secondary = DarkColors.secondary,
    secondaryContainer = DarkColors.surfaceVariant,
    onSecondaryContainer = DarkColors.onSurfaceVariant,
    surface = DarkColors.surface,
    onSurface = DarkColors.onSurface,
    surfaceVariant = DarkColors.surfaceVariant,
    onSurfaceVariant = DarkColors.onSurfaceVariant,
    background = DarkColors.background,
    onBackground = DarkColors.onBackground,
    error = DarkColors.error,
    errorContainer = DarkColors.errorContainer,
    onErrorContainer = DarkColors.onErrorContainer,
    outline = DarkColors.outline
)

@Composable
fun TicketScanThemeProvider(
    appearance: AppearancePreferences = AppearancePreferences(),
    content: @Composable () -> Unit
) {
    val isDark = appearance.themeMode == ThemeMode.DARK
    val colors = if (isDark) DarkColors else LightColors
    val materialColorScheme = if (isDark) DarkMaterialColorScheme else LightMaterialColorScheme
    val typography = BaseTypography.withScale(appearance.fontScale.scaleFactor)

    TicketScanTheme(
        colors = colors,
        typography = typography,
        shapes = DefaultShapes,
        spacing = DefaultSpacing
    ) {
        MaterialTheme(
            colorScheme = materialColorScheme,
            typography = typography,
            shapes = DefaultShapes,
            content = content
        )
    }
}
