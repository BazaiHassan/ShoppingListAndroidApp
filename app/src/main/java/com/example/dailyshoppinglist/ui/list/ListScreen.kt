package com.example.dailyshoppinglist.ui.list

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.RemoveDone
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.ShoppingCart
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
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.data.db.ShoppingItem
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.domain.ShareFormatter
import com.example.dailyshoppinglist.ui.components.ConfirmDialog
import com.example.dailyshoppinglist.ui.components.EmptyState
import com.example.dailyshoppinglist.ui.components.GroupPosition
import com.example.dailyshoppinglist.ui.components.SectionFooter
import com.example.dailyshoppinglist.ui.components.SectionHeader
import com.example.dailyshoppinglist.ui.components.SuggestionChip
import com.example.dailyshoppinglist.ui.components.appTopBarColors
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.Formats
import com.example.dailyshoppinglist.ui.util.currentLocale
import com.example.dailyshoppinglist.ui.util.rememberHaptic
import com.example.dailyshoppinglist.ui.util.sidePadding
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ListScreen(
    viewModel: ListViewModel,
    insets: WindowInsets,
    onOpenHistory: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val suggestions by viewModel.suggestions.collectAsStateWithLifecycle()
    val colors = LocalAppColors.current
    val context = LocalContext.current
    val locale = currentLocale()
    val haptic = rememberHaptic()
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val focusRequester = remember { FocusRequester() }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var input by rememberSaveable { mutableStateOf("") }
    var editingId by rememberSaveable { mutableStateOf<Long?>(null) }
    var menuOpen by remember { mutableStateOf(false) }
    var confirmClearAll by rememberSaveable { mutableStateOf(false) }

    fun showUndo(message: String, restore: List<ShoppingItem>) {
        if (restore.isEmpty()) return
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result = snackbar.showSnackbar(message, context.getString(R.string.undo), duration = SnackbarDuration.Short)
            if (result == SnackbarResult.ActionPerformed) viewModel.restore(restore)
        }
    }

    fun deleteItem(item: ShoppingItem) {
        haptic()
        viewModel.delete(item)
        showUndo(context.getString(R.string.snack_item_deleted, item.name), listOf(item))
    }

    fun finish() {
        haptic()
        val count = state.inCart.size
        viewModel.finishShopping()
        scope.launch {
            snackbar.currentSnackbarData?.dismiss()
            val result = snackbar.showSnackbar(
                context.resources.getQuantityString(R.plurals.snack_trip_saved, count, Formats.number(count, locale)),
                context.getString(R.string.snack_view),
                duration = SnackbarDuration.Short,
            )
            if (result == SnackbarResult.ActionPerformed) onOpenHistory()
        }
    }

    fun share() {
        val text = ShareFormatter.format(context.getString(R.string.share_title), state.toBuy, state.inCart)
        val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
        context.startActivity(Intent.createChooser(send, context.getString(R.string.share_via)))
    }

    fun submit() {
        if (viewModel.submit(input)) {
            haptic()
            input = ""
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = colors.groupedBackground,
        contentWindowInsets = insets,
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(R.string.list_title)) },
                actions = {
                    IconButton(onClick = ::share, enabled = !state.isEmpty) {
                        Icon(Icons.Rounded.IosShare, contentDescription = stringResource(R.string.menu_share))
                    }
                    IconButton(onClick = { menuOpen = true }) {
                        Icon(Icons.Rounded.MoreHoriz, contentDescription = stringResource(R.string.more_options))
                    }
                    ListMenu(
                        expanded = menuOpen,
                        onDismiss = { menuOpen = false },
                        groupByCategory = state.groupByCategory,
                        hasChecked = state.inCart.isNotEmpty(),
                        hasItems = !state.isEmpty,
                        onToggleGroup = { viewModel.setGroupByCategory(!state.groupByCategory) },
                        onClearChecked = {
                            val removed = viewModel.clearChecked()
                            showUndo(
                                context.resources.getQuantityString(R.plurals.snack_cleared, removed.size, Formats.number(removed.size, locale)),
                                removed,
                            )
                        },
                        onClearAll = { confirmClearAll = true },
                    )
                },
                colors = appTopBarColors(),
                scrollBehavior = scrollBehavior,
                windowInsets = insets.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal),
            )
        },
        bottomBar = {
            AddItemBar(
                text = input,
                onTextChange = {
                    input = it
                    viewModel.onQueryChange(it)
                },
                onSubmit = ::submit,
                suggestions = suggestions,
                onSuggestion = {
                    haptic()
                    viewModel.addSuggestion(it)
                    input = ""
                },
                insets = insets.only(WindowInsetsSides.Bottom + WindowInsetsSides.Horizontal),
                focusRequester = focusRequester,
            )
        },
        snackbarHost = {
            SnackbarHost(snackbar) { data ->
                Snackbar(
                    data,
                    shape = RoundedCornerShape(14.dp),
                    containerColor = MaterialTheme.colorScheme.inverseSurface,
                    contentColor = MaterialTheme.colorScheme.inverseOnSurface,
                    actionColor = colors.accent,
                )
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
                    top = padding.calculateTopPadding() + 4.dp,
                    bottom = padding.calculateBottomPadding() + 24.dp,
                ),
            ) {
                when {
                    state.loading -> Unit
                    state.isEmpty -> item(key = "empty") {
                        EmptyState(
                            icon = Icons.Rounded.ShoppingCart,
                            title = stringResource(R.string.empty_list_title),
                            message = stringResource(R.string.empty_list_message),
                            modifier = Modifier.animateItem(),
                        ) {
                            if (suggestions.isNotEmpty() && input.isBlank()) {
                                SectionHeader(stringResource(R.string.quick_add))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp, androidx.compose.ui.Alignment.CenterHorizontally),
                                    verticalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    suggestions.forEach { suggestion ->
                                        SuggestionChip(
                                            label = suggestion.name,
                                            emoji = Category.fromKey(suggestion.category).emoji,
                                            onClick = {
                                                haptic()
                                                viewModel.addSuggestion(suggestion)
                                            },
                                        )
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        item(key = "progress") {
                            ProgressCard(
                                inCart = state.inCart.size,
                                total = state.total,
                                progress = state.progress,
                                onFinish = ::finish,
                                modifier = Modifier
                                    .animateItem()
                                    .padding(top = 8.dp),
                            )
                        }
                        val showCategory = !state.groupByCategory
                        if (state.groupByCategory) {
                            state.toBuyByCategory.forEach { (category, items) ->
                                item(key = "header-${category.key}") {
                                    SectionHeader(CategoryHeaderLabel(category, items.size), Modifier.animateItem())
                                }
                                itemGroup(items, false, ::deleteItem, { haptic(); viewModel.toggle(it) }, { editingId = it.id })
                            }
                        } else if (state.toBuy.isNotEmpty()) {
                            item(key = "header-to-buy") {
                                SectionHeader(
                                    "${stringResource(R.string.section_to_buy)} · ${Formats.number(state.toBuy.size, locale)}",
                                    Modifier.animateItem(),
                                )
                            }
                            itemGroup(state.toBuy, showCategory, ::deleteItem, { haptic(); viewModel.toggle(it) }, { editingId = it.id })
                        }
                        if (state.inCart.isNotEmpty()) {
                            item(key = "header-in-cart") {
                                SectionHeader(
                                    "${stringResource(R.string.section_in_cart)} · ${Formats.number(state.inCart.size, locale)}",
                                    Modifier.animateItem(),
                                )
                            }
                            itemGroup(state.inCart, showCategory, ::deleteItem, { haptic(); viewModel.toggle(it) }, { editingId = it.id })
                        }
                        item(key = "footer") {
                            SectionFooter(stringResource(R.string.tip_gestures), Modifier.animateItem().padding(top = 8.dp))
                        }
                    }
                }
            }
        }
    }

    val editing = editingId?.let { id -> (state.toBuy + state.inCart).firstOrNull { it.id == id } }
    if (editing != null) {
        ItemEditorSheet(
            item = editing,
            onDismiss = { editingId = null },
            onSave = viewModel::update,
            onDelete = { deleteItem(editing) },
        )
    }

    if (confirmClearAll) {
        ConfirmDialog(
            title = stringResource(R.string.confirm_clear_all_title),
            message = stringResource(R.string.confirm_clear_all_message),
            confirmLabel = stringResource(R.string.clear),
            onConfirm = {
                val removed = viewModel.clearAll()
                showUndo(
                    context.resources.getQuantityString(R.plurals.snack_cleared, removed.size, Formats.number(removed.size, locale)),
                    removed,
                )
            },
            onDismiss = { confirmClearAll = false },
        )
    }
}

