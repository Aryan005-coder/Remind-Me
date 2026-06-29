package com.example.remindme.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import android.util.LruCache
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.MoreVert
import android.content.ClipData
import android.content.Intent
import android.media.MediaRecorder
import android.media.MediaPlayer
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Pause
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.RepeatMode
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remindme.Room.RemainderEntity
import com.example.remindme.ui.DashboardViewModel
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.ui.theme.LocalLanguage
import com.example.remindme.ui.theme.AppTranslations
import com.example.remindme.ui.theme.getActiveAccentColor
import com.example.remindme.ui.theme.getContrastTextColor
import com.example.remindme.screens.PinVerificationDialog
import androidx.compose.ui.draw.blur
import androidx.compose.material.icons.filled.Lock
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import android.util.Patterns


// ── Adaptive colour palette ────────────────────────────────────────────────
// All UI colours are resolved at composition time so dark/light mode both
// get proper, high-contrast visuals.

data class DashboardColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentPrimary: Color,   // main accent (black in light, white in dark)
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color,
    // Category accent colours (zinc shades, visible on both themes)
    val colorGeneral: Color,
    val colorShopping: Color,
    val colorPlaces: Color,
    val colorWork: Color,
    val colorHealth: Color
)

@Composable
fun rememberDashboardColors(accentColor: String): DashboardColors {
    val dark = LocalDarkTheme.current
    val activeAccent = getActiveAccentColor(accentColor, dark)
    return if (dark) {
        DashboardColors(
            screenBackground  = Color(0xFF0D0D0F),   // near-black grouped background
            cardBackground    = Color(0xFF1C1C1E),   // iOS dark card
            accentPrimary     = activeAccent,   // dynamic pastel accent
            textPrimary       = Color(0xFFF5F5F7),
            textMuted         = Color(0xFF8E8E93),
            borderColor       = Color(0xFF2C2C2E),   // dark separator
            colorGeneral      = activeAccent,
            colorShopping     = Color(0xFFD4D4D8),
            colorPlaces       = Color(0xFFA1A1AA),
            colorWork         = Color(0xFF71717A),
            colorHealth       = Color(0xFF52525B)
        )
    } else {
        DashboardColors(
            screenBackground  = Color(0xFFF2F2F7),   // iOS light grouped background
            cardBackground    = Color(0xFFFFFFFF),
            accentPrimary     = activeAccent,   // dynamic pastel accent
            textPrimary       = Color(0xFF000000),
            textMuted         = Color(0xFF8E8E93),
            borderColor       = Color(0xFFE5E5EA),
            colorGeneral      = activeAccent,
            colorShopping     = Color(0xFF27272A),
            colorPlaces       = Color(0xFF3F3F46),
            colorWork         = Color(0xFF52525B),
            colorHealth       = Color(0xFF71717A)
        )
    }
}

