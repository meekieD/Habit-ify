package com.dyusov.core.database.di

import android.content.Context
import com.dyusov.core.database.HabitDatabase
import com.dyusov.core.database.dao.HabitCompletionDao
import com.dyusov.core.database.dao.HabitDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DatabaseModule {

    companion object {
        @Provides
        @Singleton
        fun provideDatabase(
            @ApplicationContext context: Context
        ): HabitDatabase {
            return HabitDatabase.getInstance(context)
        }

        @Singleton
        @Provides
        fun provideHabitDao(
            database: HabitDatabase
        ): HabitDao {
            return database.habitDao()
        }

        @Singleton
        @Provides
        fun provideHabitCompletionDao(
            database: HabitDatabase
        ): HabitCompletionDao {
            return database.habitCompletionDao()
        }
    }
}