package com.example.dailyshoppinglist.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dailyshoppinglist.ui.theme.LocalAppColors

val GroupCornerRadius = 12.dp

/** Where a row sits inside an inset grouped section; drives its rounded corners. */
enum class GroupPosition {
    Single, First, Middle, Last;

    val shape: Shape
        get() = when (this) {
            Single -> RoundedCornerShape(GroupCornerRadius)
            First -> RoundedCornerShape(topStart = GroupCornerRadius, topEnd = GroupCornerRadius)
            Middle -> RectangleShape
            Last -> RoundedCornerShape(bottomStart = GroupCornerRadius, bottomEnd = GroupCornerRadius)
        }

    val showsDivider: Boolean get() = this == First || this == Middle

    companion object {
        fun of(index: Int, size: Int): GroupPosition = when {
            size <= 1 -> Single
            index == 0 -> First
            index == size - 1 -> Last
            else -> Middle
        }
    }
}

/** Clips and fills a row so consecutive rows look like one rounded card. */
fun Modifier.groupedRow(position: GroupPosition, color: Color): Modifier =
    clip(position.shape).background(color)

@Composable
fun SectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 20.dp, bottom = 6.dp)
            .heightIn(min = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = LocalAppColors.current.secondaryLabel,
            modifier = Modifier.weight(1f),
        )
        trailing?.invoke(this)
    }
}

@Composable
fun SectionFooter(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = LocalAppColors.current.secondaryLabel,
        modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp),
    )
}

@Composable
fun GroupDivider(startIndent: Dp = 16.dp, modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier.padding(start = startIndent),
        thickness = 0.5.dp,
        color = LocalAppColors.current.separator,
    )
}

/** An iOS "inset grouped" section for static content. */
@Composable
fun InsetGroup(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        if (header != null) SectionHeader(header)
        Surface(
            shape = RoundedCornerShape(GroupCornerRadius),
            color = LocalAppColors.current.card,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(content = content)
        }
        if (footer != null) SectionFooter(footer)
    }
}

@Composable
fun GroupRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    titleColor: Color = LocalAppColors.current.label,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    showChevron: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val colors = LocalAppColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .heightIn(min = 48.dp)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        leading?.invoke()
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, color = titleColor, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = colors.secondaryLabel, maxLines = 2, overflow = TextOverflow.Ellipsis)
            }
        }
        trailing?.invoke()
        if (showChevron) {
            Icon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = colors.tertiaryLabel,
                modifier = Modifier.size(22.dp),
            )
        }
    }
}

/** Rounded-square colored icon, like the ones in iOS Settings. */
@Composable
fun IconBadge(icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(RoundedCornerShape(7.dp))
            .background(color),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(19.dp))
    }
}
