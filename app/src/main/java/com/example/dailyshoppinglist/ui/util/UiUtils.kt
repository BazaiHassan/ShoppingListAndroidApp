package com.example.dailyshoppinglist.ui.util

import android.icu.text.DateFormat
import android.icu.util.ULocale
import androidx.annotation.PluralsRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.ui.theme.LocalHapticsEnabled
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

/** Maximum width of reading content on tablets, foldables and desktop windows. */
val MaxContentWidth = 720.dp

/** Horizontal padding that keeps content centred and readable on wide screens. */
fun sidePadding(maxWidth: Dp, limit: Dp = MaxContentWidth, minimum: Dp = 16.dp): Dp =
    maxOf(minimum, (maxWidth - limit) / 2)

@Composable
@ReadOnlyComposable
fun currentLocale(): Locale = LocalConfiguration.current.locales[0] ?: Locale.getDefault()

@Composable
fun quantityString(@PluralsRes id: Int, count: Int, vararg args: Any): String {
    LocalConfiguration.current // re-read when the locale changes
    return LocalContext.current.resources.getQuantityString(id, count, *args)
}

/** Light tick feedback that respects the user's haptics setting. */
@Composable
fun rememberHaptic(): () -> Unit {
    val haptics = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    return remember(haptics, enabled) {
        { if (enabled) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove) }
    }
}

/** Locale-aware formatting. Persian uses the Solar Hijri calendar. */
object Formats {
    private fun uLocale(locale: Locale): ULocale =
        if (locale.language == "fa") ULocale("fa_IR@calendar=persian") else ULocale.forLocale(locale)

    private fun format(skeleton: String, millis: Long, locale: Locale): String =
        DateFormat.getInstanceForSkeleton(skeleton, uLocale(locale)).format(Date(millis))

    fun dateTime(millis: Long, locale: Locale) = format("EEEEdMMMMjmm", millis, locale)
    fun date(millis: Long, locale: Locale) = format("EEEEdMMMMy", millis, locale)
    fun shortDate(millis: Long, locale: Locale) = format("EEEdMMM", millis, locale)
    fun time(millis: Long, locale: Locale) = format("jmm", millis, locale)
    fun monthYear(millis: Long, locale: Locale) = format("yMMMM", millis, locale)

    fun number(value: Int, locale: Locale): String = NumberFormat.getIntegerInstance(locale).format(value)
    fun percent(fraction: Float, locale: Locale): String = NumberFormat.getPercentInstance(locale).format(fraction)
}
