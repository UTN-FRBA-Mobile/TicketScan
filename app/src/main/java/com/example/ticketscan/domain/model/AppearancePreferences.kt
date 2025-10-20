package com.example.ticketscan.domain.model

/**
 * User-configurable appearance preferences shared across the app.
 */
 data class AppearancePreferences(
     val themeMode: ThemeMode = ThemeMode.LIGHT,
     val fontScale: FontScale = FontScale.MEDIUM
 ) {
     val isDarkTheme: Boolean get() = themeMode == ThemeMode.DARK
 }

 enum class ThemeMode {
     LIGHT,
     DARK
 }

 enum class FontScale(val scaleFactor: Float) {
     SMALL(0.9f),
     MEDIUM(1.0f),
     LARGE(1.15f)
 }
