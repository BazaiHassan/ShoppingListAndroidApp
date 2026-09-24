package com.example.dailyshoppinglist.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.R
import com.example.dailyshoppinglist.domain.MAX_QUANTITY
import com.example.dailyshoppinglist.ui.theme.LocalAppColors
import com.example.dailyshoppinglist.ui.util.Formats
import com.example.dailyshoppinglist.ui.util.currentLocale

/** Round Reminders-style checkbox with a springy fill animation. */
@Composable
fun CheckCircle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val colors = LocalAppColors.current
    val progress by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
        label = "check",
    )
    Box(
        modifier = modifier
            .size(44.dp)
            .toggleable(
                value = checked,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 20.dp),
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
            .then(if (contentDescription != null) Modifier.semantics { this.contentDescription = contentDescription } else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            Modifier
                .size(24.dp)
                .border(1.6.dp, if (checked) colors.accent else colors.tertiaryLabel, CircleShape),
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    scaleX = progress
                    scaleY = progress
                    alpha = progress.coerceIn(0f, 1f)
                }
                .background(colors.accent, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
    }
}

/** iOS-like segmented control with a sliding thumb. */
@Composable
fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(colors.fill)
            .padding(2.dp),
    ) {
        val segment = maxWidth / options.size
        val offset by animateDpAsState(
            targetValue = segment * selectedIndex,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "segment",
        )
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset(x = offset) // offset() mirrors automatically in RTL
                .width(segment)
                .fillMaxHeight()
                .shadow(2.dp, RoundedCornerShape(7.dp))
                .background(if (colors.isDark) Color(0xFF636366) else Color.White, RoundedCornerShape(7.dp)),
        )
        Row(Modifier.fillMaxWidth()) {
            options.forEachIndexed { index, label ->
                if (index > 0) {
                    val hidden = index == selectedIndex || index == selectedIndex + 1
                    VerticalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = if (hidden) Color.Transparent else colors.separator,
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .selectable(
                            selected = index == selectedIndex,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelect(index) },
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (index == selectedIndex) FontWeight.SemiBold else FontWeight.Normal,
                        color = colors.label,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/** Compact − / + stepper. */
@Composable
fun QuantityStepper(value: Int, onValueChange: (Int) -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalAppColors.current
    val locale = currentLocale()
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            Formats.number(value, locale),
            style = MaterialTheme.typography.titleMedium,
            color = colors.label,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(32.dp),
        )
        Row(
            Modifier
                .height(34.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(colors.fill),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StepperButton(Icons.Rounded.Remove, stringResource(R.string.decrease), enabled = value > 1) { onValueChange(value - 1) }
            VerticalDivider(Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = colors.separator)
            StepperButton(Icons.Rounded.Add, stringResource(R.string.increase), enabled = value < MAX_QUANTITY) { onValueChange(value + 1) }
        }
    }
}

@Composable
private fun StepperButton(icon: ImageVector, description: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = LocalAppColors.current
    Box(
        Modifier
            .width(46.dp)
            .fillMaxHeight()
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = description, tint = if (enabled) colors.label else colors.tertiaryLabel, modifier = Modifier.size(20.dp))
    }
}

/** Rounded search field in the style of UISearchBar. */
@Composable
fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colors.fill)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Rounded.Search, contentDescription = null, tint = colors.secondaryLabel, modifier = Modifier.size(20.dp))
        Box(Modifier.weight(1f).padding(horizontal = 6.dp), contentAlignment = Alignment.CenterStart) {
            if (value.isEmpty()) {
                Text(placeholder, style = MaterialTheme.typography.bodyLarge, color = colors.secondaryLabel, maxLines = 1)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = colors.label),
                cursorBrush = SolidColor(colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions.Default,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (value.isNotEmpty()) {
            IconButton(onClick = { onValueChange("") }, modifier = Modifier.size(28.dp)) {
                Icon(Icons.Rounded.Cancel, contentDescription = stringResource(R.string.clear), tint = colors.tertiaryLabel, modifier = Modifier.size(18.dp))
            }
        }
    }
}

/** Pill-shaped chip used for suggestions and quick-add. */
@Composable
fun SuggestionChip(label: String, emoji: String, onClick: () -> Unit, modifier: Modifier = Modifier, badge: String? = null) {
    val colors = LocalAppColors.current
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = colors.card,
        modifier = modifier,
        shadowElevation = 0.dp,
        border = androidx.compose.foundation.BorderStroke(0.5.dp, colors.separator),
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(emoji, style = MaterialTheme.typography.bodyMedium)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = colors.label, maxLines = 1)
            if (badge != null) {
                Text(badge, style = MaterialTheme.typography.labelSmall, color = colors.secondaryLabel)
            }
        }
    }
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    destructive: Boolean = true,
) {
    val colors = LocalAppColors.current
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.elevatedCard,
        shape = RoundedCornerShape(20.dp),
        title = { Text(title, style = MaterialTheme.typography.titleMedium, color = colors.label) },
        text = { Text(message, style = MaterialTheme.typography.bodyMedium, color = colors.secondaryLabel) },
        confirmButton = {
            TextButton(onClick = { onConfirm(); onDismiss() }) {
                Text(
                    confirmLabel,
                    color = if (destructive) colors.destructive else colors.accent,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = colors.accent, style = MaterialTheme.typography.bodyLarge)
            }
        },
    )
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = LocalAppColors.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(colors.accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = colors.accent, modifier = Modifier.size(44.dp))
        }
        Text(title, style = MaterialTheme.typography.headlineSmall, color = colors.label, textAlign = TextAlign.Center)
        Text(message, style = MaterialTheme.typography.bodyMedium, color = colors.secondaryLabel, textAlign = TextAlign.Center)
        content?.invoke()
    }
}
