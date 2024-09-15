package com.vishal.dev.passwordmanager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vishal.dev.passwordmanager.model.Passwords

@Database(entities = [Passwords::class], version = 1, exportSchema = false)
abstract class PasswordDatabase: RoomDatabase() {

    abstract fun passDao(): PassDao
}