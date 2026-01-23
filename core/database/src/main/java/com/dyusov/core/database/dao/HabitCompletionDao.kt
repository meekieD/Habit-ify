package com.dyusov.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.dyusov.core.database.entity.HabitCompletionDbModel
import kotlinx.coroutines.flow.Flow

/**
 * DAO for interacting with habit completion entity.
 */
@Dao
interface HabitCompletionDao {

    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY timestamp DESC")
    fun getCompletionsByHabitId(habitId: Long): Flow<List<HabitCompletionDbModel>>

    @Insert
    suspend fun insertCompletion(habitCompletionDbModel: HabitCompletionDbModel)

    @Query("DELETE FROM habit_completions WHERE id = :id")
    suspend fun deleteCompletionById(id: Long)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsByHabitId(habitId: Long)

    @Query("""
        SELECT * FROM habit_completions 
        WHERE timestamp >= :startOfDay 
        AND timestamp < :endOfDay 
        ORDER BY timestamp DESC
    """)
    fun getAllCompletionsByDate(startOfDay: Long, endOfDay: Long): Flow<List<HabitCompletionDbModel>>

    @Query("""
        SELECT * FROM habit_completions 
        WHERE habitId = :habitId 
        AND timestamp >= :startOfDay AND timestamp < :endOfDay
    """)
    suspend fun getHabitCompletionsByDate(
        habitId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): List<HabitCompletionDbModel>

    @Query("""
        DELETE FROM habit_completions 
        WHERE habitId = :habitId 
        AND timestamp >= :startOfDay 
        AND timestamp <= :endOfDay
    """)
    suspend fun deleteHabitCompletionsByDate(habitId: Long, startOfDay: Long, endOfDay: Long)

    @Query("""
        SELECT COUNT(*) FROM habit_completions 
        WHERE habitId = :habitId 
        AND timestamp >= :startTimestamp AND timestamp < :endTimestamp
    """)
    suspend fun countHabitCompletionsInPeriod(
        habitId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): Int

    @Query("""
        SELECT EXISTS(
            SELECT 1 FROM habit_completions 
            WHERE habitId = :habitId 
            AND timestamp >= :startOfDay AND timestamp < :endOfDay
        )
    """)
    suspend fun isHabitCompletedOnDate(
        habitId: Long,
        startOfDay: Long,
        endOfDay: Long
    ): Boolean
}