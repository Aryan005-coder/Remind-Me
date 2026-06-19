package com.example.remindme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.os.PowerManager
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.runtime.CompositionLocalProvider
import com.example.remindme.Room.ReminderRepository
import com.example.remindme.Room.databaseprovider
import com.example.remindme.Room.DeviceProfileEntity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.remindme.navigation.AppNavigation
import com.example.remindme.ui.DashboardViewModel
import com.example.remindme.ui.DashboardViewModelFactory
import com.example.remindme.ui.ProfileViewModel
import com.example.remindme.ui.ProfileViewModelFactory
import com.example.remindme.ui.SettingsViewModel
import com.example.remindme.ui.SettingsViewModelFactory
import com.example.remindme.ui.theme.RemindMeTheme
import com.example.remindme.ui.theme.LocalDarkTheme
import com.example.remindme.ui.theme.LocalFontScale
import com.example.remindme.ui.theme.LocalLanguage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request runtime permissions for SMS and Notifications
        val permissions = mutableListOf(android.Manifest.permission.SEND_SMS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        requestPermissions(permissions.toTypedArray(), 101)

        // Request ignoring battery optimizations to keep alarms alive in background
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!powerManager.isIgnoringBatteryOptimizations(packageName)) {
                try {
                    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        // Initialize database, repository, and ViewModels
        val database = databaseprovider.getDatabase(applicationContext)
        val repository = ReminderRepository(database.reminderDao())

        // Retrieve device ID and store in the database if not present
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
        lifecycleScope.launch {
            try {
                val dao = database.reminderDao()
                val existing = dao.getDeviceProfile(deviceId)
                if (existing == null) {
                    dao.insertDeviceProfile(DeviceProfileEntity(deviceId = deviceId, phoneNumber = ""))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        
        val dashboardViewModel: DashboardViewModel by viewModels {
            DashboardViewModelFactory(repository)
        }
        val profileViewModel: ProfileViewModel by viewModels {
            ProfileViewModelFactory(applicationContext)
        }
        val settingsViewModel: SettingsViewModel by viewModels {
            SettingsViewModelFactory(applicationContext)
        }

        setContent {
            val themeState by settingsViewModel.theme.collectAsState()
            val langState by settingsViewModel.language.collectAsState()
            val fontState by settingsViewModel.fontSize.collectAsState()

            val darkTheme = when (themeState) {
                "light" -> false
                "dark" -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            val fontScaleMultiplier = when (fontState) {
                "small" -> 0.85f
                "large" -> 1.2f
                else -> 1.0f
            }

            RemindMeTheme(darkTheme = darkTheme) {
                val currentDensity = LocalDensity.current
                val customDensity = Density(
                    density = currentDensity.density,
                    fontScale = currentDensity.fontScale * fontScaleMultiplier
                )

                CompositionLocalProvider(
                    LocalDarkTheme provides darkTheme,
                    LocalFontScale provides fontScaleMultiplier,
                    LocalLanguage provides langState,
                    LocalDensity provides customDensity
                ) {
                    AppNavigation(
                        dashboardViewModel = dashboardViewModel,
                        profileViewModel = profileViewModel,
                        settingsViewModel = settingsViewModel,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}