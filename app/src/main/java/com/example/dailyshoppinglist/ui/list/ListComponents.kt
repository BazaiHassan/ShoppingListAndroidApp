package com.example.dailyshoppinglist.ui.list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.data.db.FrequentItem
import com.example.dailyshoppinglist.data.db.ShoppingItem
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.ui.components.CheckCircle
import com.example.dailyshoppinglist.ui.components.GroupDivider
import com.example.dailyshoppinglist.ui.components.GroupPosition
import com.example.dailyshoppinglist.ui.components.SuggestionChip
import com.example.dailyshoppinglist.ui.components.groupedRow
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.Formats
import com.example.dailyshoppinglist.ui.util.MaxContentWidth
import com.example.dailyshoppinglist.ui.util.currentLocale
import com.example.dailyshoppinglist.ui.util.quantityString

/** A list row that can be swiped away to delete. */
@Composable
fun SwipeableItemRow(
    item: ShoppingItem,
    position: GroupPosition,
    showCategory: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        },
        positionalThreshold = { distance -> distance * 0.4f },
    )
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        modifier = modifier.groupedRow(position, colors.destructive),
        backgroundContent = {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(colors.destructive)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(Icons.Rounded.Delete, contentDescription = stringResource(R.string.delete), tint = Color.White)
            }
        },
    ) {
        ShoppingItemRow(item, position, showCategory, onToggle, onClick)
    }
}

@Composable
fun ShoppingItemRow(
    item: ShoppingItem,
    position: GroupPosition,
    showCategory: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    val textAlpha by animateFloatAsState(if (item.isChecked) 0.55f else 1f, label = "alpha")
    Column(Modifier.background(colors.card)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .heightIn(min = 54.dp)
                .padding(start = 4.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            CheckCircle(
                checked = item.isChecked,
                onCheckedChange = { onToggle() },
                contentDescription = stringResource(if (item.isChecked) R.string.mark_not_bought else R.string.mark_bought),
            )
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 4.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
            ) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = (if (item.isChecked) colors.secondaryLabel else colors.label).copy(alpha = textAlpha),
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.note.isNotBlank()) {
                    Text(
                        item.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.secondaryLabel,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (item.quantity > 1) {
                Text(
                    "×" + Formats.number(item.quantity, locale),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (item.isChecked) colors.secondaryLabel else colors.accent,
                    modifier = Modifier
                        .background(colors.fill, RoundedCornerShape(50))
                        .padding(horizontal = 9.dp, vertical = 2.dp),
                )
            }
            if (showCategory) {
                Text(
                    Category.fromKey(item.category).emoji,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
        if (position.showsDivider) GroupDivider(startIndent = 52.dp)
    }
}

/** Summary card with a progress ring and the "Finish" action. */
@Composable
fun ProgressCard(
    inCart: Int,
    total: Int,
    progress: Float,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    val animated by animateFloatAsState(progress, label = "progress")
    Surface(shape = RoundedCornerShape(16.dp), color = colors.card, modifier = modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.size(54.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { animated },
                    modifier = Modifier.size(54.dp),
                    color = if (inCart == total) colors.success else colors.accent,
                    strokeWidth = 5.dp,
                    trackColor = colors.fill,
                    strokeCap = StrokeCap.Round,
                )
                Text(Formats.percent(progress, locale), style = MaterialTheme.typography.labelSmall, color = colors.label)
            }
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.progress_title, Formats.number(inCart, locale), Formats.number(total, locale)),
                    style = MaterialTheme.typography.titleMedium,
                    color = colors.label,
                )
                Text(
                    if (inCart == total) {
                        stringResource(R.string.progress_done)
                    } else {
                        quantityString(R.plurals.progress_remaining, total - inCart, Formats.number(total - inCart, locale))
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.secondaryLabel,
                )
            }
            AnimatedVisibility(visible = inCart > 0, enter = fadeIn(), exit = fadeOut()) {
                Button(
                    onClick = onFinish,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent, contentColor = Color.White),
                ) {
                    Text(stringResource(R.string.finish_shopping), style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

/** Bottom composer: a rounded text field with history-based suggestions above it. */
@Composable
fun AddItemBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    suggestions: List<FrequentItem>,
    onSuggestion: (FrequentItem) -> Unit,
    insets: WindowInsets,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    var focused by remember { mutableStateOf(false) }
    Surface(color = colors.groupedBackground.copy(alpha = 0.97f), modifier = modifier.fillMaxWidth()) {
        Column(
            Modifier
                .windowInsetsPadding(insets)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalDivider(thickness = 0.5.dp, color = colors.separator)
            AnimatedVisibility(
                visible = focused && suggestions.isNotEmpty(),
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                LazyRow(
                    modifier = Modifier.widthIn(max = MaxContentWidth),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(suggestions, key = { it.name }) { suggestion ->
                        SuggestionChip(
                            label = suggestion.name,
                            emoji = Category.fromKey(suggestion.category).emoji,
                            onClick = { onSuggestion(suggestion) },
                        )
                    }
                }
            }
            Row(
                Modifier
                    .widthIn(max = MaxContentWidth)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .heightIn(min = 44.dp)
                        .background(colors.card, RoundedCornerShape(22.dp))
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.AddCircle, contentDescription = null, tint = colors.accent, modifier = Modifier.size(24.dp))
                    Box(
                        Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp, vertical = 10.dp),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (text.isEmpty()) {
                            Text(stringResource(R.string.add_item_hint), style = MaterialTheme.typography.bodyLarge, color = colors.tertiaryLabel, maxLines = 1)
                        }
                        BasicTextField(
                            value = text,
                            onValueChange = onTextChange,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.label),
                            cursorBrush = SolidColor(colors.accent),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .onFocusChanged { focused = it.isFocused },
                        )
                    }
                }
                val canSubmit = text.isNotBlank()
                IconButton(
                    onClick = onSubmit,
                    enabled = canSubmit,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White,
                        disabledContainerColor = colors.fill,
                        disabledContentColor = colors.tertiaryLabel,
                    ),
                ) {
                    Icon(Icons.Rounded.ArrowUpward, contentDescription = stringResource(R.string.add_item))
                }
            }
        }
    }
}

@Composable
fun CategoryHeaderLabel(category: Category, count: Int): String {
    val locale = currentLocale()
    return "${category.emoji}  ${stringResource(category.label)} · ${Formats.number(count, locale)}"
}
