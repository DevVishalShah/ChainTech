package com.vishal.dev.passwordmanager.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishal.dev.passwordmanager.db.PassDao
import com.vishal.dev.passwordmanager.model.Passwords
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PasswordViewModel @Inject constructor(
    private val passDao: PassDao
) : ViewModel() {

    // Expose a StateFlow of the list of passwords
    private val _passwords = MutableStateFlow<List<Passwords>>(emptyList())
    val passwords: StateFlow<List<Passwords>> = _passwords.asStateFlow()

    init {
        // Collect from DAO to keep the StateFlow updated
        viewModelScope.launch(Dispatchers.IO) {
            passDao.getAllPasswords().collect { passwordsList ->
                _passwords.value = passwordsList
            }
        }
    }

    fun addPassword(accountType: String, username: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
//                val encryptedPassword = PasswordEncryption.encryptPassword(password)
                val passwordEntity = Passwords(
                    accountType = accountType, username = username, encryptedPassword = password
                )
                passDao.insertPassword(passwordEntity)

                // Log success message
                Log.d("PasswordViewModel", "Password inserted successfully")
            } catch (e: Exception) {
                // Log error message
                Log.e("PasswordViewModel", "Error inserting password: ${e.localizedMessage}")
            }
        }
    }

    fun deletePass(passwords: Passwords) {
        viewModelScope.launch(Dispatchers.IO) {
            passDao.deletePassword(passwords)
        }
    }

    // Dummy methods for database operations
    private suspend fun fetchPasswordsFromDatabase(): List<Passwords> {
        // Implement your database fetching logic here
        return emptyList()
    }

    private suspend fun insertPasswordToDatabase(password: Passwords) {
        // Implement your database insertion logic here
    }

    private suspend fun deletePasswordFromDatabase(password: Passwords) {
        // Implement your database deletion logic here
    }
}