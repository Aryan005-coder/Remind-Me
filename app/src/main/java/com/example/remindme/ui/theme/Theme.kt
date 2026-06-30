package com.example.remindme.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.staticCompositionLocalOf

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    
)

val LocalDarkTheme = staticCompositionLocalOf { false }
val LocalFontScale = staticCompositionLocalOf { 1.0f }
val LocalLanguage = staticCompositionLocalOf { "en" }

@Composable
fun RemindMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

fun getActiveAccentColor(theme: String, isDark: Boolean): Color {
    return when (theme) {
        "rose" -> if (isDark) Color(0xFFFFB7B2) else Color(0xFFD36F8A)
        "mint" -> if (isDark) Color(0xFFB5EAD7) else Color(0xFF3B8A75)
        "sky" -> if (isDark) Color(0xFFB3E5FC) else Color(0xFF2C6B9E)
        "lavender" -> if (isDark) Color(0xFFE8AEFF) else Color(0xFF8A5CBA)
        "peach" -> if (isDark) Color(0xFFFFDAC1) else Color(0xFFC97A4A)
        "coral" -> if (isDark) Color(0xFFFF8A65) else Color(0xFFE25B45)
        "banana" -> if (isDark) Color(0xFFFFF59D) else Color(0xFFA57D13)
        else -> if (isDark) Color(0xFFF5F5F7) else Color(0xFF09090B)
    }
}

fun getContrastTextColor(theme: String, isDark: Boolean, fallbackTextForDarkBg: Color, fallbackTextForLightBg: Color): Color {
    if (theme == "default") {
        return if (isDark) fallbackTextForDarkBg else fallbackTextForLightBg
    }
    return if (isDark) Color(0xFF0D0D0F) else Color.White
}