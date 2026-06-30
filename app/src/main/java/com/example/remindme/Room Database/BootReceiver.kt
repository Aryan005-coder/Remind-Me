package com.example.remindme.Room

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = databaseprovider.getDatabase(context)
                    val reminders = db.reminderDao().getAllRemainders()
                    
                    for (reminder in reminders) {
                        ReminderScheduler.schedule(
                            context = context,
                            id = reminder.id,
                            phoneNumber = reminder.phone_number,
                            message = reminder.message,
                            date = reminder.date,
                            time = reminder.time
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
