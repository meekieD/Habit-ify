@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dyusov.feature.habit.details.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.toEndOfMonthTimestamp
import com.dyusov.core.common.datetime.toStartOfMonthTimestamp
import com.dyusov.core.common.utils.onError
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.streak.CalculateStreakUseCase
import com.dyusov.core.domain.tracking.GetHabitWithCompletionsInPeriodUseCase
import com.dyusov.core.domain.tracking.ToggleHabitCompletionOnDateUseCase
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitCompletion
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth

@HiltViewModel(assistedFactory = HabitDetailsViewModel.Factory::class)
class HabitDetailsViewModel @AssistedInject constructor(
    private val getHabitWithCompletionsInPeriodUseCase: GetHabitWithCompletionsInPeriodUseCase,
    private val toggleHabitCompletionOnDateUseCase: ToggleHabitCompletionOnDateUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    @Assisted("habitId") private val habitId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("habitId") habitId: Long): HabitDetailsViewModel
    }

    private val _state = MutableStateFlow<HabitDetailsState>(
        HabitDetailsState.Initial
    )

    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    init {
        Log.d("HabitDetailsViewModel", "init called with habitId=$habitId")
        loadHabitDetails(YearMonth.now())
        observeStreak()
    }

    fun processCommand(command: HabitDetailsCommand) {
        Log.d("HabitDetailsViewModel", "processCommand: $command, current state: ${_state.value}")
        viewModelScope.launch {
            when (command) {
                is HabitDetailsCommand.ToggleHabitCompletionOnDate -> {
                    toggleHabitCompletionOnDateUseCase(
                        habitId = habitId,
                        date = command.selectedDate
                    )
                    loadHabitDetails(
                        month = (_state.value as? HabitDetailsState.Content)?.currentMonth
                            ?: YearMonth.now()
                    )
                }

                is HabitDetailsCommand.SetDisplayedMonth -> {
                    loadHabitDetails(command.selectedMonth)
                }

                is HabitDetailsCommand.Back -> {
                    _navigationEvent.tryEmit(Unit)
                }
            }
        }
    }

    private fun loadHabitDetails(month: YearMonth) {
        Log.d("HabitDetailsViewModel", "loadHabitDetails called for month=$month, habitId=$habitId")
        viewModelScope.launch {
            getHabitWithCompletionsInPeriodUseCase(
                habitId = habitId,
                startTimestamp = month.toStartOfMonthTimestamp(),
                endTimestamp = month.toEndOfMonthTimestamp()
            ).onSuccess { habitWithCompletions ->
                Log.d(
                    "HabitDetailsViewModel",
                    "Got habit details SUCCESS: habitId=${habitWithCompletions.habit.id}, completionsCount=${habitWithCompletions.completions.size}"
                )
                _state.update {
                    HabitDetailsState.Content(
                        habit = habitWithCompletions.habit,
                        completions = habitWithCompletions.completions,
                        currentMonth = month
                    )
                }
                Log.d("HabitDetailsViewModel", "State updated to Content")
            }.onError { error ->
                Log.e(
                    "HabitDetailsViewModel",
                    "Got ERROR while getting habit details with id=$habitId: $error"
                )
                _state.update {
                    HabitDetailsState.Initial
                }
            }
        }
    }

    private fun observeStreak() {
        viewModelScope.launch {
            Log.d("HabitDetailsViewModel", "observeStreak started")
            _state
                .mapNotNull {
                    (it as? HabitDetailsState.Content)?.habit
                }
                .flatMapLatest { habit ->
                    Log.d("HabitDetailsViewModel", "observeStreak: got habit=${habit.id}")
                    calculateStreakUseCase(habit)
                }
                .collect { result ->
                    Log.d("HabitDetailsViewModel", "observeStreak: result=$result")
                    result.onSuccess { streak ->
                        _state.update { currentState ->
                            if (currentState is HabitDetailsState.Content) {
                                Log.d("HabitDetailsViewModel", "observeStreak: updating streak to $streak")
                                currentState.copy(currentStreak = streak)
                            } else {
                                currentState
                            }
                        }
                    }
                }
        }
    }
}

sealed interface HabitDetailsCommand {
    data class ToggleHabitCompletionOnDate(val selectedDate: LocalDate) : HabitDetailsCommand
    data class SetDisplayedMonth(val selectedMonth: YearMonth) : HabitDetailsCommand
    data object Back : HabitDetailsCommand
}

sealed interface HabitDetailsState {
    data object Initial : HabitDetailsState

    data class Content(
        val habit: Habit,
        val completions: List<HabitCompletion>,
        val currentMonth: YearMonth,
        val currentStreak: Int = 0
    ) : HabitDetailsState
}
