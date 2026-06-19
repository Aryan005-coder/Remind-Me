package com.example.remindme.Room

import android.content.Context
import androidx.room.Room
import com.example.remindme.room.ReminderDatabase

object databaseprovider {

    private var INSTANCE: ReminderDatabase? = null

    fun getDatabase(context: Context): ReminderDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                ReminderDatabase::class.java,
                "reminder_database"
            )
                .fallbackToDestructiveMigration()
                .build()

            INSTANCE = instance
            instance
        }
    }
}