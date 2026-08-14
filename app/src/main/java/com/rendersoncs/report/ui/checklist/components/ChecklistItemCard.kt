package com.rendersoncs.report.ui.checklist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rendersoncs.report.R
import com.rendersoncs.report.ui.checklist.ChecklistItemUi
import com.rendersoncs.report.ui.theme.ConformGreen
import com.rendersoncs.report.ui.theme.NonConformRed
import com.rendersoncs.report.ui.theme.NotApplicable
import com.rendersoncs.report.ui.theme.ReportShapes
import com.rendersoncs.report.ui.theme.ResumeWarningContainer
import com.rendersoncs.report.ui.theme.ResumeWarningOn

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistItemCard(
    item: ChecklistItemUi,
    onSelectConformity: (Int) -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    onNote: () -> Unit,
    onEdit: () -> Unit,
    onReset: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ReportShapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(end = 32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 32.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                MediaIcon(
                    icon = Icons.Outlined.PhotoCamera,
                    selected = false,
                    contentDescription = stringResource(R.string.msg_take_image),
                    onClick = onCamera
                )
                Spacer(modifier = Modifier.width(8.dp))
                BadgedBox(
                    badge = {
                        if (item.hasPhoto) {
                            Badge { Text("1") }
                        }
                    }
                ) {
                    MediaIcon(
                        icon = Icons.Outlined.PhotoLibrary,
                        selected = item.hasPhoto,
                        contentDescription = stringResource(R.string.msg_select_from_gallery),
                        onClick = onGallery
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                MediaIcon(
                    icon = Icons.Outlined.ChatBubbleOutline,
                    selected = item.hasNote,
                    contentDescription = stringResource(R.string.label_observation),
                    onClick = onNote
                )
                Spacer(modifier = Modifier.weight(1f))
                ConformityButton(
                    label = stringResource(R.string.radio_according),
                    selected = item.conformity == ChecklistItemUi.C,
                    selectedColor = ConformGreen,
                    onClick = { onSelectConformity(ChecklistItemUi.C) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConformityButton(
                    label = stringResource(R.string.radio_not_applicable),
                    selected = item.conformity == ChecklistItemUi.NA,
                    selectedColor = NotApplicable,
                    onClick = { onSelectConformity(ChecklistItemUi.NA) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ConformityButton(
                    label = stringResource(R.string.radio_not_according),
                    selected = item.conformity == ChecklistItemUi.NC,
                    selectedColor = NonConformRed,
                    onClick = { onSelectConformity(ChecklistItemUi.NC) }
                )
            }
            if (item.conformity == ChecklistItemUi.NC && item.hasNote) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(ReportShapes.small)
                        .background(ResumeWarningContainer)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.WarningAmber,
                        contentDescription = null,
                        tint = ResumeWarningOn,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodySmall,
                        color = ResumeWarningOn
                    )
                }
            }
        }
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert,
                        contentDescription = stringResource(R.string.checklist_edit_item),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.checklist_edit_item)) },
                        onClick = {
                            menuExpanded = false
                            onEdit()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.checklist_reset_item)) },
                        onClick = {
                            menuExpanded = false
                            onReset()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(stringResource(R.string.checklist_remove_item)) },
                        onClick = {
                            menuExpanded = false
                            onRemove()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaIcon(
    icon: ImageVector,
    selected: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(
                if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.secondaryContainer
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (selected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.primary
            },
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun ConformityButton(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val borderColor = if (selected) selectedColor else MaterialTheme.colorScheme.outline
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(
                width = if (selected) 2.dp else 1.dp,
                color = borderColor,
                shape = CircleShape
            )
            .background(if (selected) selectedColor.copy(alpha = 0.12f) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = if (selected) selectedColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
