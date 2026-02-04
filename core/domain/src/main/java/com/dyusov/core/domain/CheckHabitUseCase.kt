package com.dyusov.core.domain

import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.data.repo.HabitCompletionRepository
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class CheckHabitUseCase @Inject constructor(
    private val habitCompletionRepository: HabitCompletionRepository
) {
    suspend operator fun invoke(habitId: Long, date: LocalDate) {
        habitCompletionRepository.isHabitCompletedOnDate(
            habitId = habitId,
            date = date
        ).onSuccess { isCompleted ->
            if (!isCompleted) {
                habitCompletionRepository.addCompletion(
                    habitId = habitId,
                    timestamp = System.currentTimeMillis()
                )
            }
        }
    }
}