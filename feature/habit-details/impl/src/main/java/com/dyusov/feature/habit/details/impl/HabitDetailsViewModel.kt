package com.dyusov.feature.habit.details.impl

import androidx.lifecycle.ViewModel
import com.dyusov.core.domain.tracking.GetHabitWithCompletionsInPeriodUseCase
import com.dyusov.core.domain.tracking.GetHabitWithCompletionsUseCase
import com.dyusov.core.domain.tracking.ToggleHabitCompletionOnDateUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = HabitDetailsViewModel.Factory::class)
class HabitDetailsViewModel @AssistedInject constructor(
    private val getHabitWithCompletionsUseCase: GetHabitWithCompletionsUseCase,
    private val getHabitWithCompletionsInPeriodUseCase: GetHabitWithCompletionsInPeriodUseCase,
    private val toggleHabitCompletionOnDateUseCase: ToggleHabitCompletionOnDateUseCase,
    @Assisted("habitId") private val habitId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("habitId") habitId: Long): HabitDetailsViewModel
    }
}