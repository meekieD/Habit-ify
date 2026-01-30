package com.dyusov.core.data.repo

import com.dyusov.core.data.utils.toEndOfDayTimestamp
import com.dyusov.core.data.utils.toEntities
import com.dyusov.core.data.utils.toStartOfDayTimestamp
import com.dyusov.core.database.dao.HabitCompletionDao
import com.dyusov.core.database.entity.HabitCompletionDbModel
import com.dyusov.core.model.HabitCompletion
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class HabitCompletionRepositoryImpl @Inject constructor(
    private val habitCompletionDao: HabitCompletionDao
) : HabitCompletionRepository {

    override fun getCompletionsByHabitId(habitId: Long): Flow<List<HabitCompletion>> {
        return habitCompletionDao.getCompletionsByHabitId(habitId).map {
            it.toEntities()
        }
    }

    override fun getCompletionsByDate(date: LocalDate): Flow<List<HabitCompletion>> {
        return habitCompletionDao.getAllCompletionsByDate(
            date.toStartOfDayTimestamp(),
            date.toEndOfDayTimestamp()
        ).map {
            it.toEntities()
        }
    }

    override suspend fun getCompletionsForHabitOnDate(
        habitId: Long,
        date: LocalDate
    ): List<HabitCompletion> {
        return habitCompletionDao.getHabitCompletionsByDate(
            habitId,
            date.toStartOfDayTimestamp(),
            date.toEndOfDayTimestamp()
        ).toEntities()
    }

    override suspend fun addCompletion(habitId: Long, timestamp: Long) {
        return habitCompletionDao.insertCompletion(
            HabitCompletionDbModel(0, habitId, timestamp)
        )
    }

    override suspend fun deleteCompletionById(completionId: Long) {
        habitCompletionDao.deleteCompletionById(completionId)
    }

    override suspend fun deleteCompletionByHabitId(habitId: Long) {
        habitCompletionDao.deleteCompletionsByHabitId(habitId)
    }

    override suspend fun deleteCompletionByDate(
        habitId: Long,
        date: LocalDate
    ) {
        habitCompletionDao.deleteHabitCompletionsByDate(
            habitId,
            date.toStartOfDayTimestamp(),
            date.toEndOfDayTimestamp()
        )
    }

    override suspend fun countCompletionsInPeriod(
        habitId: Long,
        startTimestamp: Long,
        endTimestamp: Long
    ): Int {
        return habitCompletionDao.countHabitCompletionsInPeriod(
            habitId,
            startTimestamp,
            endTimestamp
        )
    }

    override suspend fun isHabitCompletedOnDate(
        habitId: Long,
        date: LocalDate
    ): Boolean {
        return habitCompletionDao.isHabitCompletedOnDate(
            habitId,
            date.toStartOfDayTimestamp(),
            date.toEndOfDayTimestamp()
        )
    }
}