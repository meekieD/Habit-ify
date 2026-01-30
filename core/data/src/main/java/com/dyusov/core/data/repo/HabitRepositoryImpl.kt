package com.dyusov.core.data.repo

import com.dyusov.core.data.utils.toDbModel
import com.dyusov.core.data.utils.toEntities
import com.dyusov.core.data.utils.toEntity
import com.dyusov.core.database.dao.HabitDao
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getAllHabits(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map {
            it.toEntities()
        }
    }

    override fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>> {
        return habitDao.getAllHabitsWithCompletions().map {
            it.toEntities()
        }
    }

    override suspend fun getHabitById(habitId: Long): Habit {
        return habitDao.getHabit(habitId).toEntity()
    }

    override suspend fun getHabitWithCompletions(habitId: Long): HabitWithCompletions {
        return habitDao.getHabitWithCompletions(habitId).toEntity()
    }

    override suspend fun upsertHabit(habit: Habit): Long {
        return habitDao.upsertHabit(habit.toDbModel())
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
    }
}