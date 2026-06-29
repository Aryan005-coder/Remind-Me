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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle

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
                        text = "BackNote is an offline-first app. We do not collect, store, or transmit any of your personal details to external cloud systems. Your reminder messages, schedules, and attached image file paths are stored strictly on your local device in a secure SQLite database using Room persistence components.",
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
                        text = "To provide its core functionality, the application may request access to certain device features and permissions.\n\n" +
                                "• Scheduling and Background Processing: Used to ensure reminders and alerts are delivered at the time selected by the user, even when the application is not actively open.\n\n" +
                                "• Notifications: Used to display reminder alerts and other important application-related information.\n\n" +
                                "• Photos, Media, and Files: Used to allow users to select, attach, view, and manage images associated with reminders.\n\n" +
                                "Information accessed through these permissions is used solely for providing the application's features and is not sold, rented, traded, or used for advertising purposes. Users may revoke permissions at any time through their device settings, though certain features may become unavailable.",
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
                        text = "You can delete individual reminders at any time from the main timeline. Any information associated with a deleted reminder is removed immediately. Uninstalling the application will remove all app-related data stored on your device.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    val uriHandler = LocalUriHandler.current
                    val annotatedText = buildAnnotatedString {
                        append("To know more ")
                        pushStringAnnotation(tag = "URL", annotation = "https://docs.google.com/document/d/1iqzmexIrsGATQtk4nSVWmhj7HRvU-sm_gokV_pYPqaQ/edit")
                        withStyle(style = SpanStyle(color = Color(0xFF007AFF), textDecoration = TextDecoration.Underline, fontWeight = FontWeight.Bold)) {
                            append("click here")
                        }
                        pop()
                    }

                    ClickableText(
                        text = annotatedText,
                        style = TextStyle(
                            color = c.textPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        onClick = { offset ->
                            annotatedText.getStringAnnotations(tag = "URL", start = offset, end = offset)
                                .firstOrNull()?.let { annotation ->
                                    try {
                                        uriHandler.openUri(annotation.item)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                        }
                    )
                }
            }
        }
    }
}
