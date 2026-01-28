package com.dyusov.core.data.repo

import com.dyusov.core.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Repository for managing habit completions.
 */
interface HabitCompletionRepository {

    fun getCompletionsByHabitId(habitId: Long): Flow<List<HabitCompletion>>

    fun getCompletionsByDate(date: LocalDate): Flow<List<HabitCompletion>>

    suspend fun getCompletionsForHabitOnDate(habitId: Long, date: LocalDate): List<HabitCompletion>

    suspend fun addCompletion(habitId: Long, timestamp: Long = System.currentTimeMillis())

    suspend fun deleteCompletionById(completionId: Long)

    suspend fun deleteCompletionByHabitId(habitId: Long)

    suspend fun deleteCompletionByDate(habitId: Long, date: LocalDate)

    suspend fun countCompletionsInPeriod(
        habitId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): Int

    suspend fun isHabitCompletedOnDate(habitId: Long, date: LocalDate): Boolean
}