package com.dyusov.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.dyusov.core.database.entity.HabitDbModel
import com.dyusov.core.database.entity.HabitWithCompletionsDbModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO for interacting with habit entity.
 */
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitDbModel>>

    @Upsert
    suspend fun upsertHabit(habit: HabitDbModel): Long

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long)

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabit(habitId: Long): HabitDbModel

    /**
     * Get habits with completions info
     */

    @Transaction
    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitWithCompletions(habitId: Long): HabitWithCompletionsDbModel

    @Transaction
    @Query("SELECT * FROM habits")
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletionsDbModel>>
}