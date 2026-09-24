package com.example.dailyshoppinglist.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.dailyshoppinglist.data.settings.AccentColor

/** iOS-style semantic colors used throughout the app. */
@Immutable
data class AppColors(
    val isDark: Boolean,
    val accent: Color,
    val groupedBackground: Color,
    val card: Color,
    val elevatedCard: Color,
    val label: Color,
    val secondaryLabel: Color,
    val tertiaryLabel: Color,
    val separator: Color,
    val fill: Color,
    val destructive: Color,
    val success: Color,
)

fun lightAppColors(accent: AccentColor) = AppColors(
    isDark = false,
    accent = Color(accent.light),
    groupedBackground = Color(0xFFF2F2F7),
    card = Color(0xFFFFFFFF),
    elevatedCard = Color(0xFFFFFFFF),
    label = Color(0xFF000000),
    secondaryLabel = Color(0x993C3C43),
    tertiaryLabel = Color(0x4D3C3C43),
    separator = Color(0x4A3C3C43),
    fill = Color(0x1F767680),
    destructive = Color(0xFFFF3B30),
    success = Color(0xFF34C759),
)

fun darkAppColors(accent: AccentColor) = AppColors(
    isDark = true,
    accent = Color(accent.dark),
    groupedBackground = Color(0xFF000000),
    card = Color(0xFF1C1C1E),
    elevatedCard = Color(0xFF2C2C2E),
    label = Color(0xFFFFFFFF),
    secondaryLabel = Color(0x99EBEBF5),
    tertiaryLabel = Color(0x4DEBEBF5),
    separator = Color(0xA6545458),
    fill = Color(0x3D767680),
    destructive = Color(0xFFFF453A),
    success = Color(0xFF30D158),
)

val LocalAppColors = staticCompositionLocalOf { lightAppColors(AccentColor.BLUE) }
