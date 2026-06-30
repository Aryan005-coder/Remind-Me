package com.example.remindme.screens

import android.widget.Toast
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.ui.theme.LocalLanguage
import com.example.remindme.ui.theme.AppTranslations
import com.example.remindme.ui.theme.getActiveAccentColor
import com.example.remindme.ui.theme.getContrastTextColor
import kotlinx.coroutines.launch
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType

data class SetLockColors(
    val screenBackground: Color,
    val cardBackground: Color,
    val accentBlack: Color,
    val textPrimary: Color,
    val textMuted: Color,
    val borderColor: Color,
    val dotSelected: Color,
    val dotUnselected: Color,
    val errorColor: Color
)

@Composable
fun rememberSetLockColors(accentColor: String): SetLockColors {
    val dark = LocalDarkTheme.current
    val activeAccent = getActiveAccentColor(accentColor, dark)
    return if (dark) {
        SetLockColors(
            screenBackground = Color(0xFF0D0D0F),
            cardBackground = Color(0xFF1C1C1E),
            accentBlack = activeAccent,
            textPrimary = Color(0xFFF5F5F7),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFF2C2C2E),
            dotSelected = activeAccent,
            dotUnselected = Color(0xFF2C2C2E),
            errorColor = Color(0xFFFF453A)
        )
    } else {
        SetLockColors(
            screenBackground = Color(0xFFF2F2F7),
            cardBackground = Color(0xFFFFFFFF),
            accentBlack = activeAccent,
            textPrimary = Color(0xFF000000),
            textMuted = Color(0xFF8E8E93),
            borderColor = Color(0xFFE5E5EA),
            dotSelected = activeAccent,
            dotUnselected = Color(0xFFE5E5EA),
            errorColor = Color(0xFFFF3B30)
        )
    }
}

