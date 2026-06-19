package com.example.remindme.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.remindme.Room.RemainderDao
import com.example.remindme.Room.RemainderEntity
import com.example.remindme.Room.DeviceProfileEntity

@Database(
    entities = [RemainderEntity::class, DeviceProfileEntity::class],
    version = 9,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {

    abstract fun reminderDao(): RemainderDao
}