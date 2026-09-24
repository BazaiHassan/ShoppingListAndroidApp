package com.example.dailyshoppinglist.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.rounded.AddCircleOutline
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.data.db.FrequentItem
import com.example.dailyshoppinglist.data.db.TripItem
import com.example.dailyshoppinglist.data.db.TripWithItems
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.ui.components.ConfirmDialog
import com.example.dailyshoppinglist.ui.components.EmptyState
import com.example.dailyshoppinglist.ui.components.GroupDivider
import com.example.dailyshoppinglist.ui.components.GroupPosition
import com.example.dailyshoppinglist.ui.components.InsetGroup
import com.example.dailyshoppinglist.ui.components.SearchField
import com.example.dailyshoppinglist.ui.components.SectionHeader
import com.example.dailyshoppinglist.ui.components.SuggestionChip
import com.example.dailyshoppinglist.ui.components.appTopBarColors
import com.example.dailyshoppinglist.ui.components.groupedRow
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.Formats
import com.example.dailyshoppinglist.ui.util.MaxContentWidth
import com.example.dailyshoppinglist.ui.util.currentLocale
import com.example.dailyshoppinglist.ui.util.quantityString
import com.example.dailyshoppinglist.ui.util.rememberHaptic
import com.example.dailyshoppinglist.ui.util.sidePadding
import kotlinx.coroutines.launch

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
    widthSizeClass: WindowWidthSizeClass,
    insets: WindowInsets,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    var selectedId by rememberSaveable { mutableStateOf<Long?>(null) }
    val selected = state.trip(selectedId)
    val twoPane = widthSizeClass == WindowWidthSizeClass.Expanded

    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val haptic = rememberHaptic()
    val locale = currentLocale()

    fun notify(message: String) {
        haptic()
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            snackbar.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    val actions = TripActions(
        onAddItem = { item ->
            viewModel.addToList(item)
            notify(context.getString(R.string.added_to_list, item.name))
        },
        onAddAll = { items ->
            viewModel.addAllToList(items)
            notify(context.resources.getQuantityString(R.plurals.added_all, items.size, Formats.number(items.size, locale)))
        },
        onDelete = { id ->
            viewModel.deleteTrip(id)
            if (selectedId == id) selectedId = null
        },
    )

    val list: @Composable (WindowInsets) -> Unit = { paneInsets ->
        TripListPane(
            state = state,
            query = query,
            selectedId = if (twoPane) selectedId else null,
            onQueryChange = viewModel::onQueryChange,
            onSelect = { selectedId = it },
            onQuickAdd = {
                viewModel.addToList(it)
                notify(context.getString(R.string.added_to_list, it.name))
            },
            onClearHistory = {
                viewModel.clearHistory()
                selectedId = null
            },
            insets = paneInsets,
        )
    }

    Box(Modifier.fillMaxSize()) {
        if (twoPane) {
            Row(Modifier.fillMaxSize()) {
                Box(Modifier.weight(0.42f)) { list(insets.only(WindowInsetsSides.Vertical + WindowInsetsSides.Start)) }
                VerticalDivider(thickness = 0.5.dp, color = LocalAppColors.current.separator)
                Box(Modifier.weight(0.58f)) {
                    val detailInsets = insets.only(WindowInsetsSides.Vertical + WindowInsetsSides.End)
                    if (selected != null) {
                        TripDetailPane(selected, showBack = false, onBack = {}, actions = actions, insets = detailInsets)
                    } else {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .windowInsetsPadding(detailInsets),
                            verticalArrangement = Arrangement.Center,
                        ) {
                            EmptyState(
                                icon = Icons.Rounded.ReceiptLong,
                                title = stringResource(R.string.history_title),
                                message = stringResource(R.string.select_trip),
                            )
                        }
                    }
                }
            }
        } else {
            BackHandler(enabled = selected != null) { selectedId = null }
            AnimatedContent(
                targetState = selected,
                transitionSpec = {
                    val forward = targetState != null
                    (slideInHorizontally(tween(280)) { if (forward) it / 3 else -it / 3 } + fadeIn(tween(280)))
                        .togetherWith(slideOutHorizontally(tween(280)) { if (forward) -it / 3 else it / 3 } + fadeOut(tween(200)))
                },
                contentKey = { it?.trip?.id },
                label = "history",
            ) { trip ->
                if (trip != null) {
                    TripDetailPane(trip, showBack = true, onBack = { selectedId = null }, actions = actions, insets = insets)
                } else {
                    list(insets)
                }
            }
        }
        SnackbarHost(
            snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .windowInsetsPadding(insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal))
                .padding(bottom = 72.dp),
        ) { data ->
            Snackbar(
                data,
                shape = RoundedCornerShape(14.dp),
                containerColor = MaterialTheme.colorScheme.inverseSurface,
                contentColor = MaterialTheme.colorScheme.inverseOnSurface,
            )
        }
    }
}

