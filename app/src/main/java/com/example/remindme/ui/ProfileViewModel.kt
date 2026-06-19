package com.example.remindme.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.provider.Settings
import com.example.remindme.Room.databaseprovider
import com.example.remindme.Room.DeviceProfileEntity

class ProfileViewModel(private val context: Context) : ViewModel() {

    private val sharedPrefs = context.getSharedPreferences("user_profile_prefs", Context.MODE_PRIVATE)

    private val _savedName = MutableStateFlow(sharedPrefs.getString("profile_name", "") ?: "")
    val savedName: StateFlow<String> = _savedName.asStateFlow()

    private val _savedPhone = MutableStateFlow(sharedPrefs.getString("profile_phone", "") ?: "")
    val savedPhone: StateFlow<String> = _savedPhone.asStateFlow()

    fun saveProfile(name: String, phone: String) {
        sharedPrefs.edit().apply {
            putString("profile_name", name)
            putString("profile_phone", phone)
            apply()
        }
        _savedName.value = name
        _savedPhone.value = phone

        // Map phone number to device ID in the database
        val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
        viewModelScope.launch {
            try {
                val database = databaseprovider.getDatabase(context)
                database.reminderDao().insertDeviceProfile(DeviceProfileEntity(deviceId = deviceId, phoneNumber = phone))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ProfileViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(context.applicationContext) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
