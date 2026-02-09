package com.dyusov.core.domain.tracking

import com.dyusov.core.common.utils.MyError
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow
import jakarta.inject.Inject

class GetAllHabitsWithCompletionsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<MyResult<List<HabitWithCompletions>, MyError>> = habitRepository.getAllHabitsWithCompletions()
}