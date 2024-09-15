package com.vishal.dev.passwordmanager.di

import android.content.Context
import androidx.room.Room
import com.vishal.dev.passwordmanager.db.PassDao
import com.vishal.dev.passwordmanager.db.PasswordDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providePasswordDatabase(
        @ApplicationContext context: Context
    ): PasswordDatabase {
        return Room.databaseBuilder(
            context.applicationContext, PasswordDatabase::class.java, "password_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providePasswordDao(passwordDatabase: PasswordDatabase): PassDao {
        return passwordDatabase.passDao()
    }
}
