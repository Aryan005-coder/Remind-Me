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

data class TermsConditionsColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color
)

@Composable
fun rememberTermsConditionsColors(): TermsConditionsColors {
    val dark = isSystemInDarkTheme()
    return if (dark) {
        TermsConditionsColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = Color(0xFFF5F5F7),
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E)
        )
    } else {
        TermsConditionsColors(
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
fun TermsConditionsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val c = rememberTermsConditionsColors()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Terms & Conditions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = c.textPrimary) },
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
                        text = "1. Terms of Service",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "By installing and using the BackNote app, you agree to these terms and conditions. The application is designed to schedule local reminders and trigger alarm notifications directly on your device.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "2. Local Reminders and Alarm Processing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "BackNote works entirely offline. Reminders are scheduled locally on your device using Android's AlarmManager features. The application does not send SMS messages or use mobile data to process reminders, ensuring zero carrier charges or messaging costs.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "3. Disclaimer of Warranty",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "The application is provided on an 'as is' and 'as available' basis without warranties of any kind. While the scheduling system leverages native Android AlarmManager features, reminder delivery can be affected by device power-saving states, battery optimization features, carrier network availability, or software modifications.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "4. Limitation of Liability",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = c.textPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "In no event shall the developers of BackNote be liable for any direct, indirect, incidental, or consequential damages resulting from missed notifications, transmission failures, or SMS delivery issues.",
                        fontSize = 13.sp,
                        color = c.textMuted,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}
