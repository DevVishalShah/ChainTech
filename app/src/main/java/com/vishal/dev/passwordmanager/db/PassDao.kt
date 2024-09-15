package com.vishal.dev.passwordmanager.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vishal.dev.passwordmanager.model.Passwords
import kotlinx.coroutines.flow.Flow

@Dao
interface PassDao {
    @Query("SELECT * FROM password_table")
    fun getAllPasswords(): Flow<List<Passwords>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPassword(passwords: Passwords)

    @Delete
    fun deletePassword(passwords: Passwords)

}