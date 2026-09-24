package com.example.dailyshoppinglist.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.data.settings.AccentColor
import com.example.dailyshoppinglist.data.settings.ThemeMode

val LocalHapticsEnabled = staticCompositionLocalOf { true }

@Composable
fun ThemeMode.isDark(): Boolean = when (this) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@Composable
fun ShoppingTheme(
    themeMode: ThemeMode,
    accent: AccentColor,
    hapticsEnabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val dark = themeMode.isDark()
    val colors = remember(dark, accent) { if (dark) darkAppColors(accent) else lightAppColors(accent) }
    val scheme = remember(colors) { colors.toColorScheme() }
    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalHapticsEnabled provides hapticsEnabled,
    ) {
        MaterialTheme(colorScheme = scheme, typography = AppTypography, shapes = AppShapes, content = content)
    }
}

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

private fun AppColors.toColorScheme() = if (isDark) {
    darkColorScheme(
        primary = accent,
        onPrimary = Color.White,
        primaryContainer = accent.copy(alpha = 0.22f).compositeOver(card),
        onPrimaryContainer = accent,
        secondary = accent,
        onSecondary = Color.White,
        secondaryContainer = accent.copy(alpha = 0.22f).compositeOver(card),
        onSecondaryContainer = accent,
        tertiary = accent,
        background = groupedBackground,
        onBackground = label,
        surface = groupedBackground,
        onSurface = label,
        surfaceVariant = fill.compositeOver(card),
        onSurfaceVariant = secondaryLabel.compositeOver(card),
        surfaceContainerLowest = card,
        surfaceContainerLow = card,
        surfaceContainer = card,
        surfaceContainerHigh = elevatedCard,
        surfaceContainerHighest = elevatedCard,
        surfaceTint = Color.Transparent,
        outline = tertiaryLabel.compositeOver(card),
        outlineVariant = separator.compositeOver(card),
        error = destructive,
        onError = Color.White,
        inverseSurface = Color(0xFFE5E5EA),
        inverseOnSurface = Color.Black,
        inversePrimary = accent,
        scrim = Color.Black.copy(alpha = 0.5f),
    )
} else {
    lightColorScheme(
        primary = accent,
        onPrimary = Color.White,
        primaryContainer = accent.copy(alpha = 0.14f).compositeOver(card),
        onPrimaryContainer = accent,
        secondary = accent,
        onSecondary = Color.White,
        secondaryContainer = accent.copy(alpha = 0.14f).compositeOver(card),
        onSecondaryContainer = accent,
        tertiary = accent,
        background = groupedBackground,
        onBackground = label,
        surface = groupedBackground,
        onSurface = label,
        surfaceVariant = fill.compositeOver(card),
        onSurfaceVariant = secondaryLabel.compositeOver(card),
        surfaceContainerLowest = card,
        surfaceContainerLow = card,
        surfaceContainer = card,
        surfaceContainerHigh = elevatedCard,
        surfaceContainerHighest = elevatedCard,
        surfaceTint = Color.Transparent,
        outline = tertiaryLabel.compositeOver(card),
        outlineVariant = separator.compositeOver(card),
        error = destructive,
        onError = Color.White,
        inverseSurface = Color(0xFF2C2C2E),
        inverseOnSurface = Color.White,
        inversePrimary = accent,
        scrim = Color.Black.copy(alpha = 0.4f),
    )
}
