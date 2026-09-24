package com.example.dailyshoppinglist.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.data.db.ShoppingItem
import com.example.dailyshoppinglist.domain.Category
import com.example.dailyshoppinglist.ui.components.GroupDivider
import com.example.dailyshoppinglist.ui.components.GroupRow
import com.example.dailyshoppinglist.ui.components.InsetGroup
import com.example.dailyshoppinglist.ui.components.QuantityStepper
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.MaxContentWidth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ItemEditorSheet(
    item: ShoppingItem,
    onDismiss: () -> Unit,
    onSave: (ShoppingItem) -> Unit,
    onDelete: () -> Unit,
) {
    val colors = LocalAppColors.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var name by rememberSaveable(item.id) { mutableStateOf(item.name) }
    var note by rememberSaveable(item.id) { mutableStateOf(item.note) }
    var quantity by rememberSaveable(item.id) { mutableIntStateOf(item.quantity) }
    var categoryKey by rememberSaveable(item.id) { mutableStateOf(Category.fromKey(item.category).key) }

    fun close(then: () -> Unit = {}) {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            then()
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.groupedBackground,
        dragHandle = { BottomSheetDefaults.DragHandle(color = colors.tertiaryLabel) },
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                Modifier
                    .widthIn(max = MaxContentWidth)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
            ) {
                // iOS style sheet header: Cancel · Title · Save
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { close() }) {
                        Text(stringResource(R.string.cancel), color = colors.accent, style = MaterialTheme.typography.bodyLarge)
                    }
                    Text(
                        stringResource(R.string.edit_item),
                        style = MaterialTheme.typography.titleMedium,
                        color = colors.label,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f),
                    )
                    TextButton(
                        enabled = name.isNotBlank(),
                        onClick = {
                            val updated = item.copy(name = name, note = note, quantity = quantity, category = categoryKey)
                            close { onSave(updated) }
                        },
                    ) {
                        Text(
                            stringResource(R.string.save),
                            color = if (name.isNotBlank()) colors.accent else colors.tertiaryLabel,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }

                InsetGroup(Modifier.padding(top = 8.dp)) {
                    SheetTextField(name, { name = it }, stringResource(R.string.item_name), ImeAction.Next)
                    GroupDivider()
                    SheetTextField(note, { note = it }, stringResource(R.string.item_note), ImeAction.Done)
                }

                InsetGroup(header = stringResource(R.string.quantity)) {
                    GroupRow(
                        title = stringResource(R.string.quantity),
                        trailing = { QuantityStepper(quantity, { quantity = it }) },
                    )
                }

                InsetGroup(header = stringResource(R.string.category)) {
                    FlowRow(
                        Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Category.entries.forEach { category ->
                            CategoryChip(category, selected = category.key == categoryKey) { categoryKey = category.key }
                        }
                    }
                }

                InsetGroup(Modifier.padding(top = 20.dp)) {
                    GroupRow(
                        title = stringResource(R.string.delete_item),
                        titleColor = colors.destructive,
                        onClick = { close(onDelete) },
                    )
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun SheetTextField(value: String, onValueChange: (String) -> Unit, placeholder: String, imeAction: ImeAction) {
    val colors = LocalAppColors.current
    Box(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (value.isEmpty()) {
            Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = colors.tertiaryLabel)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.label),
            cursorBrush = SolidColor(colors.accent),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = imeAction),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun CategoryChip(category: Category, selected: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) colors.accent else colors.fill,
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(category.emoji, style = MaterialTheme.typography.bodyMedium)
            Text(
                stringResource(category.label),
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) Color.White else colors.label,
            )
        }
    }
}
