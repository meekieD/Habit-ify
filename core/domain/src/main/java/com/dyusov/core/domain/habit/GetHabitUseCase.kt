package com.dyusov.core.domain.habit

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.Habit
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long): MyResult<Habit, MyError> = habitRepository.getHabitById(habitId)
}