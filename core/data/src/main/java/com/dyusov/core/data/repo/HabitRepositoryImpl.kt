package com.dyusov.core.data.repo

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.utils.throwCancellationExceptionAndGeneralError
import com.dyusov.core.data.utils.toDbModel
import com.dyusov.core.data.utils.toEntity
import com.dyusov.core.data.utils.toHabitEntities
import com.dyusov.core.data.utils.toHabitWithCompletionsEntities
import com.dyusov.core.database.dao.HabitDao
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao
) : HabitRepository {

    override fun getAllHabits(): Flow<MyResult<List<Habit>, MyError>> {
        return habitDao.getAllHabits().map {
            MyResult.Success(it.toHabitEntities())
        }
    }

    override fun getAllHabitsWithCompletions(): Flow<MyResult<List<HabitWithCompletions>, MyError>> {
        return habitDao.getAllHabitsWithCompletions().map {
            MyResult.Success(it.toHabitWithCompletionsEntities())
        }
    }

    override suspend fun getHabitWithCompletionsInPeriod(
        habitId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): MyResult<HabitWithCompletions, MyError> {
        return try {
            val entity = habitDao.getHabitWithCompletions(habitId).toEntity()
            val filteredEntity = entity.copy(
                completions = entity.completions.filter { completion ->
                    completion.timestamp in startTimestamp..endTimestamp
                }
            )
            MyResult.Success(filteredEntity)
        } catch (_: Exception) {
            throwCancellationExceptionAndGeneralError()
        }
    }

    override fun getAllHabitsWithCompletionsInPeriod(
        startTimestamp: Long,
        endTimestamp: Long
    ): Flow<MyResult<List<HabitWithCompletions>, MyError>> {
        return habitDao.getAllHabitsWithCompletions().map { list ->
            val domainList = list.map { dbModel ->
                val entity = dbModel.toEntity()
                entity.copy(
                    completions = entity.completions.filter { completion ->
                        completion.timestamp in startTimestamp..endTimestamp
                    }
                )
            }
            MyResult.Success(domainList)
        }
    }

    override suspend fun getHabitById(habitId: Long): MyResult<Habit, MyError> {
        return try {
            MyResult.Success(habitDao.getHabit(habitId).toEntity())
        } catch (_: Exception) {
            throwCancellationExceptionAndGeneralError()
        }
    }

    override suspend fun getHabitWithCompletions(habitId: Long): MyResult<HabitWithCompletions, MyError> {
        return try {
            MyResult.Success(habitDao.getHabitWithCompletions(habitId).toEntity())
        } catch (_: Exception) {
            throwCancellationExceptionAndGeneralError()
        }
    }

    override suspend fun upsertHabit(habit: Habit): MyResult<Long, MyError> {
        return try {
            MyResult.Success(habitDao.upsertHabit(habit.toDbModel()))
        } catch (_: Exception) {
            throwCancellationExceptionAndGeneralError()
        }
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
    }
}