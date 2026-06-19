package com.example.remindme.Room

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsManager
import androidx.core.app.NotificationCompat
import com.example.remindme.MainActivity
import android.provider.Settings
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.Dispatchers

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val message = intent.getStringExtra("message") ?: "Time for your reminder!"
        val phoneNumber = intent.getStringExtra("phone_number") ?: ""
        val reminderId = intent.getIntExtra("id", 0)

        // Send SMS if phoneNumber is present and matches the registered phone number of this device ID
        if (phoneNumber.isNotEmpty()) {
            var isAllowed = false
            try {
                val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown_device"
                val database = databaseprovider.getDatabase(context)
                runBlocking(Dispatchers.IO) {
                    val profile = database.reminderDao().getDeviceProfile(deviceId)
                    if (profile != null && profile.phoneNumber.isNotEmpty()) {
                        val cleanPhone = phoneNumber.replace(Regex("[^0-9]"), "")
                        val cleanProfilePhone = profile.phoneNumber.replace(Regex("[^0-9]"), "")
                        if (cleanPhone.isNotEmpty() && cleanProfilePhone.isNotEmpty() &&
                            (cleanPhone == cleanProfilePhone || cleanPhone.endsWith(cleanProfilePhone) || cleanProfilePhone.endsWith(cleanPhone))) {
                            isAllowed = true
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (isAllowed) {
                try {
                    val smsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        context.getSystemService(SmsManager::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        SmsManager.getDefault()
                    }
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for scheduled reminders"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (phoneNumber.isNotEmpty()) "Reminder for $phoneNumber" else "RemindMe"
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(reminderId, notification)
    }
}
