package com.example.remindme.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.ui.theme.LocalLanguage
import com.example.remindme.ui.theme.AppTranslations
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle

data class ProfileColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color
)

@Composable
fun rememberProfileColors(): ProfileColors {
    val dark = LocalDarkTheme.current
    return if (dark) {
        ProfileColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = Color(0xFFF5F5F7),
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E)
        )
    } else {
        ProfileColors(
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
fun ProfileScreen(
    savedName: String,
    savedPhone: String,
    onSave: (String, String) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAboutUs: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit,
    onNavigateToTermsConditions: () -> Unit,
    onNavigateToStarred: () -> Unit,
    onNavigateToArchive: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = rememberProfileColors()
    val lang = LocalLanguage.current

    var inputName by remember { mutableStateOf(savedName) }
    var inputPhone by remember { mutableStateOf(savedPhone) }
    val context = LocalContext.current
    val devicePhoneNumber = remember(context) { getDevicePhoneNumber(context) }

    // Sync input state if saved values update
    LaunchedEffect(savedName, savedPhone, devicePhoneNumber) {
        inputName = savedName
        if (savedPhone.isEmpty() && !devicePhoneNumber.isNullOrEmpty()) {
            inputPhone = devicePhoneNumber
        } else {
            inputPhone = savedPhone
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(c.screenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // Title Row: Back chevron | Title | Settings cog
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(44.dp)
                    .background(c.cardBackground, CircleShape)
                    .border(1.dp, c.borderColor, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = "Back",
                    tint = c.accentBlack,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = AppTranslations.getString("profile", lang),
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = c.textPrimary,
                letterSpacing = (-1).sp
            )

            IconButton(onClick = onNavigateToSettings) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = AppTranslations.getString("settings", lang),
                    tint = c.accentBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Text(
            text = AppTranslations.getString("recipient_desc", lang),
            fontSize = 13.sp,
            color = c.textMuted,
            lineHeight = 18.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Name Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = c.cardBackground),
            border = BorderStroke(1.dp, c.borderColor)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = AppTranslations.getString("recipient_name", lang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.accentBlack,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    textStyle = TextStyle(
                        color = c.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(c.accentBlack),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (inputName.isEmpty()) {
                                Text(
                                    text = AppTranslations.getString("name_placeholder", lang),
                                    color = c.textMuted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        // Phone Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = c.cardBackground),
            border = BorderStroke(1.dp, c.borderColor)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = AppTranslations.getString("phone_number", lang),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.accentBlack,
                    letterSpacing = 0.5.sp
                )
                BasicTextField(
                    value = inputPhone,
                    onValueChange = { inputPhone = it },
                    textStyle = TextStyle(
                        color = c.textPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    cursorBrush = SolidColor(c.accentBlack),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp),
                    decorationBox = { innerTextField ->
                        Box(contentAlignment = Alignment.CenterStart) {
                            if (inputPhone.isEmpty()) {
                                Text(
                                    text = AppTranslations.getString("phone_placeholder", lang),
                                    color = c.textMuted,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save Button (Monochrome Premium styling)
        Button(
            onClick = {
                val enteredPhoneClean = inputPhone.trim().replace(Regex("[^0-9]"), "")
                val devicePhoneClean = devicePhoneNumber?.replace(Regex("[^0-9]"), "") ?: ""

                if (inputPhone.isBlank()) {
                    Toast.makeText(context, AppTranslations.getString("phone_empty", lang), Toast.LENGTH_SHORT).show()
                } else if (devicePhoneClean.isNotEmpty() && enteredPhoneClean != devicePhoneClean && !devicePhoneClean.endsWith(enteredPhoneClean) && !enteredPhoneClean.endsWith(devicePhoneClean)) {
                    Toast.makeText(context, "not your phone number", Toast.LENGTH_SHORT).show()
                } else {
                    onSave(inputName.trim(), inputPhone.trim())
                    Toast.makeText(context, AppTranslations.getString("save_success", lang), Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = c.accentBlack,
                contentColor = c.screenBackground
            ),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = AppTranslations.getString("save_settings", lang),
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = c.screenBackground
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Two side-by-side boxes: Starred and Archive
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Starred Card Box
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clickable { onNavigateToStarred() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Starred Messages",
                        tint = Color(0xFFFFD700), // Gold Star
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Starred Messages",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                }
            }

            // Archive Card Box (with box arrow down icon)
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(90.dp)
                    .clickable { onNavigateToArchive() },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Archive,
                        contentDescription = "Archive Messages",
                        tint = c.accentBlack,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Archive Messages",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Monochrome App Logo Footer
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppTranslations.getString("remind", lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = c.textPrimary,
                letterSpacing = (-0.5).sp
            )
            Text(
                text = AppTranslations.getString("me", lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = c.textMuted,
                letterSpacing = (-0.5).sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = AppTranslations.getString("version", lang),
            fontSize = 12.sp,
            color = c.textMuted,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Clickable Links to Info Screens
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppTranslations.getString("about_us", lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = c.accentBlack,
                modifier = Modifier.clickable { onNavigateToAboutUs() }
            )
            Text(
                text = "  •  ",
                fontSize = 11.sp,
                color = c.textMuted
            )
            Text(
                text = AppTranslations.getString("privacy_policy", lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = c.accentBlack,
                modifier = Modifier.clickable { onNavigateToPrivacyPolicy() }
            )
            Text(
                text = "  •  ",
                fontSize = 11.sp,
                color = c.textMuted
            )
            Text(
                text = AppTranslations.getString("terms_conditions", lang),
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = c.accentBlack,
                modifier = Modifier.clickable { onNavigateToTermsConditions() }
            )
        }
    }
}

fun getDevicePhoneNumber(context: android.content.Context): String? {
    try {
        if (androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_NUMBERS) == android.content.pm.PackageManager.PERMISSION_GRANTED ||
            androidx.core.content.ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_PHONE_STATE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val subscriptionManager = context.getSystemService(android.content.Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? android.telephony.SubscriptionManager
                val activeList = subscriptionManager?.activeSubscriptionInfoList
                if (!activeList.isNullOrEmpty()) {
                    for (info in activeList) {
                        val num = info.number
                        if (!num.isNullOrEmpty()) {
                            return num
                        }
                    }
                }
            }
            
            val telephonyManager = context.getSystemService(android.content.Context.TELEPHONY_SERVICE) as? android.telephony.TelephonyManager
            val line1Number = telephonyManager?.line1Number
            if (!line1Number.isNullOrEmpty()) {
                return line1Number
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}
