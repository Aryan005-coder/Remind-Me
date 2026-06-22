package com.example.remindme.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(context: Context) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)

    private val _theme = MutableStateFlow(sharedPrefs.getString("app_theme", "system") ?: "system")
    val theme: StateFlow<String> = _theme.asStateFlow()

    private val _language = MutableStateFlow(sharedPrefs.getString("app_language", "en") ?: "en")
    val language: StateFlow<String> = _language.asStateFlow()

    private val _fontSize = MutableStateFlow(sharedPrefs.getString("app_font_size", "medium") ?: "medium")
    val fontSize: StateFlow<String> = _fontSize.asStateFlow()

    private val _lockPin = MutableStateFlow(sharedPrefs.getString("app_lock_pin", null))
    val lockPin: StateFlow<String?> = _lockPin.asStateFlow()

    private val _accentColor = MutableStateFlow(sharedPrefs.getString("app_accent_color", "default") ?: "default")
    val accentColor: StateFlow<String> = _accentColor.asStateFlow()

    fun setAccentColor(color: String) {
        sharedPrefs.edit().putString("app_accent_color", color).apply()
        _accentColor.value = color
    }

    private val _exportEmail = MutableStateFlow(sharedPrefs.getString("app_export_email", null))
    val exportEmail: StateFlow<String?> = _exportEmail.asStateFlow()

    fun setTheme(theme: String) {
        sharedPrefs.edit().putString("app_theme", theme).apply()
        _theme.value = theme
    }

    fun setLanguage(language: String) {
        sharedPrefs.edit().putString("app_language", language).apply()
        _language.value = language
    }

    fun setFontSize(fontSize: String) {
        sharedPrefs.edit().putString("app_font_size", fontSize).apply()
        _fontSize.value = fontSize
    }

    fun setLockPin(pin: String?) {
        if (pin == null) {
            sharedPrefs.edit().remove("app_lock_pin").apply()
        } else {
            sharedPrefs.edit().putString("app_lock_pin", pin).apply()
        }
        _lockPin.value = pin
    }

    fun setExportEmail(email: String?) {
        if (email == null) {
            sharedPrefs.edit().remove("app_export_email").apply()
        } else {
            sharedPrefs.edit().putString("app_export_email", email).apply()
        }
        _exportEmail.value = email
    }
}

class SettingsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
