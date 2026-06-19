package com.example.remindme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.ui.DashboardViewModel
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.ui.theme.LocalLanguage
import com.example.remindme.ui.theme.AppTranslations
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.graphics.SolidColor

val IconExport: ImageVector
    get() = ImageVector.Builder(
        name = "Export",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f
    ).apply {
        path(
            stroke = SolidColor(Color.White),
            strokeLineWidth = 2f,
            strokeLineCap = androidx.compose.ui.graphics.StrokeCap.Round,
            strokeLineJoin = androidx.compose.ui.graphics.StrokeJoin.Round
        ) {
            // U-shape tray
            moveTo(5f, 12f)
            lineTo(5f, 18f)
            curveTo(5f, 19.1f, 5.9f, 20f, 7f, 20f)
            lineTo(17f, 20f)
            curveTo(18.1f, 20f, 19f, 19.1f, 19f, 18f)
            lineTo(19f, 12f)

            // Up arrow shaft
            moveTo(12f, 15f)
            lineTo(12f, 3f)

            // Arrow head
            moveTo(8f, 7f)
            lineTo(12f, 3f)
            lineTo(16f, 7f)
        }
    }.build()

data class SettingsColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color
)

@Composable
fun rememberSettingsColors(): SettingsColors {
    val dark = LocalDarkTheme.current
    return if (dark) {
        SettingsColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = Color(0xFFF5F5F7),
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E)
        )
    } else {
        SettingsColors(
            screenBackground = Color(0xFFF2F2F7),
            cardBackground = Color(0xFFFFFFFF),
            accentBlack = Color(0xFF09090B),
            textPrimary = Color(0xFF000000),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFFE5E5EA)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    dashboardViewModel: DashboardViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToSetLock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val remindersState by dashboardViewModel.reminders.collectAsState()
    val archivedState by dashboardViewModel.archivedReminders.collectAsState()

    val c = rememberSettingsColors()
    val themeState by viewModel.theme.collectAsState()
    val langState by viewModel.language.collectAsState()
    val fontState by viewModel.fontSize.collectAsState()

    val titleText = AppTranslations.getString("settings", langState)
    val backText = AppTranslations.getString("back", langState)
    val themeHeaderText = AppTranslations.getString("theme", langState).uppercase()
    val langHeaderText = AppTranslations.getString("language", langState).uppercase()
    val fontHeaderText = AppTranslations.getString("font_size", langState).uppercase()

    val themeLabels = mapOf(
        "system" to mapOf(
            "en" to "System", "hi" to "सिस्टम", "es" to "Sistema",
            "mr" to "सिस्टम", "bn" to "সিস্টেম", "ta" to "முறைமை", "te" to "సిస్టమ్",
            "kn" to "ಸಿಸ್ಟಮ್", "ml" to "സിസ്റ്റം", "gu" to "સિસ્ટમ", "pa" to "ਸਿਸਟਮ"
        ),
        "light" to mapOf(
            "en" to "Light", "hi" to "लाइट", "es" to "Claro",
            "mr" to "प्रकाशमय", "bn" to "লাইট", "ta" to "ஒளி", "te" to "లైట్",
            "kn" to "ಲೈಟ್", "ml" to "ലൈറ്റ്", "gu" to "લાઇટ", "pa" to "ਲਾਈਟ"
        ),
        "dark" to mapOf(
            "en" to "Dark", "hi" to "डार्क", "es" to "Oscuro",
            "mr" to "गडद", "bn" to "ডার্ক", "ta" to "இருள்", "te" to "డార్క్",
            "kn" to "ಡಾರ್ਕ", "ml" to "ഡാർക്ക്", "gu" to "ડાર્ક", "pa" to "ਡਾਰਕ"
        )
    )

    val fontLabels = mapOf(
        "small" to mapOf(
            "en" to "Small", "hi" to "छोटा", "es" to "Pequeño",
            "mr" to "लहान", "bn" to "ছোট", "ta" to "சிறிய", "te" to "చిన్న",
            "kn" to "ಸಣ್ಣ", "ml" to "ചെറുത്", "gu" to "નાનું", "pa" to "ਛੋਟਾ"
        ),
        "medium" to mapOf(
            "en" to "Medium", "hi" to "मध्यम", "es" to "Medio",
            "mr" to "मध्यम", "bn" to "মাঝারি", "ta" to "நடுத்தரம்", "te" to "మధ్యస్థం",
            "kn" to "ಮಧ್ಯಮ", "ml" to "മധ്യം", "gu" to "મધ્યમ", "pa" to "ਮੱਧਮ"
        ),
        "large" to mapOf(
            "en" to "Large", "hi" to "बड़ा", "es" to "Grande",
            "mr" to "मोठा", "bn" to "বড়", "ta" to "பெரிய", "te" to "పెద్ద",
            "kn" to "ದೊಡ್ಡದು", "ml" to "വലുത്", "gu" to "મોટું", "pa" to "ਵੱਡਾ"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = titleText, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = c.textPrimary, modifier = Modifier.padding(start = 12.dp)) },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(44.dp)
                            .background(c.cardBackground, CircleShape)
                            .border(1.dp, c.borderColor, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = backText,
                            tint = c.accentBlack,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = c.screenBackground)
            )
        },
        containerColor = c.screenBackground,
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Theme selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = themeHeaderText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf("system", "light", "dark")
                        options.forEach { value ->
                            val label = themeLabels[value]?.get(langState) ?: themeLabels[value]?.get("en") ?: value
                            val isSelected = themeState == value
                            val bg = if (isSelected) c.accentBlack else c.screenBackground
                            val text = if (isSelected) c.screenBackground else c.textMuted
                            val border = if (isSelected) c.accentBlack else c.borderColor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setTheme(value) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = text
                                )
                            }
                        }
                    }
                }
            }

            // Language selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = langHeaderText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    val options = listOf(
                        "en" to "English",
                        "hi" to "Hindi",
                        "es" to "Spanish",
                        "mr" to "Marathi",
                        "bn" to "Bengali",
                        "ta" to "Tamil",
                        "te" to "Telugu",
                        "kn" to "Kannada",
                        "ml" to "Malayalam",
                        "gu" to "Gujarati",
                        "pa" to "Punjabi"
                    )
                    val chunkedOptions = options.chunked(3)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        chunkedOptions.forEach { rowOptions ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowOptions.forEach { (value, label) ->
                                    val isSelected = langState == value
                                    val bg = if (isSelected) c.accentBlack else c.screenBackground
                                    val text = if (isSelected) c.screenBackground else c.textMuted
                                    val border = if (isSelected) c.accentBlack else c.borderColor
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(bg)
                                            .border(1.dp, border, RoundedCornerShape(10.dp))
                                            .clickable { viewModel.setLanguage(value) }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = label,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = text
                                        )
                                    }
                                }
                                if (rowOptions.size < 3) {
                                    repeat(3 - rowOptions.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Font Size selection Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = fontHeaderText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf("small", "medium", "large")
                        options.forEach { value ->
                            val label = fontLabels[value]?.get(langState) ?: fontLabels[value]?.get("en") ?: value
                            val isSelected = fontState == value
                            val bg = if (isSelected) c.accentBlack else c.screenBackground
                            val text = if (isSelected) c.screenBackground else c.textMuted
                            val border = if (isSelected) c.accentBlack else c.borderColor
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(bg)
                                    .border(1.dp, border, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setFontSize(value) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = text
                                )
                            }
                        }
                    }
                }
            }

            // Security card
            val isPinSet by viewModel.lockPin.collectAsState()
            val pinStatus = if (!isPinSet.isNullOrEmpty()) "Configured" else "Not Configured"
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToSetLock() }
                        .padding(16.dp)
                ) {
                    Text(
                        text = "SECURITY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Lock Screen PIN",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = c.textPrimary
                        )
                        Text(
                            text = pinStatus,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isPinSet.isNullOrEmpty()) c.accentBlack else c.textMuted
                        )
                    }
                }
            }

            // Export Email selection Card
            val exportEmail by viewModel.exportEmail.collectAsState()
            val emailStatus = if (!exportEmail.isNullOrEmpty()) "Configured" else "Not Configured"
            var showEmailDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPORT CHATS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = c.textMuted,
                            letterSpacing = 0.5.sp
                        )
                        IconButton(
                            onClick = {
                                if (exportEmail.isNullOrBlank()) {
                                    Toast.makeText(context, "Please set an export Gmail first!", Toast.LENGTH_LONG).show()
                                } else {
                                    exportChatsToEmail(
                                        context = context,
                                        email = exportEmail!!,
                                        reminders = remindersState,
                                        archived = archivedState
                                    )
                                }
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = IconExport,
                                contentDescription = "Export Chats",
                                tint = c.accentBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showEmailDialog = true },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Recipient Gmail",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = c.textPrimary
                            )
                            if (!exportEmail.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = exportEmail!!,
                                    fontSize = 13.sp,
                                    color = c.textMuted
                                )
                            }
                        }
                        Text(
                            text = emailStatus,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!exportEmail.isNullOrEmpty()) c.accentBlack else c.textMuted
                        )
                    }
                }
            }

            if (showEmailDialog) {
                var emailInput by remember { mutableStateOf(exportEmail ?: "") }
                var emailError by remember { mutableStateOf("") }
                AlertDialog(
                    onDismissRequest = { showEmailDialog = false },
                    title = { Text("Configure Export Gmail", fontWeight = FontWeight.Bold, color = c.textPrimary) },
                    containerColor = c.cardBackground,
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Enter your Gmail address where your exported reminders will be sent:", color = c.textMuted, fontSize = 14.sp)
                            if (!exportEmail.isNullOrEmpty()) {
                                Text("Currently configured: ${exportEmail!!}", color = c.accentBlack, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                            OutlinedTextField(
                                value = emailInput,
                                onValueChange = {
                                    emailInput = it
                                    emailError = ""
                                },
                                placeholder = { Text("example@gmail.com") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                isError = emailError.isNotEmpty(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = c.textPrimary,
                                    unfocusedTextColor = c.textPrimary,
                                    focusedBorderColor = c.accentBlack,
                                    unfocusedBorderColor = c.borderColor,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent
                                )
                            )
                            if (emailError.isNotEmpty()) {
                                Text(emailError, color = Color.Red, fontSize = 12.sp)
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                val trimmed = emailInput.trim()
                                if (trimmed.isEmpty()) {
                                    viewModel.setExportEmail(null)
                                    showEmailDialog = false
                                } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmed).matches()) {
                                    emailError = "Invalid email address format!"
                                } else if (!trimmed.endsWith("@gmail.com", ignoreCase = true)) {
                                    emailError = "Please enter a valid Gmail address (@gmail.com)!"
                                } else {
                                    viewModel.setExportEmail(trimmed)
                                    showEmailDialog = false
                                }
                            }
                        ) {
                            Text("Save", color = c.accentBlack, fontWeight = FontWeight.Bold)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEmailDialog = false }) {
                            Text("Cancel", color = c.textMuted)
                        }
                    }
                )
            }
        }
    }
}
