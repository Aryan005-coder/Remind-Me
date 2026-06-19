package com.example.remindme.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.OnConflictStrategy

@Dao
interface RemainderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRemainder(remainder: RemainderEntity): Long

    @Delete
    suspend fun deleteRemainder(remainder: RemainderEntity)

    @Query("SELECT * FROM remainders WHERE isArchived = 0 ORDER BY id ASC")
    suspend fun getAllRemainders(): List<RemainderEntity>

    @Query("SELECT * FROM remainders WHERE isArchived = 1 ORDER BY id DESC")
    suspend fun getArchivedReminders(): List<RemainderEntity>

    @Query("SELECT * FROM remainders WHERE isStarred = 1 AND isArchived = 0 ORDER BY id DESC")
    suspend fun getStarredReminders(): List<RemainderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeviceProfile(profile: DeviceProfileEntity)

    @Query("SELECT * FROM device_profiles WHERE deviceId = :deviceId LIMIT 1")
    suspend fun getDeviceProfile(deviceId: String): DeviceProfileEntity?
}
