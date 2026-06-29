package com.example.remindme.Room

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.media.RingtoneManager
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.example.remindme.MainActivity

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val reminderId = intent.getIntExtra("id", 0)
        val message = intent.getStringExtra("message") ?: "Time for your reminder!"
        val phoneNumber = intent.getStringExtra("phone_number") ?: ""

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val sharedPrefs = context.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
        val savedUriString = sharedPrefs.getString("app_alarm_ringtone", null)
        val channelId = "reminder_channel_" + (savedUriString?.hashCode() ?: 0)

        val alarmSound = if (savedUriString != null) {
            Uri.parse(savedUriString)
        } else {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        }

        if (action == "com.example.remindme.ACTION_OFF") {
            notificationManager.cancel(reminderId)
            return
        }

        if (action == "com.example.remindme.ACTION_SNOOZE") {
            notificationManager.cancel(reminderId)
            // Reschedule alarm for 5 minutes (300,000 ms) in the future
            val snoozeTimeMillis = System.currentTimeMillis() + 5 * 60 * 1000
            ReminderScheduler.schedule(context, reminderId, phoneNumber, message, snoozeTimeMillis)
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()

            val channel = NotificationChannel(
                channelId,
                "Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notification channel for scheduled reminders"
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
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

        // Action Intents for Snooze and Off
        val offIntent = Intent(context, ReminderReceiver::class.java).apply {
            this.action = "com.example.remindme.ACTION_OFF"
            putExtra("id", reminderId)
        }
        val pendingOff = PendingIntent.getBroadcast(
            context,
            reminderId + 100000,
            offIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(context, ReminderReceiver::class.java).apply {
            this.action = "com.example.remindme.ACTION_SNOOZE"
            putExtra("id", reminderId)
            putExtra("phone_number", phoneNumber)
            putExtra("message", message)
        }
        val pendingSnooze = PendingIntent.getBroadcast(
            context,
            reminderId + 200000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (phoneNumber.isNotEmpty()) "Reminder for $phoneNumber" else "Back Note"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setSound(alarmSound)
            .setOngoing(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_lock_idle_alarm, "Snooze", pendingSnooze)
            .addAction(android.R.drawable.ic_delete, "Off", pendingOff)
            .build()

        notification.flags = notification.flags or android.app.Notification.FLAG_INSISTENT

        notificationManager.notify(reminderId, notification)
    }
}