private class TripActions(
    val onAddItem: (TripItem) -> Unit,
    val onAddAll: (List<TripItem>) -> Unit,
    val onDelete: (Long) -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripListPane(
    state: HistoryUiState,
    query: String,
    selectedId: Long?,
    onQueryChange: (String) -> Unit,
    onSelect: (Long) -> Unit,
    onQuickAdd: (FrequentItem) -> Unit,
    onClearHistory: () -> Unit,
    insets: WindowInsets,
) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    var menuOpen by remember { mutableStateOf(false) }
    var confirmClear by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.groupedBackground,
        contentWindowInsets = insets,
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                actions = {
                    if (state.hasHistory) {
                        IconButton(onClick = { menuOpen = true }) {
                            Icon(Icons.Rounded.MoreHoriz, contentDescription = stringResource(R.string.more_options))
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false },
                            shape = RoundedCornerShape(14.dp),
                            containerColor = colors.elevatedCard,
                        ) {
                            DropdownMenuItem(
                                text = { Text(stringResource(R.string.clear_history), color = colors.destructive) },
                                leadingIcon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = colors.destructive) },
                                onClick = {
                                    menuOpen = false
                                    confirmClear = true
                                },
                            )
                        }
                    }
                },
                colors = appTopBarColors(),
                scrollBehavior = scrollBehavior,
                windowInsets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            )
        },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val side = sidePadding(maxWidth)
            val layoutDirection = LocalLayoutDirection.current
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = side + padding.calculateStartPadding(layoutDirection),
                    end = side + padding.calculateEndPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
            ) {
                if (state.loading) return@LazyColumn
                if (!state.hasHistory) {
                    item(key = "empty") {
                        EmptyState(
                            icon = Icons.Rounded.History,
                            title = stringResource(R.string.history_empty_title),
                            message = stringResource(R.string.history_empty_message),
                        )
                    }
                    return@LazyColumn
                }
                item(key = "search") {
                    SearchField(query, onQueryChange, stringResource(R.string.history_search), Modifier.padding(top = 4.dp, bottom = 4.dp))
                }
                if (query.isBlank() && state.frequent.isNotEmpty()) {
                    item(key = "frequent") {
                        Column {
                            SectionHeader(stringResource(R.string.frequent_title))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(state.frequent, key = { it.name }) { frequent ->
                                    SuggestionChip(
                                        label = frequent.name,
                                        emoji = Category.fromKey(frequent.category).emoji,
                                        badge = "×" + Formats.number(frequent.count, locale),
                                        onClick = { onQuickAdd(frequent) },
                                    )
                                }
                            }
                        }
                    }
                }
                if (state.trips.isEmpty()) {
                    item(key = "no-results") {
                        Text(
                            stringResource(R.string.history_no_results),
                            style = MaterialTheme.typography.bodyMedium,
                            color = colors.secondaryLabel,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                        )
                    }
                }
                // Group consecutive trips by (localized) month.
                val months = state.trips.groupBy { Formats.monthYear(it.trip.completedAt, locale) }
                months.forEach { (month, trips) ->
                    item(key = "month-$month") { SectionHeader(month, Modifier.animateItem()) }
                    itemsIndexed(trips, key = { _, trip -> "trip-${trip.trip.id}" }) { index, trip ->
                        TripRow(
                            trip = trip,
                            position = GroupPosition.of(index, trips.size),
                            selected = trip.trip.id == selectedId,
                            onClick = { onSelect(trip.trip.id) },
                            modifier = Modifier.animateItem(),
                        )
                    }
                }
            }
        }
    }

    if (confirmClear) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_clear_history_title),
            message = stringResource(R.string.confirm_clear_history_message),
            confirmLabel = stringResource(R.string.clear),
            onConfirm = onClearHistory,
            onDismiss = { confirmClear = false },
        )
    }
}

