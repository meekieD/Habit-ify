package com.dyusov.core.data.repo

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing habits.
 */
interface HabitRepository {

    fun getAllHabits(): Flow<MyResult<List<Habit>, MyError>>

    fun getAllHabitsWithCompletions(): Flow<MyResult<List<HabitWithCompletions>, MyError>>

    suspend fun getHabitById(habitId: Long): MyResult<Habit, MyError>

    suspend fun getHabitWithCompletions(habitId: Long): MyResult<HabitWithCompletions, MyError>

    suspend fun upsertHabit(habit: Habit): MyResult<Long, MyError>

    suspend fun deleteHabit(habitId: Long)
}