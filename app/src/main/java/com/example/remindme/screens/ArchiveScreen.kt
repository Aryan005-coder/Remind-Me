package com.example.remindme.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remindme.Room.RemainderEntity
import com.example.remindme.ui.DashboardViewModel
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.screens.PinVerificationDialog
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.draw.blur
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchiveScreen(
    viewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lockPin by settingsViewModel.lockPin.collectAsState()
    var reminderToVerifyPin by remember { mutableStateOf<RemainderEntity?>(null) }

    val dark = LocalDarkTheme.current
    val context = LocalContext.current

    // Colours that match the rest of the app
    val bg       = if (dark) Color(0xFF0D0D0F) else Color(0xFFF2F2F7)
    val card     = if (dark) Color(0xFF1C1C1E) else Color(0xFFFFFFFF)
    val accent   = Color(0xFF5338D5)
    val textPri  = if (dark) Color(0xFFF5F5F7) else Color(0xFF000000)
    val textMut  = Color(0xFF8E8E93)
    val border   = if (dark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)

    val archived by viewModel.archivedReminders.collectAsState()

    // Refresh on first entry
    LaunchedEffect(Unit) { viewModel.loadArchivedReminders() }

    // Per-item delete confirmation state
    var confirmDeleteItem by remember { mutableStateOf<RemainderEntity?>(null) }

    Scaffold(
        containerColor = bg,
        modifier = modifier,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bg)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(card, CircleShape)
                        .border(1.dp, border, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Back",
                        tint = textPri,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Archive",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = textPri,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${archived.size} message${if (archived.size != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = textMut,
                    fontWeight = FontWeight.Medium
                )
            }

            HorizontalDivider(color = border, thickness = 0.5.dp)

            // ── List / Empty state ───────────────────────────────────────────
            if (archived.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🗂️", fontSize = 56.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Nothing archived yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPri
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Long-press the three-dot menu on any\nmessage and tap Archive.",
                            fontSize = 14.sp,
                            color = textMut,
                            textAlign = TextAlign.Center,
                            lineHeight = 20.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(archived, key = { it.id }) { reminder ->
                        ArchiveCard(
                            reminder = reminder,
                            cardColor = card,
                            borderColor = border,
                            accentColor = accent,
                            textPrimary = textPri,
                            textMuted = textMut,
                            customCategories = emptyList(), // pass if needed
                            isLocked = reminder.isLocked,
                            lockPin = lockPin,
                            onUnlockClick = { reminderToVerifyPin = reminder },
                            onRestore = { viewModel.restoreReminder(reminder) },
                            onDelete = { confirmDeleteItem = reminder },
                            onUpdateReminder = { updated ->
                                viewModel.updateReminder(context, updated)
                            }
                        )
                    }
                }
            }
        }
    }

    // ── Permanent delete confirmation dialog ─────────────────────────────────
    confirmDeleteItem?.let { item ->
        AlertDialog(
            onDismissRequest = { confirmDeleteItem = null },
            containerColor = card,
            title = {
                Text(
                    "Delete permanently?",
                    fontWeight = FontWeight.Bold,
                    color = textPri
                )
            },
            text = {
                Text(
                    "This message will be removed forever and cannot be restored.",
                    color = textMut,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteReminder(context, item)
                        viewModel.loadArchivedReminders()
                        confirmDeleteItem = null
                    }
                ) {
                    Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteItem = null }) {
                    Text("Cancel", color = textMut)
                }
            }
        )
    }

    // PIN Verification Dialog for Locked Messages
    if (reminderToVerifyPin != null) {
        PinVerificationDialog(
            correctPin = lockPin ?: "",
            onDismiss = { reminderToVerifyPin = null },
            onCorrectPin = {
                val reminder = reminderToVerifyPin!!
                viewModel.updateReminder(context, reminder.copy(isLocked = false))
                reminderToVerifyPin = null
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ArchiveCard(
    reminder: RemainderEntity,
    cardColor: Color,
    borderColor: Color,
    accentColor: Color,
    textPrimary: Color,
    textMuted: Color,
    customCategories: List<Pair<String, String>>,
    isLocked: Boolean,
    lockPin: String?,
    onUnlockClick: () -> Unit,
    onRestore: () -> Unit,
    onDelete: () -> Unit,
    onUpdateReminder: (RemainderEntity) -> Unit
) {
    val context = LocalContext.current
    val dark = LocalDarkTheme.current
    var showActionDialog by remember { mutableStateOf(false) }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Reminder Actions", fontWeight = FontWeight.Bold, color = textPrimary) },
            containerColor = cardColor,
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showActionDialog = false
                                onRestore()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Default.DateRange, contentDescription = "Restore", tint = accentColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Restore to Timeline", color = textPrimary, fontSize = 16.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showActionDialog = false
                                onDelete()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Delete Permanently", color = textPrimary, fontSize = 16.sp)
                    }
                    if (!reminder.isLocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showActionDialog = false
                                    if (lockPin.isNullOrEmpty()) {
                                        android.widget.Toast.makeText(context, "Please set a lock PIN in Settings first!", android.widget.Toast.LENGTH_LONG).show()
                                    } else {
                                        onUpdateReminder(reminder.copy(isLocked = true))
                                    }
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Lock",
                                tint = accentColor
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Lock Message",
                                color = textPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Close", color = textMuted)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isLocked) {
                        onUnlockClick()
                    }
                },
                onLongClick = {
                    if (isLocked) {
                        onUnlockClick()
                    } else {
                        showActionDialog = true
                    }
                }
            ),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .then(if (isLocked) Modifier.blur(14.dp) else Modifier)
            ) {
                // Message (with Checklist support)
                val displayedMessage = remember(reminder.message, isLocked) {
                    if (isLocked) {
                        reminder.message.map { char ->
                            if (char.isWhitespace()) char else '•'
                        }.joinToString("")
                    } else {
                        reminder.message
                    }
                }
                val lines = remember(displayedMessage) { displayedMessage.split("\n") }
                val hasChecklist = remember(lines) { lines.any { it.startsWith("[ ]") || it.startsWith("[x]") } }

                Box(modifier = Modifier.fillMaxWidth()) {
                    if (hasChecklist) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            lines.forEachIndexed { index, line ->
                                val isChecked = line.startsWith("[x]")
                                val isUnchecked = line.startsWith("[ ]")

                                if (isChecked || isUnchecked) {
                                    val displayText = line.substring(3).trim()
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !isLocked) {
                                                val newMarker = if (isChecked) "[ ]" else "[x]"
                                                val updatedLines = lines.toMutableList()
                                                updatedLines[index] = "$newMarker $displayText"
                                                val updatedMessage = updatedLines.joinToString("\n")
                                                onUpdateReminder(reminder.copy(message = updatedMessage))
                                            }
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Checkbox(
                                            checked = isChecked,
                                            onCheckedChange = null,
                                            colors = CheckboxDefaults.colors(
                                                checkedColor = accentColor,
                                                uncheckedColor = borderColor,
                                                checkmarkColor = cardColor
                                            ),
                                            modifier = Modifier.size(20.dp),
                                            enabled = !isLocked
                                        )
                                        Text(
                                            text = displayText,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isChecked) textMuted else textPrimary,
                                            lineHeight = 22.sp,
                                            style = if (isChecked) androidx.compose.ui.text.TextStyle(
                                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                            ) else androidx.compose.ui.text.TextStyle.Default
                                        )
                                    }
                                } else {
                                    Text(
                                        text = line,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = textPrimary,
                                        lineHeight = 22.sp
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = displayedMessage,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = textPrimary,
                            lineHeight = 22.sp
                        )
                    }
                }

            // Image (if any)
            if (!reminder.imageUri.isNullOrEmpty() && !isLocked) {
                Spacer(modifier = Modifier.height(10.dp))
                UriImage(
                    uriString = reminder.imageUri,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .combinedClickable(
                            onClick = {},
                            onLongClick = { showActionDialog = true }
                        ),
                    contentScale = ContentScale.Crop,
                    enforceAspectRatio = true
                )
            }

            // Audio (if any)
            if (!reminder.audioPath.isNullOrEmpty() && !isLocked) {
                Spacer(modifier = Modifier.height(10.dp))
                AudioPlayerView(
                    audioPath = reminder.audioPath,
                    accentPrimary = accentColor,
                    borderColor = borderColor,
                    textMuted = textMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom row: meta on left, actions on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!reminder.tag.isNullOrEmpty()) {
                        val customCategories = remember { loadCustomCategories(context) }
                        val defaultColors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF14B8A6),
                            Color(0xFFA855F7),
                            Color(0xFFF97316),
                            Color(0xFF06B6D4)
                        )
                        val index = customCategories.indexOfFirst { it.first == reminder.tag }
                        val tagColor = if (index != -1) {
                            defaultColors[index % defaultColors.size]
                        } else {
                            when (reminder.tag) {
                                "Shopping" -> if (dark) Color(0xFFD4D4D8) else Color(0xFF3F3F46)
                                "Places" -> if (dark) Color(0xFFA1A1AA) else Color(0xFF71717A)
                                "Work" -> if (dark) Color(0xFF71717A) else Color(0xFFA1A1AA)
                                "Health" -> if (dark) Color(0xFF52525B) else Color(0xFFD4D4D8)
                                else -> textPrimary
                            }
                        }
                        Icon(
                            imageVector = getCategoryIcon(reminder.tag ?: "", customCategories),
                            contentDescription = reminder.tag ?: "Category",
                            tint = if (dark) Color(0xFF8E8E93) else Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (reminder.timeWritten.isNotEmpty()) {
                        Text(
                            text = reminder.timeWritten,
                            fontSize = 11.sp,
                            color = textMuted
                        )
                    }
                }

                // Restore + Delete buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Restore button
                    Box(
                        modifier = Modifier
                            .height(24.dp)
                            .border(1.dp, textPrimary.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(enabled = !isLocked) { onRestore() }
                            .padding(horizontal = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Restore",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }

                    // Permanent delete
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(34.dp),
                        enabled = !isLocked
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete permanently",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        if (isLocked) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = textPrimary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to Reveal",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            }
        }
    }
}
}
