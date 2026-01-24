package com.dyusov.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.dyusov.core.database.dao.HabitCompletionDao
import com.dyusov.core.database.dao.HabitDao
import com.dyusov.core.database.entity.HabitCompletionDbModel
import com.dyusov.core.database.entity.HabitDbModel
import com.dyusov.core.database.utils.DateConverters
import com.dyusov.core.database.utils.HabitConverters

/**
 * Room database for habit tracking data.
 * Contains habits and their completion records.
 */
@Database(
    entities = [
        HabitDbModel::class,
        HabitCompletionDbModel::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(HabitConverters::class, DateConverters::class)
abstract class HabitDatabase : RoomDatabase() {

    abstract fun habitDao(): HabitDao

    abstract fun habitCompletionDao(): HabitCompletionDao
}