package com.example.dailyshoppinglist.ui.settings

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailyshoppinglist.BuildConfig
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.data.settings.AccentColor
import com.example.dailyshoppinglist.data.settings.ThemeMode
import com.example.dailyshoppinglist.ui.components.ConfirmDialog
import com.example.dailyshoppinglist.ui.components.GroupDivider
import com.example.dailyshoppinglist.ui.components.GroupRow
import com.example.dailyshoppinglist.ui.components.IconBadge
import com.example.dailyshoppinglist.ui.components.InsetGroup
import com.example.dailyshoppinglist.ui.components.SegmentedControl
import com.example.dailyshoppinglist.ui.components.appTopBarColors
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.sidePadding

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel, insets: WindowInsets) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val colors = LocalAppColors.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val language = remember { AppLanguage.current() }
    var confirmClearHistory by rememberSaveable { mutableStateOf(false) }
    val accentPreview = { accent: AccentColor -> Color(if (colors.isDark) accent.dark else accent.light) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.groupedBackground,
        contentWindowInsets = insets,
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                colors = appTopBarColors(),
                scrollBehavior = scrollBehavior,
                windowInsets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val side = sidePadding(maxWidth, limit = 640.dp)
            val layoutDirection = LocalLayoutDirection.current
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = side + padding.calculateStartPadding(layoutDirection),
                    end = side + padding.calculateEndPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 32.dp,
                ),
            ) {
                item(key = "appearance") {
                    InsetGroup(header = stringResource(R.string.appearance)) {
                        GroupRow(
                            title = stringResource(R.string.theme),
                            leading = { IconBadge(Icons.Rounded.DarkMode, Color(0xFF5856D6)) },
                        )
                        SegmentedControl(
                            options = listOf(
                                stringResource(R.string.theme_system),
                                stringResource(R.string.theme_light),
                                stringResource(R.string.theme_dark),
                            ),
                            selectedIndex = settings.themeMode.ordinal,
                            onSelect = { viewModel.setThemeMode(ThemeMode.entries[it]) },
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp),
                        )
                        GroupDivider(startIndent = 58.dp)
                        GroupRow(
                            title = stringResource(R.string.accent_color),
                            leading = { IconBadge(Icons.Rounded.Palette, accentPreview(settings.accent)) },
                        )
                        AccentPicker(
                            selected = settings.accent,
                            preview = accentPreview,
                            onSelect = viewModel::setAccent,
                        )
                    }
                }
                item(key = "language") {
                    InsetGroup(header = stringResource(R.string.language)) {
                        val options = listOf(
                            AppLanguage.SYSTEM to stringResource(R.string.language_system),
                            AppLanguage.PERSIAN to "فارسی",
                            AppLanguage.ENGLISH to "English",
                        )
                        options.forEachIndexed { index, (option, label) ->
                            GroupRow(
                                title = label,
                                leading = if (index == 0) {
                                    { IconBadge(Icons.Rounded.Language, Color(0xFF007AFF)) }
                                } else {
                                    { Box(Modifier.size(30.dp)) }
                                },
                                trailing = {
                                    if (option == language) Icon(Icons.Rounded.Check, contentDescription = null, tint = colors.accent)
                                },
                                onClick = { if (option != language) viewModel.setLanguage(option) },
                            )
                            if (index < options.lastIndex) GroupDivider(startIndent = 58.dp)
                        }
                    }
                }
                item(key = "general") {
                    InsetGroup(
                        header = stringResource(R.string.general),
                        footer = stringResource(R.string.group_by_category_footer),
                    ) {
                        SwitchRow(
                            title = R.string.haptics,
                            icon = { IconBadge(Icons.Rounded.Vibration, Color(0xFFFF2D55)) },
                            checked = settings.haptics,
                            onCheckedChange = viewModel::setHaptics,
                        )
                        GroupDivider(startIndent = 58.dp)
                        SwitchRow(
                            title = R.string.menu_group_by_category,
                            icon = { IconBadge(Icons.Rounded.Category, Color(0xFFFF9500)) },
                            checked = settings.groupByCategory,
                            onCheckedChange = viewModel::setGroupByCategory,
                        )
                    }
                }
                item(key = "data") {
                    InsetGroup(header = stringResource(R.string.data)) {
                        GroupRow(
                            title = stringResource(R.string.clear_history),
                            titleColor = colors.destructive,
                            leading = { IconBadge(Icons.Rounded.Delete, colors.destructive) },
                            onClick = { confirmClearHistory = true },
                        )
                    }
                }
                item(key = "about") {
                    InsetGroup(header = stringResource(R.string.about), footer = stringResource(R.string.about_footer)) {
                        GroupRow(
                            title = stringResource(R.string.version),
                            leading = { IconBadge(Icons.Rounded.Info, Color(0xFF8E8E93)) },
                            trailing = {
                                Text(BuildConfig.VERSION_NAME, style = MaterialTheme.typography.bodyLarge, color = colors.secondaryLabel)
                            },
                        )
                    }
                }
            }
        }
    }

    if (confirmClearHistory) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_clear_history_title),
            message = stringResource(R.string.confirm_clear_history_message),
            confirmLabel = stringResource(R.string.clear),
            onConfirm = viewModel::clearHistory,
            onDismiss = { confirmClearHistory = false },
        )
    }
}

@Composable
private fun SwitchRow(
    @StringRes title: Int,
    icon: @Composable () -> Unit,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val colors = LocalAppColors.current
    GroupRow(
        title = stringResource(title),
        leading = icon,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedTrackColor = colors.success,
                    checkedThumbColor = Color.White,
                    checkedBorderColor = colors.success,
                    uncheckedTrackColor = colors.fill,
                    uncheckedThumbColor = Color.White,
                    uncheckedBorderColor = Color.Transparent,
                ),
            )
        },
        onClick = { onCheckedChange(!checked) },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AccentPicker(
    selected: AccentColor,
    preview: (AccentColor) -> Color,
    onSelect: (AccentColor) -> Unit,
) {
    val colors = LocalAppColors.current
    val names = accentNames()
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AccentColor.entries.forEach { accent ->
            val isSelected = accent == selected
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(if (isSelected) 2.5.dp else 0.dp, if (isSelected) colors.label.copy(alpha = 0.25f) else Color.Transparent, CircleShape)
                    .padding(if (isSelected) 4.dp else 0.dp)
                    .background(preview(accent), CircleShape)
                    .selectable(selected = isSelected, role = Role.RadioButton, onClick = { onSelect(accent) })
                    .semantics { contentDescription = names.getValue(accent) },
                contentAlignment = Alignment.Center,
            ) {
                if (isSelected) Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun accentNames(): Map<AccentColor, String> = mapOf(
    AccentColor.BLUE to stringResource(R.string.accent_blue),
    AccentColor.INDIGO to stringResource(R.string.accent_indigo),
    AccentColor.PURPLE to stringResource(R.string.accent_purple),
    AccentColor.PINK to stringResource(R.string.accent_pink),
    AccentColor.RED to stringResource(R.string.accent_red),
    AccentColor.ORANGE to stringResource(R.string.accent_orange),
    AccentColor.GREEN to stringResource(R.string.accent_green),
    AccentColor.TEAL to stringResource(R.string.accent_teal),
)
