package com.dyusov.core.domain.habit

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitFrequency
import jakarta.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        frequency: HabitFrequency,
        color: Int
    ): MyResult<Long, MyError> {
        val timestamp = System.currentTimeMillis()
        val habit = Habit(
            id = 0L,
            name = name,
            isCompletedToday = false,
            description = description,
            frequency = frequency,
            color = color,
            createdAt = timestamp,
            updatedAt = timestamp
        )
        return habitRepository.upsertHabit(habit)
    }
}