package com.dyusov.core.domain.habit

import com.dyusov.core.data.repo.HabitRepository
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long) = habitRepository.deleteHabit(habitId)
}