package com.dyusov.core.data.repo

import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

/**
 * Repository for managing habits.
 */
interface HabitRepository {

    fun getAllHabits(): Flow<List<Habit>>

    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>>

    suspend fun getHabitById(habitId: Long): Habit

    suspend fun getHabitWithCompletions(habitId: Long): HabitWithCompletions

    suspend fun upsertHabit(habit: Habit): Long

    suspend fun deleteHabit(habitId: Long)
}