private fun LazyListScope.itemGroup(
    items: List<ShoppingItem>,
    showCategory: Boolean,
    onDelete: (ShoppingItem) -> Unit,
    onToggle: (ShoppingItem) -> Unit,
    onEdit: (ShoppingItem) -> Unit,
) {
    itemsIndexed(items, key = { _, item -> "item-${item.id}" }) { index, item ->
        SwipeableItemRow(
            item = item,
            position = GroupPosition.of(index, items.size),
            showCategory = showCategory,
            onToggle = { onToggle(item) },
            onClick = { onEdit(item) },
            onDelete = { onDelete(item) },
            modifier = Modifier.animateItem(),
        )
    }
}

@Composable
private fun ListMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    groupByCategory: Boolean,
    hasChecked: Boolean,
    hasItems: Boolean,
    onToggleGroup: () -> Unit,
    onClearChecked: () -> Unit,
    onClearAll: () -> Unit,
) {
    val colors = LocalAppColors.current
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(14.dp),
        containerColor = colors.elevatedCard,
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.menu_group_by_category)) },
            leadingIcon = { Icon(Icons.Outlined.Category, contentDescription = null) },
            trailingIcon = { if (groupByCategory) Icon(Icons.Rounded.Check, contentDescription = null, tint = colors.accent) },
            onClick = {
                onToggleGroup()
                onDismiss()
            },
        )
        HorizontalDivider(thickness = 0.5.dp, color = colors.separator)
        DropdownMenuItem(
            text = { Text(stringResource(R.string.menu_clear_checked)) },
            leadingIcon = { Icon(Icons.Outlined.RemoveDone, contentDescription = null) },
            enabled = hasChecked,
            onClick = {
                onClearChecked()
                onDismiss()
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.menu_clear_all), color = if (hasItems) colors.destructive else colors.tertiaryLabel) },
            leadingIcon = { Icon(Icons.Outlined.DeleteSweep, contentDescription = null, tint = if (hasItems) colors.destructive else colors.tertiaryLabel) },
            enabled = hasItems,
            onClick = {
                onClearAll()
                onDismiss()
            },
        )
    }
}

