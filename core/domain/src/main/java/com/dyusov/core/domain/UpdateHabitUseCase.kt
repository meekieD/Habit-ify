package com.dyusov.core.domain

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.Habit
import javax.inject.Inject

class UpdateHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): MyResult<Long, MyError> {
        return habitRepository.upsertHabit(habit.copy(updatedAt = System.currentTimeMillis()))
    }
}
