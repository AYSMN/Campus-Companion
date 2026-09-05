package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Core Glass Colors
val GlassWhiteHigh = Color(0x33FFFFFF)
val GlassWhiteMid = Color(0x24FFFFFF)
val GlassWhiteLow = Color(0x14FFFFFF)
val GlassWhiteBorder = Color(0x40FFFFFF)

val GlassDarkHigh = Color(0x66111827)
val GlassDarkMid = Color(0x401F2937)
val GlassDarkLow = Color(0x2B111827)
val GlassDarkBorder = Color(0x33FFFFFF)

// Accent Colors
val ElectricSapphire = Color(0xFF3B82F6)
val NeonCyan = Color(0xFF06B6D4)
val EmeraldMint = Color(0xFF10B981)
val AmberGold = Color(0xFFF59E0B)
val SunsetRose = Color(0xFFF43F5E)
val VioletPurple = Color(0xFF8B5CF6)
val IndigoDeep = Color(0xFF6366F1)

// Time of Day Gradients
object TimeGradients {
    // Morning (5am - 12pm): Golden sunrise, peach, soft lavender
    val MorningLight = listOf(
        Color(0xFFFFF7ED),
        Color(0xFFFEF3C7),
        Color(0xFFFDE68A),
        Color(0xFFE0E7FF)
    )
    val MorningDark = listOf(
        Color(0xFF1E1B4B),
        Color(0xFF31103F),
        Color(0xFF451A03),
        Color(0xFF0F172A)
    )

    // Afternoon (12pm - 5pm): Bright sky, cyan, ocean
    val AfternoonLight = listOf(
        Color(0xFFEFF6FF),
        Color(0xFFE0F2FE),
        Color(0xFFCCFBF1),
        Color(0xFFF0FDF4)
    )
    val AfternoonDark = listOf(
        Color(0xFF022C22),
        Color(0xFF0C4A6E),
        Color(0xFF1E1B4B),
        Color(0xFF020617)
    )

    // Evening (5pm - 9pm): Sunset magenta, violet, twilight indigo
    val EveningLight = listOf(
        Color(0xFFFAF5FF),
        Color(0xFFFCE7F3),
        Color(0xFFFFEDD5),
        Color(0xFFEDE9FE)
    )
    val EveningDark = listOf(
        Color(0xFF2E1065),
        Color(0xFF4A044E),
        Color(0xFF431407),
        Color(0xFF090D16)
    )

    // Night (9pm - 5am): Cosmic sapphire, deep obsidian, nebula
    val NightLight = listOf(
        Color(0xFFF1F5F9),
        Color(0xFFE2E8F0),
        Color(0xFFDDD6FE),
        Color(0xFFCFFAFE)
    )
    val NightDark = listOf(
        Color(0xFF030712),
        Color(0xFF0B1329),
        Color(0xFF111827),
        Color(0xFF020617)
    )
}
