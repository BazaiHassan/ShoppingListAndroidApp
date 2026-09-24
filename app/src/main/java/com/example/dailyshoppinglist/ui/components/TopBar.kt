package com.example.dailyshoppinglist.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import com.example.dailyshoppinglist.ui.theme.LocalAppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun appTopBarColors(): TopAppBarColors {
    val colors = LocalAppColors.current
    return TopAppBarDefaults.largeTopAppBarColors(
        containerColor = colors.groupedBackground,
        scrolledContainerColor = colors.card,
        navigationIconContentColor = colors.accent,
        titleContentColor = colors.label,
        actionIconContentColor = colors.accent,
    )
}
