package com.example.dailyshoppinglist.ui

import androidx.annotation.StringRes
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.ui.history.HistoryScreen
import com.example.dailyshoppinglist.ui.history.HistoryViewModel
import com.example.dailyshoppinglist.ui.list.ListScreen
import com.example.dailyshoppinglist.ui.list.ListViewModel
import com.example.dailyshoppinglist.ui.settings.SettingsScreen
import com.example.dailyshoppinglist.ui.settings.SettingsViewModel
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.Formats
import com.example.dailyshoppinglist.ui.util.currentLocale
import com.example.dailyshoppinglist.ui.util.rememberHaptic

enum class AppTab(@StringRes val label: Int, val selectedIcon: ImageVector, val icon: ImageVector) {
    LIST(R.string.tab_list, Icons.Rounded.ShoppingCart, Icons.Outlined.ShoppingCart),
    HISTORY(R.string.tab_history, Icons.Rounded.History, Icons.Outlined.History),
    SETTINGS(R.string.tab_settings, Icons.Rounded.Settings, Icons.Outlined.Settings),
}

/**
 * Adaptive shell: a bottom tab bar on phones, a navigation rail on tablets,
 * foldables and landscape windows.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ShoppingAppRoot(widthSizeClass: WindowWidthSizeClass) {
    val listViewModel: ListViewModel = viewModel(factory = ListViewModel.Factory)
    val historyViewModel: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
    val settingsViewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory)

    val colors = LocalAppColors.current
    val haptic = rememberHaptic()
    var tab by rememberSaveable { mutableStateOf(AppTab.LIST) }
    val listState by listViewModel.uiState.collectAsStateWithLifecycle()
    val badge = listState.toBuy.size
    val stateHolder = rememberSaveableStateHolder()
    val useRail = widthSizeClass != WindowWidthSizeClass.Compact
    val imeVisible = WindowInsets.isImeVisible

    val select: (AppTab) -> Unit = {
        if (it != tab) haptic()
        tab = it
    }

    val content: @Composable (WindowInsets) -> Unit = { insets ->
        Crossfade(targetState = tab, animationSpec = tween(180), label = "tab") { current ->
            stateHolder.SaveableStateProvider(current.name) {
                when (current) {
                    AppTab.LIST -> ListScreen(listViewModel, insets, onOpenHistory = { select(AppTab.HISTORY) })
                    AppTab.HISTORY -> HistoryScreen(historyViewModel, widthSizeClass, insets)
                    AppTab.SETTINGS -> SettingsScreen(settingsViewModel, insets)
                }
            }
        }
    }

    if (useRail) {
        Row(
            Modifier
                .fillMaxSize()
                .background(colors.groupedBackground),
        ) {
            NavigationRail(
                containerColor = colors.card,
                header = { Spacer(Modifier.height(8.dp)) },
            ) {
                Spacer(Modifier.weight(1f))
                AppTab.entries.forEach { item ->
                    val selected = item == tab
                    NavigationRailItem(
                        selected = selected,
                        onClick = { select(item) },
                        icon = { TabIcon(item, selected, if (item == AppTab.LIST) badge else 0) },
                        label = { Text(stringResource(item.label), style = MaterialTheme.typography.labelMedium) },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = colors.accent,
                            selectedTextColor = colors.accent,
                            indicatorColor = colors.accent.copy(alpha = 0.14f),
                            unselectedIconColor = colors.secondaryLabel,
                            unselectedTextColor = colors.secondaryLabel,
                        ),
                        modifier = Modifier.padding(vertical = 6.dp),
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            VerticalDivider(thickness = 0.5.dp, color = colors.separator)
            Box(Modifier.weight(1f)) {
                content(WindowInsets.safeDrawing.only(WindowInsetsSides.Vertical + WindowInsetsSides.End))
            }
        }
    } else {
        Column(
            Modifier
                .fillMaxSize()
                .background(colors.groupedBackground),
        ) {
            Box(Modifier.weight(1f)) {
                // While typing, the keyboard replaces the tab bar, so screens pad for it themselves.
                content(
                    if (imeVisible) {
                        WindowInsets.safeDrawing
                    } else {
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal)
                    },
                )
            }
            if (!imeVisible) AppTabBar(tab, badge, select)
        }
    }
}

/** Translucent iOS-style tab bar. */
@Composable
private fun AppTabBar(selected: AppTab, badge: Int, onSelect: (AppTab) -> Unit) {
    val colors = LocalAppColors.current
    Surface(color = colors.card.copy(alpha = 0.97f)) {
        Column {
            HorizontalDivider(thickness = 0.5.dp, color = colors.separator)
            Row(
                Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                AppTab.entries.forEach { tab ->
                    val isSelected = tab == selected
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .selectable(
                                selected = isSelected,
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                role = Role.Tab,
                                onClick = { onSelect(tab) },
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                    ) {
                        TabIcon(tab, isSelected, if (tab == AppTab.LIST) badge else 0)
                        Text(
                            stringResource(tab.label),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) colors.accent else colors.secondaryLabel,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TabIcon(tab: AppTab, selected: Boolean, badge: Int) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    BadgedBox(
        badge = {
            if (badge > 0) {
                Badge(containerColor = colors.destructive, contentColor = Color.White) {
                    Text(if (badge > 99) "99+" else Formats.number(badge, locale))
                }
            }
        },
    ) {
        Icon(
            if (selected) tab.selectedIcon else tab.icon,
            contentDescription = null,
            tint = if (selected) colors.accent else colors.secondaryLabel,
            modifier = Modifier.size(26.dp),
        )
    }
}
