package com.dyusov.core.domain.tracking

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.HabitWithCompletions
import jakarta.inject.Inject

class GetHabitWithCompletionsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long): MyResult<HabitWithCompletions, MyError> = habitRepository.getHabitWithCompletions(habitId)
}