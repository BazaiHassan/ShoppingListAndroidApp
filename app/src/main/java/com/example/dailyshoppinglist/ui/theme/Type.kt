package com.example.dailyshoppinglist.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.dailyshoppinglist.R

val Vazir = FontFamily(Font(R.font.vazir, FontWeight.Normal))

private fun style(size: TextUnit, lineHeight: TextUnit, weight: FontWeight = FontWeight.Normal) =
    TextStyle(fontFamily = Vazir, fontWeight = weight, fontSize = size, lineHeight = lineHeight)

/** Sizes follow Apple's Human Interface Guidelines text styles. */
val AppTypography = Typography(
    displayLarge = style(40.sp, 52.sp, FontWeight.Bold),
    displayMedium = style(36.sp, 46.sp, FontWeight.Bold),
    displaySmall = style(34.sp, 44.sp, FontWeight.Bold), // Large Title
    headlineLarge = style(30.sp, 40.sp, FontWeight.Bold),
    headlineMedium = style(30.sp, 40.sp, FontWeight.Bold), // Large top app bar title
    headlineSmall = style(22.sp, 30.sp, FontWeight.Bold), // Title 2
    titleLarge = style(17.sp, 24.sp, FontWeight.SemiBold), // Headline / collapsed bar title
    titleMedium = style(17.sp, 24.sp, FontWeight.SemiBold),
    titleSmall = style(15.sp, 22.sp, FontWeight.SemiBold),
    bodyLarge = style(17.sp, 26.sp), // Body
    bodyMedium = style(15.sp, 22.sp), // Subheadline
    bodySmall = style(13.sp, 19.sp), // Footnote
    labelLarge = style(15.sp, 20.sp, FontWeight.SemiBold),
    labelMedium = style(13.sp, 18.sp, FontWeight.Medium),
    labelSmall = style(11.sp, 15.sp, FontWeight.Medium), // Caption 2
)
