package com.example.remindme.Room

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReminderRepository(private val remainderDao: RemainderDao) {

    suspend fun getAllReminders(): List<RemainderEntity> {
        return remainderDao.getAllRemainders()
    }

    suspend fun getArchivedReminders(): List<RemainderEntity> {
        return remainderDao.getArchivedReminders()
    }

    suspend fun getStarredReminders(): List<RemainderEntity> {
        return remainderDao.getStarredReminders()
    }

    suspend fun insertReminder(reminder: RemainderEntity): Long {
        return remainderDao.insertRemainder(reminder)
    }

    suspend fun deleteReminder(reminder: RemainderEntity) {
        remainderDao.deleteRemainder(reminder)
    }
}
