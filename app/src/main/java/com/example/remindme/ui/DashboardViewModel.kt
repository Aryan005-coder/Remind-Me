package com.example.remindme.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.remindme.Room.RemainderEntity
import com.example.remindme.Room.ReminderRepository
import com.example.remindme.Room.ReminderScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _reminders = MutableStateFlow<List<RemainderEntity>>(emptyList())
    val reminders: StateFlow<List<RemainderEntity>> = _reminders.asStateFlow()

    private val _archivedReminders = MutableStateFlow<List<RemainderEntity>>(emptyList())
    val archivedReminders: StateFlow<List<RemainderEntity>> = _archivedReminders.asStateFlow()

    private val _starredReminders = MutableStateFlow<List<RemainderEntity>>(emptyList())
    val starredReminders: StateFlow<List<RemainderEntity>> = _starredReminders.asStateFlow()

    init {
        loadReminders()
        loadStarredReminders()
        loadArchivedReminders()
    }

    fun loadReminders() {
        viewModelScope.launch {
            _reminders.value = repository.getAllReminders()
        }
    }

    fun loadArchivedReminders() {
        viewModelScope.launch {
            _archivedReminders.value = repository.getArchivedReminders()
        }
    }

    fun loadStarredReminders() {
        viewModelScope.launch {
            _starredReminders.value = repository.getStarredReminders()
        }
    }

    fun toggleStar(context: Context, reminder: RemainderEntity) {
        viewModelScope.launch {
            val updated = reminder.copy(isStarred = !reminder.isStarred)
            repository.insertReminder(updated)
            loadReminders()
            loadStarredReminders()
            loadArchivedReminders()
        }
    }

    private fun formatIndianPhoneNumber(number: String): String {
        val cleanNumber = number.replace(Regex("[^0-9+]"), "")
        return when {
            cleanNumber.startsWith("+91") -> cleanNumber
            cleanNumber.startsWith("91") && cleanNumber.length == 12 -> "+$cleanNumber"
            cleanNumber.startsWith("0") && cleanNumber.length == 11 -> "+91${cleanNumber.substring(1)}"
            cleanNumber.length == 10 -> "+91$cleanNumber"
            else -> cleanNumber
        }
    }

    fun addReminder(context: Context, phoneNumber: String, message: String, date: String, time: String, imageUri: String? = null, tag: String? = null, audioPath: String = "") {
        viewModelScope.launch {
            val formattedNumber = formatIndianPhoneNumber(phoneNumber)
            val sdf = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
            val currentFormattedTime = sdf.format(java.util.Date())
            val reminder = RemainderEntity(
                phone_number = formattedNumber,
                message = message,
                date = date,
                time = time,
                audioPath = audioPath,
                imageUri = imageUri,
                tag = tag,
                timeWritten = currentFormattedTime
            )
            val id = repository.insertReminder(reminder)
            ReminderScheduler.schedule(context, id.toInt(), formattedNumber, message, date, time)
            loadReminders()
        }
    }

    fun deleteReminder(context: Context, reminder: RemainderEntity) {
        viewModelScope.launch {
            repository.deleteReminder(reminder)
            ReminderScheduler.cancel(context, reminder.id)
            loadReminders()
            loadStarredReminders()
        }
    }

    fun archiveReminder(context: Context, reminder: RemainderEntity) {
        viewModelScope.launch {
            
            ReminderScheduler.cancel(context, reminder.id)
            repository.insertReminder(reminder.copy(isArchived = true))
            loadReminders()
            loadArchivedReminders()
            loadStarredReminders()
        }
    }

    fun restoreReminder(reminder: RemainderEntity) {
        viewModelScope.launch {
            repository.insertReminder(reminder.copy(isArchived = false))
            loadReminders()
            loadArchivedReminders()
            loadStarredReminders()
        }
    }

    fun updateReminder(context: Context, reminder: RemainderEntity) {
        viewModelScope.launch {
            val formattedNumber = formatIndianPhoneNumber(reminder.phone_number)
            val updatedReminder = reminder.copy(phone_number = formattedNumber)
            
            ReminderScheduler.cancel(context, updatedReminder.id)
            
            repository.insertReminder(updatedReminder)
            
            ReminderScheduler.schedule(
                context = context,
                id = updatedReminder.id,
                phoneNumber = updatedReminder.phone_number,
                message = updatedReminder.message,
                date = updatedReminder.date,
                time = updatedReminder.time
            )
            loadReminders()
            loadStarredReminders()
            loadArchivedReminders()
        }
    }
}

class DashboardViewModelFactory(private val repository: ReminderRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
