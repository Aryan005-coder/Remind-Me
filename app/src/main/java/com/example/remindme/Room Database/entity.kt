package com.example.remindme.Room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName  = "remainders")
data class RemainderEntity(
  @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val phone_number: String,
    val message: String,
    val date: String,
    val time: String,
    val audioPath: String = "",
    val imageUri: String? = null,
    val tag: String? = null,
    val timeWritten: String = "",
    val isArchived: Boolean = false,
    val isStarred: Boolean = false,
    val isLocked: Boolean = false
)

@Entity(tableName = "device_profiles")
data class DeviceProfileEntity(
    @PrimaryKey val deviceId: String,
    val phoneNumber: String = ""
)