// Custom implementation of Material ContentCopy icon to avoid external dependencies
val IconContentCopy: ImageVector
    get() = ImageVector.Builder(
        name = "ContentCopy",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(fill = SolidColor(Color(0xFF8E8E93))) {
            moveTo(16f, 1f)
            lineTo(4f, 1f)
            curveTo(2.9f, 1f, 2f, 1.9f, 2f, 3f)
            verticalLineTo(17f)
            horizontalLineTo(4f)
            verticalLineTo(3f)
            horizontalLineTo(16f)
            verticalLineTo(1f)
            close()
            moveTo(19f, 5f)
            horizontalLineTo(8f)
            curveTo(6.9f, 5f, 6f, 5.9f, 6f, 7f)
            verticalLineTo(21f)
            curveTo(6f, 22.1f, 6.9f, 23f, 8f, 23f)
            horizontalLineTo(19f)
            curveTo(20.1f, 23f, 21f, 22.1f, 21f, 21f)
            verticalLineTo(7f)
            curveTo(21f, 5.9f, 20.1f, 5f, 19f, 5f)
            close()
            moveTo(19f, 21f)
            horizontalLineTo(8f)
            verticalLineTo(7f)
            horizontalLineTo(19f)
            verticalLineTo(21f)
            close()
        }
    }.build()

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    settingsViewModel: SettingsViewModel,
    savedPhone: String,
    savedName: String,
    onNavigateToProfile: () -> Unit,
    onNavigateToArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lockPin by settingsViewModel.lockPin.collectAsState()
    val localAlarmEnabled by settingsViewModel.localAlarmEnabled.collectAsState()
    var reminderToVerifyPin by remember { mutableStateOf<RemainderEntity?>(null) }
    val accentColorState by settingsViewModel.accentColor.collectAsState()

    val c = rememberDashboardColors(accentColorState)
    val lang = LocalLanguage.current

    val context = LocalContext.current
    val reminders by viewModel.reminders.collectAsState()

    // Inputs inside scheduler
    // Using TextFieldValue so we can control the cursor position (TextRange) explicitly
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var selectedTag by remember { mutableStateOf("General") }

    // List mode toggle – when active, pressing Enter auto-inserts a new "[ ] " item
    var isListMode by remember { mutableStateOf(false) }

    // Search
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    // Categories
    var customCategories by remember { mutableStateOf(loadCustomCategories(context)) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var selectedFilterCategory by remember { mutableStateOf("All") }

    // State to hold reference for item to delete
    var reminderToDelete by remember { mutableStateOf<RemainderEntity?>(null) }
    // State to hold reference for item to edit
    var reminderToEdit by remember { mutableStateOf<RemainderEntity?>(null) }
    var editMessageText by remember { mutableStateOf("") }
    var zoomedImageUri by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(reminderToEdit) {
        editMessageText = reminderToEdit?.message ?: ""
    }

    var showDeleteCategoryDialog by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    LaunchedEffect(reminders.size) {
        if (reminders.isNotEmpty()) {
            listState.animateScrollToItem(reminders.size - 1)
        }
    }

    // State to track crumbling reminders
    val crumblingReminders = remember { mutableStateListOf<Int>() }

    // State to track custom iOS Style Wheel Date & Time Picker visibility
    var showDateTimePicker by remember { mutableStateOf(false) }

    var showProfileRequiredDialog by remember { mutableStateOf(false) }
    var reminderToSchedule by remember { mutableStateOf<RemainderEntity?>(null) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            selectedImageUri = uri.toString()
        }
    }

    var isRecording by remember { mutableStateOf(false) }
    var recordedAudioPath by remember { mutableStateOf<String?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }
    var recordingDuration by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    var recordingTimerJob by remember { mutableStateOf<kotlinx.coroutines.Job?>(null) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            Toast.makeText(context, "Permission to record audio was denied.", Toast.LENGTH_SHORT).show()
        }
    }

    val startRecording = {
        val audioFile = java.io.File(context.cacheDir, "audio_note_${System.currentTimeMillis()}.m4a")
        try {
            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(audioFile.absolutePath)
                prepare()
                start()
            }
            mediaRecorder = recorder
            recordedAudioPath = audioFile.absolutePath
            isRecording = true
            recordingDuration = 0
            
            recordingTimerJob?.cancel()
            recordingTimerJob = scope.launch {
                while (isRecording) {
                    kotlinx.coroutines.delay(1000)
                    recordingDuration += 1
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error starting audio recording", Toast.LENGTH_SHORT).show()
        }
    }

    val stopRecording = {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mediaRecorder = null
            isRecording = false
            recordingTimerJob?.cancel()
            recordingTimerJob = null
        }
    }

    val discardRecordedAudio = {
        recordedAudioPath?.let { path ->
            try {
                val file = java.io.File(path)
                if (file.exists()) {
                    file.delete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        recordedAudioPath = null
    }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        containerColor = c.screenBackground,
        modifier = modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Polished iOS Large Title Header with Collapsible Search
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (isSearchActive) {
                        val focusRequester = remember { FocusRequester() }
                        LaunchedEffect(isSearchActive) {
                            if (isSearchActive) {
                                focusRequester.requestFocus()
                            }
                        }
                        var searchFocused by remember { mutableStateOf(false) }
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "Search reminders...",
                                    color = c.textMuted,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = if (searchFocused || searchQuery.isNotEmpty()) c.accentPrimary else c.textMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        isSearchActive = false
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Close Search",
                                        tint = c.textMuted,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(25.dp))
                                .background(c.cardBackground)
                                .border(1.dp, if (searchFocused) c.accentPrimary else c.borderColor, RoundedCornerShape(25.dp))
                                .focusRequester(focusRequester)
                                .onFocusChanged { searchFocused = it.isFocused },
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = c.textPrimary,
                                unfocusedTextColor = c.textPrimary,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )
                        )
                    } else {
                        Row {
                            Text(
                                text = "Back",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = c.textPrimary,
                                letterSpacing = (-1).sp
                            )
                            Text(
                                text = "Note",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = c.textMuted,
                                letterSpacing = (-1).sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = { isSearchActive = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(c.cardBackground, CircleShape)
                                    .border(1.dp, c.borderColor, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = c.accentPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }



                            IconButton(
                                onClick = onNavigateToProfile,
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(c.cardBackground, CircleShape)
                                    .border(1.dp, c.borderColor, CircleShape)
                            ) {
                                if (savedName.isNotBlank()) {
                                    Text(
                                        text = savedName.trim().first().uppercaseChar().toString(),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = c.accentPrimary
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profile",
                                        tint = c.accentPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Category filter chips row (top)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 16.dp)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val defaultTags = listOf("General", "Shopping", "Places", "Work", "Health")
                    val allCategories = listOf("All") + defaultTags + customCategories.map { it.first }
                    allCategories.forEach { category ->
                        val isSelected = selectedFilterCategory == category
                        val tagColor = getCategoryColor(category, c, customCategories)
                        val isCustom = category !in defaultTags && category != "All"

                        val chipBg = if (isSelected) c.accentPrimary else c.cardBackground
                        val chipText = if (isSelected) getContrastTextColor(accentColorState, LocalDarkTheme.current, c.screenBackground, Color.White) else c.textPrimary
                        val chipBorder = if (isSelected) c.accentPrimary else c.borderColor

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(chipBg)
                                .border(1.dp, chipBorder, RoundedCornerShape(16.dp))
                                .combinedClickable(
                                    onClick = { selectedFilterCategory = category },
                                    onLongClick = if (isCustom) {{ showDeleteCategoryDialog = category }} else null
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (category != "All") {
                                Icon(
                                    imageVector = getCategoryIcon(category, customCategories),
                                    contentDescription = category,
                                    tint = if (isSelected) c.screenBackground else tagColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Text(
                                text = category,
                                color = chipText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Reminders Timeline with Buttery-Smooth Animation
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                ) {
                    val filteredReminders by remember(reminders, searchQuery, selectedFilterCategory) {
                        derivedStateOf {
                            val queryFiltered = if (searchQuery.isBlank()) reminders
                            else reminders.filter {
                                it.message.contains(searchQuery, ignoreCase = true) ||
                                (it.tag?.contains(searchQuery, ignoreCase = true) == true)
                            }
                            if (selectedFilterCategory == "All") queryFiltered
                            else queryFiltered.filter { it.tag == selectedFilterCategory }
                        }
                    }

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {

                        if (filteredReminders.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = if (searchQuery.isBlank()) AppTranslations.getString("timeline_clean", lang)
                                                   else "${AppTranslations.getString("no_reminders", lang)} \"$searchQuery\".",
                                            color = c.textMuted,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(
                                items = filteredReminders,
                                key = { it.id }
                            ) { reminder ->
                                val isCrumbling = crumblingReminders.contains(reminder.id)
                                val tagColor = when (reminder.tag) {
                                    "Shopping" -> c.colorShopping
                                    "Places" -> c.colorPlaces
                                    "Work" -> c.colorWork
                                    "Health" -> c.colorHealth
                                    else -> c.colorGeneral
                                }

                                val dismissState = rememberSwipeToDismissBoxState(
                                    confirmValueChange = { value ->
                                        when (value) {
                                            SwipeToDismissBoxValue.StartToEnd -> {
                                                reminderToDelete = reminder
                                                false
                                            }
                                            SwipeToDismissBoxValue.EndToStart -> {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                                val clip = android.content.ClipData.newPlainText("Reminder Message", reminder.message)
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                                                false
                                            }
                                            else -> false
                                        }
                                    }
                                )

                                CrumbleWrapper(
                                    isCrumbling = isCrumbling,
                                    tagColor = tagColor,
                                    onAnimationFinished = {
                                        viewModel.deleteReminder(context, reminder)
                                        crumblingReminders.remove(reminder.id)
                                    }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .animateItem()
                                            .padding(vertical = 2.dp)
                                    ) {
                                        SwipeToDismissBox(
                                            state = dismissState,
                                            backgroundContent = {
                                                val direction = dismissState.dismissDirection
                                                val color = when (direction) {
                                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFFEE2E2)
                                                    SwipeToDismissBoxValue.EndToStart -> c.cardBackground
                                                    else -> Color.Transparent
                                                }
                                                val alignment = when (direction) {
                                                    SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                                                    SwipeToDismissBoxValue.EndToStart -> Alignment.CenterEnd
                                                    else -> Alignment.Center
                                                }
                                                val icon = when (direction) {
                                                    SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Delete
                                                    SwipeToDismissBoxValue.EndToStart -> IconContentCopy
                                                    else -> null
                                                }
                                                val iconColor = when (direction) {
                                                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFFEF4444)
                                                    SwipeToDismissBoxValue.EndToStart -> c.accentPrimary
                                                    else -> Color.Transparent
                                                }

                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(color)
                                                        .padding(horizontal = 20.dp),
                                                    contentAlignment = alignment
                                                ) {
                                                    if (icon != null) {
                                                        Icon(
                                                            imageVector = icon,
                                                            contentDescription = null,
                                                            tint = iconColor,
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    }
                                                }
                                            },
                                            content = {
                                                TimelineReminderItem(
                                                    reminder = reminder,
                                                    colors = c,
                                                    lang = lang,
                                                    customCategories = customCategories,
                                                    isLocked = reminder.isLocked,
                                                    lockPin = lockPin,
                                                    onUnlockClick = {
                                                        reminderToVerifyPin = reminder
                                                    },
                                                    onDelete = { reminderToDelete = reminder },
                                                    onSetAlertClick = {
                                                        if (!localAlarmEnabled) {
                                                            Toast.makeText(context, "Please enable Local Alarm in Settings first!", Toast.LENGTH_LONG).show()
                                                        } else {
                                                            reminderToSchedule = reminder
                                                            showDateTimePicker = true
                                                        }
                                                    },
                                                    onUpdateReminder = { updated ->
                                                        viewModel.updateReminder(context, updated)
                                                    },
                                                    onImageClick = { zoomedImageUri = it },
                                                    onEditClick = {
                                                        reminderToEdit = reminder
                                                    },
                                                    onShareClick = {
                                                        val sendIntent = Intent().apply {
                                                            action = Intent.ACTION_SEND
                                                            putExtra(Intent.EXTRA_TEXT, reminder.message)
                                                            type = "text/plain"
                                                        }
                                                        val shareIntent = Intent.createChooser(sendIntent, null)
                                                        context.startActivity(shareIntent)
                                                    },
                                                    onArchiveClick = {
                                                        viewModel.archiveReminder(context, reminder)
                                                    },
                                                    onStarClick = {
                                                        viewModel.toggleStar(context, reminder)
                                                    }
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Floating iMessage-Style Bottom Composer
                Surface(
                    color = Color.Transparent,
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(0.dp)
                        .border(
                            BorderStroke(0.dp, Color.Transparent),
                            RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                    ) {
                        // Category Chips Slider with custom categories and Add button
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val defaultTags = listOf("General", "Shopping", "Places", "Work", "Health")
                            val tags = defaultTags + customCategories.map { it.first }
                            tags.forEach { tag ->
                                val isSelected = selectedTag == tag
                                val isCustom = tag !in defaultTags
                                val tagColor = getCategoryColor(tag, c, customCategories)

                                val chipBg = if (isSelected) c.accentPrimary else c.screenBackground
                                val chipText = if (isSelected) getContrastTextColor(accentColorState, LocalDarkTheme.current, c.screenBackground, Color.White) else c.textPrimary
                                val chipBorder = if (isSelected) c.accentPrimary else c.borderColor

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(chipBg)
                                        .border(1.dp, chipBorder, RoundedCornerShape(16.dp))
                                        .combinedClickable(
                                            onClick = { selectedTag = tag },
                                            onLongClick = if (isCustom) {{ showDeleteCategoryDialog = tag }} else null
                                        )
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(tag, customCategories),
                                        contentDescription = tag,
                                        tint = if (isSelected) c.screenBackground else tagColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Text(
                                        text = tag,
                                        color = if (isSelected) c.screenBackground else c.textPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Add custom category button (+)
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(c.screenBackground)
                                    .border(1.dp, c.borderColor, RoundedCornerShape(16.dp))
                                    .clickable { showAddCategoryDialog = true }
                                    .padding(10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Category",
                                    tint = c.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        // Image Preview (Animated)
                        AnimatedVisibility(
                            visible = selectedImageUri != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            selectedImageUri?.let { uri ->
                                Box(
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                ) {
                                    UriImage(
                                        uriString = uri,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImageUri = null },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(18.dp)
                                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove Image",
                                            tint = Color.White,
                                            modifier = Modifier.size(10.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Audio Preview (Animated)
                        AnimatedVisibility(
                            visible = recordedAudioPath != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            recordedAudioPath?.let { path ->
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 12.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(c.borderColor.copy(alpha = 0.2f))
                                        .border(1.dp, c.borderColor, RoundedCornerShape(12.dp))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Note Attached",
                                        tint = c.accentPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Voice note attached",
                                        fontSize = 13.sp,
                                        color = c.textPrimary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { discardRecordedAudio() },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove Audio",
                                            tint = c.textMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Input control bar (iMessage style)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // Text composer pill using BasicTextField for full layout control
                            val listIconTint = if (isListMode) c.accentPrimary else c.textMuted
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(22.dp))
                                    .background(c.screenBackground)
                                    .border(
                                        width = 1.dp,
                                        color = if (isRecording) Color.Red else if (isListMode) c.accentPrimary else c.borderColor,
                                        shape = RoundedCornerShape(22.dp)
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                if (isRecording) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Pulsing recording dot
                                            val infiniteTransition = rememberInfiniteTransition()
                                            val pulseAlpha by infiniteTransition.animateFloat(
                                                initialValue = 0.3f,
                                                targetValue = 1f,
                                                animationSpec = infiniteRepeatable(
                                                    animation = tween(1000, easing = LinearEasing),
                                                    repeatMode = RepeatMode.Reverse
                                                )
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(10.dp)
                                                    .clip(CircleShape)
                                                    .background(Color.Red.copy(alpha = pulseAlpha))
                                            )
                                            Text(
                                                text = "Recording... ${String.format("%d:%02d", recordingDuration / 60, recordingDuration % 60)}",
                                                color = c.textPrimary,
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Cancel/Discard button
                                            Text(
                                                text = "Cancel",
                                                color = c.textMuted,
                                                fontSize = 14.sp,
                                                modifier = Modifier.clickable {
                                                    stopRecording()
                                                    discardRecordedAudio()
                                                }
                                            )

                                            // Stop & Save button
                                            IconButton(
                                                onClick = { stopRecording() },
                                                modifier = Modifier
                                                    .size(28.dp)
                                                    .background(Color.Red, CircleShape)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Stop,
                                                    contentDescription = "Stop",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        // Expandable text area — uses TextFieldValue so cursor
                                        // position is explicitly set to end after list insertions
                                        androidx.compose.foundation.text.BasicTextField(
                                            value = message,
                                            onValueChange = { newVal ->
                                                if (isListMode) {
                                                    // Detect that a newline was just appended
                                                    val addedNewline = newVal.text.length > message.text.length &&
                                                            newVal.text.endsWith("\n")
                                                    if (addedNewline) {
                                                        val newText = newVal.text + "[ ] "
                                                        message = TextFieldValue(
                                                            text = newText,
                                                            selection = TextRange(newText.length)
                                                        )
                                                    } else {
                                                        message = newVal
                                                    }
                                                } else {
                                                    message = newVal
                                                }
                                            },
                                            textStyle = androidx.compose.ui.text.TextStyle(
                                                color = c.textPrimary,
                                                fontSize = 14.sp
                                            ),
                                            cursorBrush = SolidColor(c.accentPrimary),
                                            maxLines = 5,
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(end = 8.dp),
                                            decorationBox = { innerTextField ->
                                                Box(contentAlignment = Alignment.CenterStart) {
                                                    if (message.text.isEmpty()) {
                                                        Text(
                                                            text = if (isListMode) "Tap Enter to add items..." else "Write a reminder...",
                                                            color = c.textMuted,
                                                            fontSize = 14.sp
                                                        )
                                                    }
                                                    innerTextField()
                                                }
                                            }
                                        )

                                        // List-mode toggle icon
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.List,
                                            contentDescription = "Toggle List Mode",
                                            tint = listIconTint,
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    isListMode = !isListMode
                                                    if (isListMode) {
                                                        // Seed the first item; cursor goes to end
                                                        val cur = message.text
                                                        val newText = when {
                                                            cur.isEmpty() -> "[ ] "
                                                            cur.trimEnd().endsWith("]") -> cur
                                                            cur.endsWith("\n") -> cur + "[ ] "
                                                            else -> cur + "\n[ ] "
                                                        }
                                                        message = TextFieldValue(
                                                            text = newText,
                                                            selection = TextRange(newText.length)
                                                        )
                                                    }
                                                }
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Add Photo icon
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add Photo",
                                            tint = c.textMuted,
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) { imagePickerLauncher.launch(arrayOf("image/*")) }
                                        )

                                        Spacer(modifier = Modifier.width(10.dp))

                                        // Record Audio Icon
                                        Icon(
                                            imageVector = Icons.Default.Mic,
                                            contentDescription = "Record Audio",
                                            tint = c.textMuted,
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clickable(
                                                    interactionSource = remember { MutableInteractionSource() },
                                                    indication = null
                                                ) {
                                                    val hasRecordPermission = androidx.core.content.ContextCompat.checkSelfPermission(
                                                        context,
                                                        android.Manifest.permission.RECORD_AUDIO
                                                    ) == android.content.pm.PackageManager.PERMISSION_GRANTED

                                                    if (hasRecordPermission) {
                                                        startRecording()
                                                    } else {
                                                        audioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
                                                    }
                                                }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            val isEnabled = message.text.isNotBlank() || selectedImageUri != null || recordedAudioPath != null
                            val sendScale by animateFloatAsState(
                                targetValue = if (isEnabled) 1.05f else 1.0f,
                                animationSpec = tween(durationMillis = 150)
                            )

                            IconButton(
                                onClick = {  // send button
                                    if (savedPhone.isEmpty()) {
                                        showProfileRequiredDialog = true
                                        return@IconButton
                                    }

                                    viewModel.addReminder(
                                        context = context,
                                        phoneNumber = savedPhone,
                                        message = message.text,
                                        date = "",
                                        time = "",
                                        imageUri = selectedImageUri,
                                        tag = selectedTag,
                                        audioPath = recordedAudioPath ?: ""
                                    )
                                    message = TextFieldValue("")
                                    selectedDate = ""
                                    selectedTime = ""
                                    selectedImageUri = null
                                    recordedAudioPath = null
                                    selectedTag = "General"
                                    isListMode = false
                                },
                                enabled = isEnabled,
                                modifier = Modifier
                                    .scale(sendScale)
                                    .size(38.dp)
                                    .background(
                                        if (isEnabled) c.accentPrimary else c.screenBackground,
                                        CircleShape
                                    )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send",
                                    tint = if (isEnabled) getContrastTextColor(accentColorState, LocalDarkTheme.current, c.screenBackground, Color.White) else c.textMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Custom Scrollable Date/Time Picker Dialog (iOS Style)
            if (showDateTimePicker) {
                AlertDialog(
                    onDismissRequest = { 
                        showDateTimePicker = false
                        reminderToSchedule = null
                    },
                    title = {
                        Text(
                            text = "Select Date & Time",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = c.textPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val currentCal = Calendar.getInstance()
                            reminderToSchedule?.let { reminder ->
                                if (reminder.date.isNotEmpty() && reminder.time.isNotEmpty()) {
                                    val sdf = SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault())
                                    try {
                                        val parsed = sdf.parse("${reminder.date} ${reminder.time}")
                                        if (parsed != null) {
                                            currentCal.time = parsed
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                            val currentYearVal = currentCal.get(Calendar.YEAR)
                            
                            // Date States
                            var yearIdx by remember { mutableStateOf(0) } // Default current year
                            val years = (currentYearVal..(currentYearVal + 5)).map { it.toString() }
                            
                            var monthIdx by remember { mutableStateOf(currentCal.get(Calendar.MONTH)) }
                            val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                            
                            val selectedYear = years[yearIdx].toInt()
                            val maxDays = when (monthIdx + 1) {
                                4, 6, 9, 11 -> 30
                                2 -> if ((selectedYear % 4 == 0 && selectedYear % 100 != 0) || (selectedYear % 400 == 0)) 29 else 28
                                else -> 31
                            }
                            
                            val days = (1..maxDays).map { it.toString() }
                            var dayIdx by remember { 
                                val currentDay = currentCal.get(Calendar.DAY_OF_MONTH)
                                mutableStateOf((currentDay - 1).coerceIn(0, maxDays - 1)) 
                            }
                            // Adjust day index if it exceeds max days of the selected month
                            LaunchedEffect(maxDays) {
                                if (dayIdx >= maxDays) {
                                    dayIdx = maxDays - 1
                                }
                            }

                            // Time States
                            val hours = (1..12).map { String.format("%02d", it) }
                            val minutes = (0..59).map { String.format("%02d", it) }
                            val amPm = listOf("AM", "PM")

                            val initialHour = currentCal.get(Calendar.HOUR) // 0-11
                            val initialHour12 = if (initialHour == 0) 12 else initialHour
                            val initialMin = currentCal.get(Calendar.MINUTE)
                            val initialAmPm = if (currentCal.get(Calendar.AM_PM) == Calendar.AM) 0 else 1

                            var hourIdx by remember { mutableStateOf(initialHour12 - 1) }
                            var minIdx by remember { mutableStateOf(initialMin) }
                            var amPmIdx by remember { mutableStateOf(initialAmPm) }

                            // Date Selection Heading
                            Text(
                                text = "DATE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.textMuted,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 4.dp, bottom = 6.dp),
                                letterSpacing = 0.5.sp
                            )
                            // Date Drums
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(c.screenBackground, RoundedCornerShape(16.dp))
                                    .border(1.dp, c.borderColor, RoundedCornerShape(16.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WheelPicker(
                                    items = days,
                                    initialIndex = dayIdx.coerceIn(0, days.size - 1),
                                    onItemSelected = { dayIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1f)
                                )
                                WheelPicker(
                                    items = months,
                                    initialIndex = monthIdx,
                                    onItemSelected = { monthIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1.2f)
                                )
                                WheelPicker(
                                    items = years,
                                    initialIndex = yearIdx,
                                    onItemSelected = { yearIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1.2f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Time Selection Heading
                            Text(
                                text = "TIME",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.textMuted,
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 4.dp, bottom = 6.dp),
                                letterSpacing = 0.5.sp
                            )
                            // Time Drums
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(c.screenBackground, RoundedCornerShape(16.dp))
                                    .border(1.dp, c.borderColor, RoundedCornerShape(16.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                WheelPicker(
                                    items = hours,
                                    initialIndex = hourIdx,
                                    onItemSelected = { hourIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1f)
                                )
                                WheelPicker(
                                    items = minutes,
                                    initialIndex = minIdx,
                                    onItemSelected = { minIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1f)
                                )
                                WheelPicker(
                                    items = amPm,
                                    initialIndex = amPmIdx,
                                    onItemSelected = { amPmIdx = it },
                                    colors = c,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Calculate results dynamically
                            val selectedHour = hours[hourIdx]
                            val selectedMin = minutes[minIdx]
                            val selectedAmPm = amPm[amPmIdx]

                            val hour24 = when (selectedAmPm) {
                                "AM" -> if (selectedHour == "12") "00" else selectedHour
                                "PM" -> if (selectedHour == "12") "12" else (selectedHour.toInt() + 12).toString()
                                else -> selectedHour
                            }
                            selectedTime = "$hour24:$selectedMin"
                            
                            val safeDayIdx = dayIdx.coerceIn(0, days.size - 1)
                            val finalDay = if (days.isNotEmpty()) days[safeDayIdx] else "1"
                            selectedDate = "$finalDay/${monthIdx + 1}/${years[yearIdx]}"
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                reminderToSchedule?.let { reminder ->
                                    viewModel.updateReminder(
                                        context = context,
                                        reminder = reminder.copy(date = selectedDate, time = selectedTime)
                                    )
                                }
                                showDateTimePicker = false
                                reminderToSchedule = null
                            }
                        ) {
                            Text("Done", color = c.accentPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    containerColor = c.cardBackground,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            // Custom Category Dialog
            if (showAddCategoryDialog) {
                var newCategoryName by remember { mutableStateOf("") }
                var selectedIconName by remember { mutableStateOf("Favorite") }
                val iconsList = listOf(
                    "Favorite" to Icons.Default.Favorite,
                    "ShoppingCart" to Icons.Default.ShoppingCart,
                    "Place" to Icons.Default.Place,
                    "Build" to Icons.Default.Build,
                    "Person" to Icons.Default.Person,
                    "Home" to Icons.Default.Home,
                    "Star" to Icons.Default.Star,
                    "Call" to Icons.Default.Call,
                    "Email" to Icons.Default.Email,
                    "Notifications" to Icons.Default.Notifications
                )

                AlertDialog(
                    onDismissRequest = { showAddCategoryDialog = false },
                    title = {
                        Text(
                            text = AppTranslations.getString("add_category", lang),
                            fontWeight = FontWeight.Bold,
                            color = c.textPrimary
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TextField(
                                value = newCategoryName,
                                onValueChange = { newCategoryName = it },
                                placeholder = {
                                    Text(text = AppTranslations.getString("category_name", lang), color = c.textMuted)
                                },
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, c.borderColor, RoundedCornerShape(12.dp)),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = c.textPrimary,
                                    unfocusedTextColor = c.textPrimary,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent
                                )
                            )

                            Text(
                                text = AppTranslations.getString("select_icon", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.textMuted
                            )

                            val chunkedIcons = iconsList.chunked(5)
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                chunkedIcons.forEach { rowIcons ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        rowIcons.forEach { (name, iconVec) ->
                                            val isSelected = selectedIconName == name
                                            IconButton(
                                                onClick = { selectedIconName = name },
                                                modifier = Modifier
                                                    .size(44.dp)
                                                    .background(
                                                        if (isSelected) c.accentPrimary else Color.Transparent,
                                                        CircleShape
                                                    )
                                                    .border(
                                                        1.dp,
                                                        if (isSelected) c.accentPrimary else c.borderColor,
                                                        CircleShape
                                                    )
                                            ) {
                                                Icon(
                                                    imageVector = iconVec,
                                                    contentDescription = name,
                                                    tint = if (isSelected) getContrastTextColor(accentColorState, LocalDarkTheme.current, c.screenBackground, Color.White) else c.textPrimary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        if (rowIcons.size < 5) {
                                            repeat(5 - rowIcons.size) {
                                                Spacer(modifier = Modifier.size(44.dp))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val nameTrimmed = newCategoryName.trim()
                                if (nameTrimmed.isNotEmpty()) {
                                    if (customCategories.none { it.first.equals(nameTrimmed, ignoreCase = true) }) {
                                        val updated = customCategories + (nameTrimmed to selectedIconName)
                                        customCategories = updated
                                        saveCustomCategories(context, updated)
                                    }
                                }
                                showAddCategoryDialog = false
                            }
                        ) {
                            Text("Save", color = c.accentPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddCategoryDialog = false }) {
                            Text(AppTranslations.getString("cancel", lang), color = c.textMuted)
                        }
                    },
                    containerColor = c.cardBackground,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            // Profile setup warning dialog
            if (showProfileRequiredDialog) {
                AlertDialog(
                    onDismissRequest = { showProfileRequiredDialog = false },
                    title = {
                        Text(
                            text = AppTranslations.getString("profile_required_title", lang),
                            fontWeight = FontWeight.Bold,
                            color = c.textPrimary
                        )
                    },
                    text = {
                        Text(
                            text = AppTranslations.getString("profile_required_desc", lang),
                            color = c.textMuted
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                showProfileRequiredDialog = false
                                onNavigateToProfile()
                            }
                        ) {
                            Text(
                                text = AppTranslations.getString("go_to_profile", lang),
                                color = c.accentPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { showProfileRequiredDialog = false }
                        ) {
                            Text(
                                text = AppTranslations.getString("cancel", lang),
                                color = c.textMuted
                            )
                        }
                    },
                    containerColor = c.cardBackground,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Delete confirmation dialog
            if (reminderToDelete != null) {
                AlertDialog(
                    onDismissRequest = { reminderToDelete = null },
                    title = { Text(AppTranslations.getString("delete_title", lang), fontWeight = FontWeight.Bold, color = c.textPrimary) },
                    text = { Text(AppTranslations.getString("delete_desc", lang), color = c.textMuted) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                reminderToDelete?.let {
                                    crumblingReminders.add(it.id)
                                }
                                reminderToDelete = null
                            }
                        ) {
                            Text(
                                text = AppTranslations.getString("delete_confirm", lang),
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { reminderToDelete = null }) {
                            Text(
                                text = AppTranslations.getString("cancel", lang),
                                color = c.textMuted
                            )
                        }
                    },
                    containerColor = c.cardBackground,
                    shape = RoundedCornerShape(20.dp)
                )
            }

            // Edit message dialog
            if (reminderToEdit != null) {
                AlertDialog(
                    onDismissRequest = { reminderToEdit = null },
                    title = {
                        Text(
                            text = "Edit Reminder",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = c.textPrimary,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    text = {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = editMessageText,
                                onValueChange = { editMessageText = it },
                                placeholder = {
                                    Text(
                                        text = "Write a reminder...",
                                        color = c.textMuted,
                                        fontSize = 14.sp
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(c.screenBackground, RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = c.textPrimary,
                                    unfocusedTextColor = c.textPrimary,
                                    focusedBorderColor = c.accentPrimary,
                                    unfocusedBorderColor = c.borderColor,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                ),
                                maxLines = 4
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                reminderToEdit?.let { reminder ->
                                    if (editMessageText.isNotBlank()) {
                                        viewModel.updateReminder(
                                            context = context,
                                            reminder = reminder.copy(message = editMessageText)
                                        )
                                    }
                                }
                                reminderToEdit = null
                            }
                        ) {
                            Text("Save", color = c.accentPrimary, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = { reminderToEdit = null }
                        ) {
                            Text("Cancel", color = c.textMuted)
                        }
                    },
                    containerColor = c.cardBackground,
                    shape = RoundedCornerShape(24.dp)
                )
            }

            // Zoomable Image View Dialog
            if (zoomedImageUri != null) {
                ZoomableImageDialog(
                    uriString = zoomedImageUri!!,
                    onDismiss = { zoomedImageUri = null }
                )
            }

            // Delete Category confirmation dialog
            showDeleteCategoryDialog?.let { categoryToDelete ->
                AlertDialog(
                    onDismissRequest = { showDeleteCategoryDialog = null },
                    containerColor = c.cardBackground,
                    title = {
                        Text(
                            "Delete category?",
                            fontWeight = FontWeight.Bold,
                            color = c.textPrimary
                        )
                    },
                    text = {
                        Text(
                            "\"$categoryToDelete\" will be removed. Messages in this category won't be affected.",
                            color = c.textMuted,
                            lineHeight = 20.sp
                        )
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val updated = customCategories.filter { it.first != categoryToDelete }
                                customCategories = updated
                                saveCustomCategories(context, updated)
                                if (selectedTag == categoryToDelete) selectedTag = "General"
                                if (selectedFilterCategory == categoryToDelete) selectedFilterCategory = "All"
                                showDeleteCategoryDialog = null
                            }
                        ) {
                            Text("Delete", color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteCategoryDialog = null }) {
                            Text("Cancel", color = c.textMuted)
                        }
                    }
                )
            }

            // PIN Verification Dialog for Locked Messages
            if (reminderToVerifyPin != null) {
                PinVerificationDialog(
                    correctPin = lockPin ?: "",
                    accentColor = accentColorState,
                    onDismiss = { reminderToVerifyPin = null },
                    onCorrectPin = {
                        val reminder = reminderToVerifyPin!!
                        viewModel.updateReminder(context, reminder.copy(isLocked = false))
                        reminderToVerifyPin = null
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimelineReminderItem(
    reminder: RemainderEntity,
    colors: DashboardColors,
    lang: String,
    customCategories: List<Pair<String, String>>,
    isLocked: Boolean,
    lockPin: String?,
    onUnlockClick: () -> Unit,
    onDelete: () -> Unit,
    onSetAlertClick: () -> Unit,
    onUpdateReminder: (RemainderEntity) -> Unit,
    onImageClick: (String) -> Unit,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit,
    onArchiveClick: () -> Unit,
    onStarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = colors
    val tagColor = getCategoryColor(reminder.tag ?: "", colors, customCategories)
    val context = LocalContext.current
    var showActionDialog by remember { mutableStateOf(false) }

    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("Reminder Actions", fontWeight = FontWeight.Bold, color = c.textPrimary) },
            containerColor = c.cardBackground,
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
                                onEditClick()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = c.accentPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Edit", color = c.textPrimary, fontSize = 16.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showActionDialog = false
                                onShareClick()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = c.accentPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Share", color = c.textPrimary, fontSize = 16.sp)
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showActionDialog = false
                                onArchiveClick()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = "Archive", tint = c.accentPrimary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Archive", color = c.textPrimary, fontSize = 16.sp)
                    }
                    if (!reminder.isLocked) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showActionDialog = false
                                    if (lockPin.isNullOrEmpty()) {
                                        Toast.makeText(context, "Please set a lock PIN in Settings first!", Toast.LENGTH_LONG).show()
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
                                tint = c.accentPrimary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Lock Message",
                                color = c.textPrimary,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showActionDialog = false }) {
                    Text("Close", color = c.textMuted)
                }
            }
        )
    }

    Card(
        modifier = modifier
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
        colors = CardDefaults.cardColors(containerColor = c.cardBackground),
        border = BorderStroke(1.dp, c.borderColor)
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
                // Message Text / Checklist Items with three-dot overflow menu
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Left side: Checklist or text
                    Box(modifier = Modifier.weight(1f)) {
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
                                                    checkedColor = c.accentPrimary,
                                                    uncheckedColor = c.borderColor,
                                                    checkmarkColor = c.screenBackground
                                                ),
                                                modifier = Modifier.size(20.dp),
                                                enabled = !isLocked
                                            )
                                            LinkifiedText(
                                                text = displayText,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = if (isChecked) c.textMuted else c.textPrimary,
                                                lineHeight = 21.sp,
                                                style = if (isChecked) androidx.compose.ui.text.TextStyle(
                                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                                                ) else androidx.compose.ui.text.TextStyle.Default
                                            )
                                        }
                                    } else {
                                        LinkifiedText(
                                            text = line,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = c.textPrimary,
                                            lineHeight = 21.sp
                                        )
                                    }
                                }
                            }
                        } else {
                            LinkifiedText(
                                text = displayedMessage,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = c.textPrimary,
                                lineHeight = 21.sp
                            )
                        }
                    }

                    // Right side: Star button + Three-dot overflow menu
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = onStarClick,
                            modifier = Modifier.size(24.dp),
                            enabled = !isLocked
                        ) {
                            Icon(
                                imageVector = if (reminder.isStarred) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Star",
                                tint = if (reminder.isStarred) Color(0xFFFFD700) else c.textMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }


                    }
                }

                // Optional Image Preview
                if (!reminder.imageUri.isNullOrEmpty() && !isLocked) {
                    Spacer(modifier = Modifier.height(12.dp))
                    UriImage(
                        uriString = reminder.imageUri,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .combinedClickable(
                                onClick = { onImageClick(reminder.imageUri) },
                                onLongClick = { showActionDialog = true }
                            ),
                        contentScale = ContentScale.Crop,
                        enforceAspectRatio = true
                    )
                }

                // Optional Audio Player
                if (!reminder.audioPath.isNullOrEmpty() && !isLocked) {
                    Spacer(modifier = Modifier.height(12.dp))
                    AudioPlayerView(
                        audioPath = reminder.audioPath,
                        accentPrimary = c.accentPrimary,
                        borderColor = c.borderColor,
                        textMuted = c.textMuted
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                val isScheduled = reminder.date.isNotEmpty() && reminder.time.isNotEmpty()

                // Bottom strip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Bottom-left info: Category Icon and time message was written
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = getCategoryIcon(reminder.tag ?: "", customCategories),
                            contentDescription = reminder.tag ?: "Category",
                            tint = Color(0xFF8E8E93),
                            modifier = Modifier.size(16.dp)
                        )

                        val displayTime = if (reminder.timeWritten.isNotEmpty()) reminder.timeWritten else {
                            remember(reminder.time) { if (reminder.time.isNotEmpty()) formatTimeToDisplay(reminder.time) else "" }
                        }
                        if (displayTime.isNotEmpty()) {
                            Text(
                                text = displayTime,
                                fontSize = 11.sp,
                                color = c.textMuted,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Bottom-right button / duration info
                    if (isScheduled) {
                        val remainingText = getRemainingDurationString(reminder.date, reminder.time, lang)
                        val isPassed = remainingText == AppTranslations.getString("duration_passed", lang)

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isPassed) 
                                        c.borderColor.copy(alpha = 0.5f) 
                                        else 
                                        c.accentPrimary.copy(alpha = 0.1f)
                                )
                                .clickable(enabled = !isLocked) { onSetAlertClick() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Reschedule Alert",
                                tint = if (isPassed) c.textMuted else c.accentPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = remainingText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isPassed) c.textMuted else c.accentPrimary
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(c.accentPrimary.copy(alpha = 0.08f))
                                .clickable(enabled = !isLocked) { onSetAlertClick() }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Set Alert",
                                tint = c.accentPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppTranslations.getString("set_alert", lang),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.accentPrimary
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
                        tint = c.textPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap to Reveal",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                }
            }
        }
    }
}

object ImageCache {
    private val memoryCache = object : LruCache<String, androidx.compose.ui.graphics.ImageBitmap>(20) {}

    fun get(uri: String): androidx.compose.ui.graphics.ImageBitmap? = memoryCache.get(uri)
    fun put(uri: String, bitmap: androidx.compose.ui.graphics.ImageBitmap) {
        memoryCache.put(uri, bitmap)
    }
}

@Composable
fun UriImage(
    uriString: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    enforceAspectRatio: Boolean = false
) {
    val context = LocalContext.current
    // Retrieve cached bitmap if it exists to avoid async loading/flicker
    val cachedBitmap = remember(uriString) { ImageCache.get(uriString) }
    val bitmapState = remember(uriString) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(cachedBitmap) }

    LaunchedEffect(uriString) {
        if (bitmapState.value == null) {
            // Decode entirely off the main thread — this is the #1 scroll-jank fix
            kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
                try {
                    val uri = Uri.parse(uriString)
                    val rawBitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val source = ImageDecoder.createSource(context.contentResolver, uri)
                        // Downsample to max 800px on the longest side — avoids holding huge
                        // textures for every card, keeping GPU memory and upload time low.
                        ImageDecoder.decodeBitmap(source) { decoder, info, _ ->
                            val maxDim = 800
                            val w = info.size.width
                            val h = info.size.height
                            if (w > maxDim || h > maxDim) {
                                val scale = maxDim.toFloat() / maxOf(w, h)
                                decoder.setTargetSize(
                                    (w * scale).toInt().coerceAtLeast(1),
                                    (h * scale).toInt().coerceAtLeast(1)
                                )
                            }
                            decoder.isMutableRequired = false
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val full = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        val maxDim = 800
                        val w = full.width
                        val h = full.height
                        if (w > maxDim || h > maxDim) {
                            val scale = maxDim.toFloat() / maxOf(w, h)
                            Bitmap.createScaledBitmap(
                                full,
                                (w * scale).toInt().coerceAtLeast(1),
                                (h * scale).toInt().coerceAtLeast(1),
                                true
                            ).also { if (it !== full) full.recycle() }
                        } else full
                    }
                    val imageBitmap = rawBitmap.asImageBitmap()
                    ImageCache.put(uriString, imageBitmap)
                    // Switch back to main thread only for the state write
                    kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.Main) {
                        bitmapState.value = imageBitmap
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    bitmapState.value?.let { bitmap ->
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val sizeModifier = if (enforceAspectRatio) {
            Modifier
                .fillMaxWidth()
                .aspectRatio(aspectRatio.coerceIn(0.6f, 1.8f))
        } else {
            Modifier
        }

        Image(
            bitmap = bitmap,
            contentDescription = null,
            modifier = modifier.then(sizeModifier),
            contentScale = contentScale
        )
    }
}

private fun formatTimeToDisplay(time: String): String {
    return try {
        val sdfSource = SimpleDateFormat("HH:mm", Locale.getDefault())
        val sdfDest = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateObj = sdfSource.parse(time)
        if (dateObj != null) sdfDest.format(dateObj) else time
    } catch (e: Exception) {
        time
    }
}

// Particle details for Crumbling effect
data class CrumbleParticle(
    val startX: Float,
    val startY: Float,
    val vx: Float,
    val vy: Float,
    val size: Float,
    val color: Color
)

@Composable
fun CrumbleWrapper(
    isCrumbling: Boolean,
    tagColor: Color,
    onAnimationFinished: () -> Unit,
    content: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize.Zero) }
    val progress = remember { Animatable(0f) }

    val particles = remember(isCrumbling, size) {
        if (isCrumbling && size.width > 0 && size.height > 0) {
            val list = mutableListOf<CrumbleParticle>()
            val rows = 12
            val cols = 12
            val cellWidth = size.width.toFloat() / cols
            val cellHeight = size.height.toFloat() / rows

            val random = java.util.Random()

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val px = c * cellWidth + cellWidth / 2
                    val py = r * cellHeight + cellHeight / 2

                    val vx = (random.nextFloat() - 0.5f) * 400f
                    val vy = (random.nextFloat() - 0.8f) * 350f
                    val pSize = random.nextFloat() * 12f + 4f

                    val color = if (random.nextFloat() > 0.4f) {
                        tagColor.copy(alpha = random.nextFloat() * 0.5f + 0.5f)
                    } else {
                        Color(0xFF8E8E93).copy(alpha = random.nextFloat() * 0.4f + 0.3f)
                    }

                    list.add(CrumbleParticle(px, py, vx, vy, pSize, color))
                }
            }
            list
        } else {
            emptyList()
        }
    }

    LaunchedEffect(isCrumbling) {
        if (isCrumbling) {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
            onAnimationFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                size = coordinates.size
            }
    ) {
        val alpha = if (isCrumbling) 1f - (progress.value * 2f).coerceIn(0f, 1f) else 1f
        Box(modifier = Modifier.graphicsLayer { this.alpha = alpha }) {
            content()
        }

        if (isCrumbling && particles.isNotEmpty()) {
            Canvas(
                modifier = Modifier.matchParentSize()
            ) {
                val p = progress.value
                val gravity = 950f

                particles.forEach { particle ->
                    val t = p * 0.6f
                    val currentX = particle.startX + particle.vx * t
                    val currentY = particle.startY + particle.vy * t + 0.5f * gravity * t * t

                    val currentAlpha = (1f - p).coerceIn(0f, 1f)
                    val currentSize = particle.size * (1f - p * 0.5f)

                    drawCircle(
                        color = particle.color.copy(alpha = particle.color.alpha * currentAlpha),
                        radius = currentSize / 2,
                        center = Offset(currentX, currentY)
                    )
                }
            }
        }
    }
}

// Custom Scrollable vertical WheelPicker Composable (iOS drum style)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    colors: DashboardColors,
    modifier: Modifier = Modifier
) {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)

    val currentIndex = remember { derivedStateOf { lazyListState.firstVisibleItemIndex } }

    LaunchedEffect(currentIndex.value) {
        if (currentIndex.value in items.indices) {
            onItemSelected(currentIndex.value)
        }
    }

    Box(
        modifier = modifier.height(180.dp),
        contentAlignment = Alignment.Center
    ) {
        // Selection indicator lines
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalDivider(color = colors.borderColor, thickness = 1.dp)
            HorizontalDivider(color = colors.borderColor, thickness = 1.dp)
        }

        LazyColumn(
            state = lazyListState,
            flingBehavior = snapFlingBehavior,
            contentPadding = PaddingValues(vertical = 69.dp), // Height / 2 - ItemHeight / 2 -> (180 - 42) / 2
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            items(items.size) { index ->
                val isSelected = currentIndex.value == index
                val scale = if (isSelected) 1.25f else 0.85f
                val alpha = if (isSelected) 1f else 0.4f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = items[index],
                        fontSize = 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = colors.accentPrimary.copy(alpha = alpha),
                        modifier = Modifier.scale(scale)
                    )
                }
            }
        }
    }
}

fun getCategoryIcon(tag: String, customCategories: List<Pair<String, String>> = emptyList()): ImageVector {
    val match = customCategories.firstOrNull { it.first == tag }
    if (match != null) {
        return when (match.second) {
            "Favorite" -> Icons.Default.Favorite
            "ShoppingCart" -> Icons.Default.ShoppingCart
            "Place" -> Icons.Default.Place
            "Build" -> Icons.Default.Build
            "Person" -> Icons.Default.Person
            "Home" -> Icons.Default.Home
            "Star" -> Icons.Default.Star
            "Call" -> Icons.Default.Call
            "Email" -> Icons.Default.Email
            "Notifications" -> Icons.Default.Notifications
            else -> Icons.Default.DateRange
        }
    }
    return when (tag) {
        "Shopping" -> Icons.Default.ShoppingCart
        "Places" -> Icons.Default.Place
        "Work" -> Icons.Default.Build
        "Health" -> Icons.Default.Favorite
        else -> Icons.Default.DateRange
    }
}

fun getCategoryColor(tag: String, colors: DashboardColors, customCategories: List<Pair<String, String>> = emptyList()): Color {
    val defaultColors = listOf(
        Color(0xFF6366F1), // Indigo
        Color(0xFF14B8A6), // Teal
        Color(0xFFA855F7), // Purple
        Color(0xFFF97316), // Orange
        Color(0xFF06B6D4)  // Cyan
    )
    val index = customCategories.indexOfFirst { it.first == tag }
    if (index != -1) {
        return defaultColors[index % defaultColors.size]
    }
    return when (tag) {
        "Shopping" -> colors.colorShopping
        "Places" -> colors.colorPlaces
        "Work" -> colors.colorWork
        "Health" -> colors.colorHealth
        else -> colors.colorGeneral
    }
}

fun loadCustomCategories(context: Context): List<Pair<String, String>> {
    val prefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    val data = prefs.getString("custom_categories", "") ?: ""
    if (data.isEmpty()) return emptyList()
    return data.split(";").mapNotNull {
        val parts = it.split(",")
        if (parts.size == 2) parts[0] to parts[1] else null
    }
}

fun saveCustomCategories(context: Context, categories: List<Pair<String, String>>) {
    val prefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)
    val serialized = categories.joinToString(";") { "${it.first},${it.second}" }
    prefs.edit().putString("custom_categories", serialized).apply()
}

@Composable
fun ZoomableImageDialog(
    uriString: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Close Button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .statusBarsPadding()
                    .padding(16.dp)
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            // Zoomable Image Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                offset += pan
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                val bitmapState = remember(uriString) { mutableStateOf<Bitmap?>(null) }
                val context = LocalContext.current

                LaunchedEffect(uriString) {
                    try {
                        val uri = Uri.parse(uriString)
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, uri)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                        }
                        bitmapState.value = bitmap
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                bitmapState.value?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Zoomable Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            ),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}

@Composable
fun AudioPlayerView(
    audioPath: String,
    accentPrimary: Color,
    borderColor: Color,
    textMuted: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPos by remember { mutableStateOf(0) }
    var duration by remember { mutableStateOf(0) }

    DisposableEffect(audioPath) {
        onDispose {
            mediaPlayer?.apply {
                try {
                    if (isPlaying) {
                        stop()
                    }
                } catch (e: Exception) {
                    // Ignore
                }
                release()
            }
        }
    }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        currentPos = it.currentPosition
                    } else {
                        isPlaying = false
                        currentPos = 0
                    }
                }
                kotlinx.coroutines.delay(250)
            }
        }
    }

    val playPauseAudio = {
        if (mediaPlayer == null) {
            try {
                val mp = MediaPlayer().apply {
                    setDataSource(audioPath)
                    prepare()
                    setOnCompletionListener {
                        isPlaying = false
                        currentPos = 0
                    }
                }
                mediaPlayer = mp
                duration = mp.duration
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, "Cannot play audio file", Toast.LENGTH_SHORT).show()
            }
        }

        mediaPlayer?.let { mp ->
            try {
                if (isPlaying) {
                    mp.pause()
                    isPlaying = false
                } else {
                    mp.start()
                    isPlaying = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val formatTime = { ms: Int ->
        val seconds = (ms / 1000) % 60
        val minutes = (ms / (1000 * 60)) % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(borderColor.copy(alpha = 0.2f))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { playPauseAudio() },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = accentPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Progress bar / Slider
        Slider(
            value = if (duration > 0) currentPos.toFloat() / duration.toFloat() else 0f,
            onValueChange = { percent ->
                mediaPlayer?.let { mp ->
                    val newPos = (percent * duration).toInt()
                    mp.seekTo(newPos)
                    currentPos = newPos
                }
            },
            colors = SliderDefaults.colors(
                thumbColor = accentPrimary,
                activeTrackColor = accentPrimary,
                inactiveTrackColor = borderColor,
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            ),
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${formatTime(currentPos)} / ${formatTime(duration)}",
            fontSize = 11.sp,
            color = textMuted,
            fontWeight = FontWeight.Medium
        )
    }
}

fun getRemainingDurationString(date: String, time: String, lang: String): String {
    if (date.isEmpty() || time.isEmpty()) return ""
    val sdf = SimpleDateFormat("d/M/yyyy HH:mm", Locale.getDefault())
    try {
        val parsedDate = sdf.parse("$date $time")
        if (parsedDate != null) {
            val diff = parsedDate.time - System.currentTimeMillis()
            if (diff <= 0) {
                return AppTranslations.getString("duration_passed", lang)
            }
            val minutes = diff / 60000
            val hours = minutes / 60
            val days = hours / 24

            return when {
                days > 0 -> AppTranslations.getString("duration_days", lang).replace("%d", days.toString())
                hours > 0 -> {
                    val remainingMins = minutes % 60
                    AppTranslations.getString("duration_hours_mins", lang)
                        .replaceFirst("%d", hours.toString())
                        .replaceFirst("%d", remainingMins.toString())
                }
                else -> AppTranslations.getString("duration_mins", lang).replace("%d", minutes.toString())
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun exportChatsToEmail(
    context: Context,
    email: String,
    reminders: List<RemainderEntity>,
    archived: List<RemainderEntity>
) {
    val builder = StringBuilder()
    builder.append("BACK NOTE - CHATS & REMINDERS EXPORT\n")
    builder.append("Generated on: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n")
    builder.append("Recipient Gmail: $email\n")
    builder.append("=========================================\n\n")

    builder.append("── ACTIVE REMINDERS ─────────────────────\n")
    if (reminders.isEmpty()) {
        builder.append("No active reminders.\n")
    } else {
        reminders.forEachIndexed { index, reminder ->
            builder.append("${index + 1}. [${reminder.tag ?: "General"}] ")
            if (reminder.isStarred) builder.append("★ ")
            builder.append("${reminder.date} at ${reminder.time}\n")
            builder.append("   Message: ${reminder.message}\n")
            builder.append("   Contact: ${reminder.phone_number}\n")
            if (reminder.isLocked) {
                builder.append("   (Locked Message)\n")
            }
            builder.append("\n")
        }
    }

    builder.append("── ARCHIVED REMINDERS ───────────────────\n")
    val archivedFiltered = archived.filter { it.isArchived }
    if (archivedFiltered.isEmpty()) {
        builder.append("No archived reminders.\n")
    } else {
        archivedFiltered.forEachIndexed { index, reminder ->
            builder.append("${index + 1}. [${reminder.tag ?: "General"}] ")
            if (reminder.isStarred) builder.append("★ ")
            builder.append("${reminder.date} at ${reminder.time}\n")
            builder.append("   Message: ${reminder.message}\n")
            builder.append("   Contact: ${reminder.phone_number}\n")
            if (reminder.isLocked) {
                builder.append("   (Locked Message)\n")
            }
            builder.append("\n")
        }
    }

    builder.append("=========================================\n")
    builder.append("End of Export.\n")

    // Extract and validate readable image attachments (only for non-locked reminders)
    val imageUris = ArrayList<Uri>()
    val activeImages = reminders.filter { !it.isLocked }.mapNotNull { it.imageUri }.filter { it.isNotEmpty() }
    val archivedImages = archived.filter { !it.isLocked }.mapNotNull { it.imageUri }.filter { it.isNotEmpty() }
    val allImageUris = (activeImages + archivedImages).distinct()

    for (uriStr in allImageUris) {
        try {
            val uri = Uri.parse(uriStr)
            // Attempt to open the stream to verify read permission and existence
            context.contentResolver.openInputStream(uri)?.use {
                imageUris.add(uri)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val intent = if (imageUris.isNotEmpty()) {
        Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "RemindMe Exported Chats")
            putExtra(Intent.EXTRA_TEXT, builder.toString())
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            
            // Set ClipData to explicitly propagate URI permissions to other apps
            val clipData = ClipData.newRawUri("Exported Images", imageUris[0])
            for (i in 1 until imageUris.size) {
                clipData.addItem(ClipData.Item(imageUris[i]))
            }
            this.clipData = clipData
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    } else {
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, "RemindMe Exported Chats")
            putExtra(Intent.EXTRA_TEXT, builder.toString())
        }
    }

    try {
        context.startActivity(Intent.createChooser(intent, "Send Export via Gmail"))
    } catch (e: Exception) {
        Toast.makeText(context, "No email app found to send the export!", Toast.LENGTH_LONG).show()
    }
}

@Composable
fun LinkifiedText(
    text: String,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    color: Color,
    lineHeight: androidx.compose.ui.unit.TextUnit,
    style: TextStyle = TextStyle.Default,
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current
    var layoutResult by remember { mutableStateOf<androidx.compose.ui.text.TextLayoutResult?>(null) }
    
    val annotatedString = remember(text, color) {
        buildAnnotatedString {
            append(text)
            val matcher = Patterns.WEB_URL.matcher(text)
            while (matcher.find()) {
                val start = matcher.start()
                val end = matcher.end()
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFF007AFF),
                        textDecoration = TextDecoration.Underline
                    ),
                    start = start,
                    end = end
                )
                addStringAnnotation(
                    tag = "URL",
                    annotation = text.substring(start, end),
                    start = start,
                    end = end
                )
            }
        }
    }

    Text(
        text = annotatedString,
        fontSize = fontSize,
        fontWeight = fontWeight,
        color = color,
        lineHeight = lineHeight,
        style = style,
        onTextLayout = { layoutResult = it },
        modifier = modifier.pointerInput(annotatedString) {
            detectTapGestures { offset ->
                layoutResult?.let { layout ->
                    val position = layout.getOffsetForPosition(offset)
                    annotatedString.getStringAnnotations(tag = "URL", start = position, end = position)
                        .firstOrNull()?.let { annotation ->
                            var url = annotation.item
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "https://$url"
                            }
                            try {
                                uriHandler.openUri(url)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                }
            }
        }
    )
}

