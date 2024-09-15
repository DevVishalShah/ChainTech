package com.vishal.dev.passwordmanager.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "password_table")
data class Passwords(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val accountType: String,
    val username: String,
    val encryptedPassword: String,
    val createdAt: Long = System.currentTimeMillis()
)
