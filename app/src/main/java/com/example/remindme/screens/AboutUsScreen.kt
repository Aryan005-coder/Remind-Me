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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import androidx.compose.foundation.isSystemInDarkTheme

data class AboutUsColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color
)

@Composable
fun rememberAboutUsColors(): AboutUsColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        AboutUsColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = Color(0xFFF5F5F7),
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E)
        )
    } else {
        AboutUsColors(
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
fun AboutUsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = rememberAboutUsColors()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About Us", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = c.textPrimary) },
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Remind",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = c.textPrimary,
                            letterSpacing = (-1).sp
                        )
                        Text(
                            text = "Me",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Black,
                            color = c.textMuted,
                            letterSpacing = (-1).sp
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Version 1.0.0",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.accentBlack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "RemindMe is a sleek, offline-first personal reminder assistant designed for productivity and utility. By leveraging precise native scheduling triggers and automated messaging configurations, the app ensures that your custom tasks and notification milestones are met directly from your device.",
                        fontSize = 14.sp,
                        color = c.textPrimary,
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Key Design Tenets",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Minimalist & High Contrast: Clean black-and-white visual typography hierarchy matching iOS aesthetic rules.\n" +
                               "• Absolute Privacy: No third-party servers, databases, or analytics engines. All reminder schedules, images, and recipient details remain securely in the local SQLite engine (Room).\n" +
                               "• Swift Utility: Fluid swipe-to-edit layouts and snappy dismiss animations built for instant configurations.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 19.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = c.cardBackground),
                border = BorderStroke(1.dp, c.borderColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Development & Mentorship",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )

                    Text(
                        text = "This application was developed by Aryan Singh under the guidance and of White Hawk.",
                        fontSize = 14.sp,
                        color = c.textMuted,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "The primary purpose of this application is to provide a useful and accessible service to its users. This project is a non-profit initiative and has been created solely for educational, developmental, and public-benefit purposes. No financial gain, commercial profit, or monetary benefit is intended or sought through the development or distribution of this application.",
                        fontSize = 14.sp,
                        color = c.textMuted,
                        lineHeight = 20.sp
                    )

                    Text(
                        text = "All efforts have been made to ensure the application is reliable and user-friendly. Feedback and suggestions for improvement are always welcome and will help in enhancing the overall user experience.",
                        fontSize = 14.sp,
                        color = c.textMuted,
                        lineHeight = 20.sp
                    )

                    HorizontalDivider(color = c.borderColor, thickness = 1.dp)

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Developed by:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = c.textMuted
                            )
                            Text(
                                text = "Aryan Singh",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.textPrimary
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Guidance and Mentorship:",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = c.textMuted
                            )
                            Text(
                                text = "White Hawk",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = c.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
