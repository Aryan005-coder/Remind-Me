package com.example.remindme.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.isSystemInDarkTheme

data class PrivacyPolicyColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color
)

@Composable
fun rememberPrivacyPolicyColors(): PrivacyPolicyColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        PrivacyPolicyColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = Color(0xFFF5F5F7),
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E)
        )
    } else {
        PrivacyPolicyColors(
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
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = rememberPrivacyPolicyColors()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = c.textPrimary) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Back",
                            tint = c.accentBlack
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Last Updated: June 2026",
                        fontSize = 12.sp,
                        color = c.textMuted,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(
                        text = "1. Information We Collect",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "RemindMe is an offline-first app. We do not collect, store, or transmit any of your personal details to external cloud systems. The recipient phone number, reminder messages, schedules, and attached image file paths are stored strictly on your local device in a secure SQLite database using Room persistence components.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Core Device Permissions",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "To serve its core function, the app requests the following system privileges:\n" +
                               "• SEND_SMS: Used exclusively to dispatch automated SMS reminder alerts to your designated recipient at the precise scheduled times.\n" +
                               "• SCHEDULE_EXACT_ALARM: Used to schedule Android System Alarms that wake the app to dispatch notifications at your requested time.\n" +
                               "• POST_NOTIFICATIONS (Android 13+): Used to show scheduled notifications on your system dashboard.\n" +
                               "• Read External Storage / Open Document: Used to retrieve and show image attachments inside your reminder composer logs.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "3. Third-Party Data Transmission",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "We do not sell, rent, trade, or share any user details or database records with third-party service providers, advertising networks, or analytics engines. All operations happen client-side.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "4. User Control & Data Deletion",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "You can delete individual reminders at any time from the main timeline, which immediately clears all associated records. Uninstalling the app completely purges the SQLite database database files from your device.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
