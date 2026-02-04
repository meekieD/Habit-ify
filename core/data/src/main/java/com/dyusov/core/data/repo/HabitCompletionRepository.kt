package com.dyusov.core.data.repo

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/**
 * Repository for managing habit completions.
 */
interface HabitCompletionRepository {

    fun getCompletionsByHabitId(habitId: Long): Flow<MyResult<List<HabitCompletion>, MyError>>

    fun getCompletionsByDate(date: LocalDate): Flow<MyResult<List<HabitCompletion>, MyError>>

    fun getCompletionsForHabitOnDate(
        habitId: Long,
        date: LocalDate
    ): Flow<MyResult<List<HabitCompletion>, MyError>>

    suspend fun addCompletion(habitId: Long, timestamp: Long)

    suspend fun deleteCompletionById(completionId: Long)

    suspend fun deleteCompletionByHabitId(habitId: Long)

    suspend fun deleteCompletionByDate(habitId: Long, date: LocalDate)

    suspend fun countCompletionsInPeriod(
        habitId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): MyResult<Int, MyError>

    suspend fun isHabitCompletedOnDate(habitId: Long, date: LocalDate): MyResult<Boolean, MyError>
}