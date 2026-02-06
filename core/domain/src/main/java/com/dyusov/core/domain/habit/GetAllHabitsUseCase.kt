package com.dyusov.core.domain.habit

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.Habit
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<MyResult<List<Habit>, MyError>> = habitRepository.getAllHabits()
}