enum class PinState {
    VERIFY_CURRENT,
    ENTER_NEW,
    CONFIRM_NEW
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetLockScreen(
    viewModel: SettingsViewModel,
    savedPhone: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accentColorState by viewModel.accentColor.collectAsState()
    val c = rememberSetLockColors(accentColorState)
    val context = LocalContext.current
    val langState by viewModel.language.collectAsState()
    val currentPin by viewModel.lockPin.collectAsState()

    var pinState by remember {
        mutableStateOf(
            if (currentPin.isNullOrEmpty()) PinState.ENTER_NEW else PinState.VERIFY_CURRENT
        )
    }

    var pinInput by remember { mutableStateOf("") }
    var newPinTemp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showForgotDialog by remember { mutableStateOf(false) }
    
    
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    fun runShake() {
        errorMessage = if (pinState == PinState.VERIFY_CURRENT) {
            "Incorrect PIN!"
        } else {
            "PINs do not match!"
        }
        pinInput = ""
        coroutineScope.launch {
            shakeOffset.animateTo(
                targetValue = 20f,
                animationSpec = keyframes {
                    durationMillis = 300
                    0f at 0
                    -20f at 50
                    20f at 100
                    -15f at 150
                    15f at 200
                    -10f at 250
                    0f at 300
                }
            )
            shakeOffset.snapTo(0f)
        }
    }

    val titleText = if (currentPin.isNullOrEmpty()) "Set Lock PIN" else "Change Lock PIN"

    val currentStatusText = when (pinState) {
        PinState.VERIFY_CURRENT -> "Enter Current PIN"
        PinState.ENTER_NEW -> "Enter New 4-Digit PIN"
        PinState.CONFIRM_NEW -> "Confirm New PIN"
    }

    
    fun onDigitClick(digit: String) {
        if (pinInput.length < 4) {
            errorMessage = ""
            pinInput += digit
        }

        if (pinInput.length == 4) {
            when (pinState) {
                PinState.VERIFY_CURRENT -> {
                    if (pinInput == currentPin) {
                        errorMessage = ""
                        pinInput = ""
                        pinState = PinState.ENTER_NEW
                    } else {
                        runShake()
                    }
                }
                PinState.ENTER_NEW -> {
                    newPinTemp = pinInput
                    pinInput = ""
                    pinState = PinState.CONFIRM_NEW
                }
                PinState.CONFIRM_NEW -> {
                    if (pinInput == newPinTemp) {
                        viewModel.setLockPin(pinInput)
                        Toast.makeText(context, "Lock PIN configured successfully!", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    } else {
                        runShake()
                        pinState = PinState.ENTER_NEW
                    }
                }
            }
        }
    }

    
    fun onBackspaceClick() {
        if (pinInput.isNotEmpty()) {
            errorMessage = ""
            pinInput = pinInput.dropLast(1)
        }
    }

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
                            contentDescription = "Back",
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
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = currentStatusText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = c.textPrimary
                )

                
                Row(
                    modifier = Modifier.offset(x = shakeOffset.value.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    repeat(4) { index ->
                        val isFilled = index < pinInput.length
                        val scale by animateFloatAsState(
                            targetValue = if (isFilled) 1.2f else 1.0f,
                            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) c.dotSelected else c.dotUnselected)
                                .border(1.5.dp, c.borderColor, CircleShape)
                        )
                    }
                }

                
                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = c.errorColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }

            
            Column(
                modifier = Modifier.padding(horizontal = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val buttonRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("forgot", "0", "delete")
                )

                buttonRows.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { action ->
                            if (action == "forgot") {
                                if (!currentPin.isNullOrEmpty() && pinState == PinState.VERIFY_CURRENT) {
                                    KeypadButton(
                                        label = action,
                                        colors = c,
                                        onClick = {
                                            showForgotDialog = true
                                        }
                                    )
                                } else {
                                    Spacer(modifier = Modifier.size(72.dp))
                                }
                            } else if (action.isEmpty()) {
                                Spacer(modifier = Modifier.size(72.dp))
                            } else {
                                KeypadButton(
                                    label = action,
                                    colors = c,
                                    onClick = {
                                        if (action == "delete") {
                                            onBackspaceClick()
                                        } else {
                                            onDigitClick(action)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                
                if (!currentPin.isNullOrEmpty() && pinState == PinState.ENTER_NEW) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            viewModel.setLockPin(null)
                            Toast.makeText(context, "Lock PIN disabled successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = c.errorColor)
                    ) {
                        Text(
                            text = "Disable Lock PIN",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showForgotDialog) {
        var phoneInput by remember { mutableStateOf("") }
        var verificationError by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { 
                showForgotDialog = false 
                verificationError = ""
                phoneInput = ""
            },
            title = {
                Text(
                    text = "Verify Phone Number",
                    fontWeight = FontWeight.Bold,
                    color = c.textPrimary,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "To reset your PIN, please enter your registered phone number:",
                        color = c.textMuted,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = {
                            phoneInput = it
                            verificationError = ""
                        },
                        placeholder = { Text("Enter phone number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        isError = verificationError.isNotEmpty(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = c.textPrimary,
                            unfocusedTextColor = c.textPrimary,
                            focusedBorderColor = c.accentBlack,
                            unfocusedBorderColor = c.borderColor,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        )
                    )

                    if (verificationError.isNotEmpty()) {
                        Text(
                            text = verificationError,
                            color = c.errorColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cleanInput = phoneInput.replace("[^0-9]".toRegex(), "")
                        val cleanSaved = savedPhone.replace("[^0-9]".toRegex(), "")

                        if (cleanSaved.isEmpty()) {
                            verificationError = "No registered phone number found. Please set your phone number in your Profile screen first."
                        } else if (cleanInput.isNotEmpty() && (cleanInput == cleanSaved || (cleanInput.length >= 10 && cleanSaved.endsWith(cleanInput)) || (cleanSaved.length >= 10 && cleanInput.endsWith(cleanSaved)))) {
                            Toast.makeText(context, "Phone number verified. Please set a new PIN.", Toast.LENGTH_SHORT).show()
                            showForgotDialog = false
                            pinInput = ""
                            pinState = PinState.ENTER_NEW
                        } else {
                            verificationError = "Incorrect phone number. Please try again."
                        }
                    }
                ) {
                    Text("Verify", color = c.accentBlack, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showForgotDialog = false
                        verificationError = ""
                        phoneInput = ""
                    }
                ) {
                    Text("Cancel", color = c.textMuted)
                }
            },
            containerColor = c.cardBackground
        )
    }
}

@Composable
fun KeypadButton(
    label: String,
    colors: SetLockColors,
    onClick: () -> Unit
) {
    val isDelete = label == "delete"
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(colors.cardBackground)
            .border(1.dp, colors.borderColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isDelete) {
            Text(
                text = "⌫",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
        } else if (label == "forgot") {
            Text(
                text = "Forgot?",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colors.accentBlack
            )
        } else {
            Text(
                text = label,
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = colors.textPrimary
            )
        }
    }
}

@Composable
fun PinVerificationDialog(
    correctPin: String,
    accentColor: String = "default",
    onDismiss: () -> Unit,
    onCorrectPin: () -> Unit
) {
    val c = rememberSetLockColors(accentColor)
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val shakeOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    
    fun onDigitClick(digit: String) {
        if (pinInput.length < 4) {
            errorMessage = ""
            pinInput += digit
        }
        if (pinInput.length == 4) {
            if (pinInput == correctPin) {
                onCorrectPin()
            } else {
                errorMessage = "Incorrect PIN!"
                pinInput = ""
                coroutineScope.launch {
                    shakeOffset.animateTo(
                        targetValue = 15f,
                        animationSpec = keyframes {
                            durationMillis = 300
                            0f at 0
                            -15f at 50
                            15f at 100
                            -10f at 150
                            10f at 200
                            -5f at 250
                            0f at 300
                        }
                    )
                    shakeOffset.snapTo(0f)
                }
            }
        }
    }

    fun onBackspaceClick() {
        if (pinInput.isNotEmpty()) {
            errorMessage = ""
            pinInput = pinInput.dropLast(1)
        }
    }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = c.cardBackground),
            border = BorderStroke(1.dp, c.borderColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Enter PIN to Unlock",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = c.textPrimary
                )

                
                Row(
                    modifier = Modifier.offset(x = shakeOffset.value.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    repeat(4) { index ->
                        val isFilled = index < pinInput.length
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(if (isFilled) c.dotSelected else c.dotUnselected)
                                .border(1.dp, c.borderColor, CircleShape)
                        )
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = c.errorColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Spacer(modifier = Modifier.height(14.dp))
                }

                
                val buttonRows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("", "0", "delete")
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    buttonRows.forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            row.forEach { action ->
                                if (action.isEmpty()) {
                                    Spacer(modifier = Modifier.size(54.dp))
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(c.screenBackground)
                                            .border(1.dp, c.borderColor, CircleShape)
                                            .clickable {
                                                if (action == "delete") {
                                                    onBackspaceClick()
                                                } else {
                                                    onDigitClick(action)
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (action == "delete") {
                                            Text(
                                                text = "⌫",
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = c.textPrimary
                                            )
                                        } else {
                                            Text(
                                                text = action,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = c.textPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(contentColor = c.textMuted)
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