@Composable
private fun TripRow(
    trip: TripWithItems,
    position: GroupPosition,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    Column(modifier.groupedRow(position, if (selected) colors.accent.copy(alpha = 0.14f).compositeOver(colors.card) else colors.card)) {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .heightIn(min = 64.dp)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .background(colors.accent.copy(alpha = 0.14f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    Category.fromKey(trip.items.groupingBy { it.category }.eachCount().maxByOrNull { it.value }?.key).emoji,
                    style = MaterialTheme.typography.titleMedium,
                )
            }
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        Formats.shortDate(trip.trip.completedAt, locale),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.label,
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                    )
                    Text(
                        "  ·  " + Formats.time(trip.trip.completedAt, locale),
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.secondaryLabel,
                        maxLines = 1,
                    )
                }
                Text(
                    trip.items.joinToString("، ".takeIf { locale.language == "fa" } ?: ", ") { it.name },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondaryLabel,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                Formats.number(trip.items.size, locale),
                style = MaterialTheme.typography.bodyMedium,
                color = colors.secondaryLabel,
            )
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.tertiaryLabel,
                modifier = Modifier.size(22.dp),
            )
        }
        if (position.showsDivider) GroupDivider(startIndent = 68.dp)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TripDetailPane(
    trip: TripWithItems,
    showBack: Boolean,
    onBack: () -> Unit,
    actions: TripActions,
    insets: WindowInsets,
) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    var confirmDelete by rememberSaveable(trip.trip.id) { mutableStateOf(false) }

    Scaffold(
        containerColor = colors.groupedBackground,
        contentWindowInsets = insets,
        topBar = {
            TopAppBar(
                title = { Text(Formats.shortDate(trip.trip.completedAt, locale), maxLines = 1) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = stringResource(R.string.delete_trip), tint = colors.destructive)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.groupedBackground,
                    navigationIconContentColor = colors.accent,
                    titleContentColor = colors.label,
                    actionIconContentColor = colors.accent,
                ),
                windowInsets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            )
        },
        bottomBar = {
            Surface(color = colors.groupedBackground) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    HorizontalDivider(thickness = 0.5.dp, color = colors.separator)
                    Button(
                        onClick = { actions.onAddAll(trip.items) },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.White),
                        contentPadding = PaddingValues(vertical = 14.dp),
                        modifier = Modifier
                            .widthIn(max = MaxContentWidth)
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                    ) {
                        Text(stringResource(R.string.add_all_to_list), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        },
    ) { padding ->
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val side = sidePadding(maxWidth)
            val layoutDirection = LocalLayoutDirection.current
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = side + padding.calculateStartPadding(layoutDirection),
                    end = side + padding.calculateEndPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
            ) {
                item(key = "summary") {
                    InsetGroup(Modifier.padding(top = 8.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                Formats.date(trip.trip.completedAt, locale),
                                style = MaterialTheme.typography.headlineSmall,
                                color = colors.label,
                            )
                            Text(
                                Formats.time(trip.trip.completedAt, locale) + "  ·  " +
                                    quantityString(R.plurals.trip_items, trip.items.size, Formats.number(trip.items.size, locale)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = colors.secondaryLabel,
                            )
                        }
                    }
                }
                item(key = "items-header") { SectionHeader(stringResource(R.string.trip_items_header)) }
                itemsIndexed(trip.items, key = { _, item -> "ti-${item.id}" }) { index, item ->
                    val position = GroupPosition.of(index, trip.items.size)
                    Column(Modifier.groupedRow(position, colors.card)) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .heightIn(min = 52.dp)
                                .padding(start = 16.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Text(Category.fromKey(item.category).emoji, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                item.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = colors.label,
                                modifier = Modifier.weight(1f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (item.quantity > 1) {
                                Text("×" + Formats.number(item.quantity, locale), style = MaterialTheme.typography.labelMedium, color = colors.secondaryLabel)
                            }
                            IconButton(onClick = { actions.onAddItem(item) }) {
                                Icon(Icons.Rounded.AddCircleOutline, contentDescription = stringResource(R.string.add_to_list), tint = colors.accent)
                            }
                        }
                        if (position.showsDivider) GroupDivider(startIndent = 48.dp)
                    }
                }
            }
        }
    }

    if (confirmDelete) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_delete_trip_title),
            message = stringResource(R.string.confirm_delete_trip_message),
            confirmLabel = stringResource(R.string.delete),
            onConfirm = { actions.onDelete(trip.trip.id) },
            onDismiss = { confirmDelete = false },
        )
    }
}

