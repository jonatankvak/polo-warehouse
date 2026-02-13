package com.polo.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.polo.domain.model.PalletStatus
import com.polo.domain.model.PalletStatus.CREATED
import com.polo.domain.model.PalletStatus.READY
import com.polo.domain.model.PalletStatus.TRANSPORT

@Composable
fun PalletStatusChip(
    status: PalletStatus,
    modifier: Modifier = Modifier,
    size: PalletStatusChipSize = PalletStatusChipSize.Medium
) {
    val style = when (status) {
        CREATED -> StatusStyle(
            label = "Created",
            icon = Icons.Default.Inventory2,
            container = Color(0xFFCFFAFE),
            content = Color(0xFF155E75)
        )
        READY -> StatusStyle(
            label = "Ready",
            icon = Icons.Default.TaskAlt,
            container = Color(0xFFC2F0D6),
            content = Color(0xFF008542)
        )
        TRANSPORT -> StatusStyle(
            label = "Transport",
            icon = Icons.Default.LocalShipping,
            container = Color(0xFFFDE68A),
            content = Color(0xFFB45309)
        )
    }

    val horizontalPadding = when (size) {
        PalletStatusChipSize.Small -> 8.dp
        PalletStatusChipSize.Medium -> 10.dp
    }
    val verticalPadding = when (size) {
        PalletStatusChipSize.Small -> 4.dp
        PalletStatusChipSize.Medium -> 6.dp
    }
    val spacing = when (size) {
        PalletStatusChipSize.Small -> 4.dp
        PalletStatusChipSize.Medium -> 6.dp
    }
    val iconSize = when (size) {
        PalletStatusChipSize.Small -> 14.dp
        PalletStatusChipSize.Medium -> 16.dp
    }

    Row(
        modifier = modifier
            .background(color = style.container, shape = RoundedCornerShape(999.dp))
            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = style.icon,
            contentDescription = null,
            tint = style.content,
            modifier = Modifier.size(iconSize)
        )
        Text(
            text = style.label,
            style = if (size == PalletStatusChipSize.Small) {
                MaterialTheme.typography.labelSmall
            } else {
                MaterialTheme.typography.labelMedium
            },
            color = style.content
        )
    }
}

@Preview
@Composable
private fun PalletStatusCreatedPreview() {
    PalletStatusChip(status = CREATED)
}

private data class StatusStyle(
    val label: String,
    val icon: ImageVector,
    val container: Color,
    val content: Color
)

enum class PalletStatusChipSize {
    Small,
    Medium
}
