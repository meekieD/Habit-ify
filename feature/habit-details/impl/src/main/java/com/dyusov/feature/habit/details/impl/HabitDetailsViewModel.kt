@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dyusov.feature.habit.details.impl

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.toEndOfMonthTimestamp
import com.dyusov.core.common.datetime.toLocalDate
import com.dyusov.core.common.datetime.toStartOfMonthTimestamp
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.streak.CalculateStreakUseCase
import com.dyusov.core.domain.tracking.GetHabitWithCompletionsUseCase
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.daysUntil

@HiltViewModel(assistedFactory = HabitDetailsViewModel.Factory::class)
class HabitDetailsViewModel @AssistedInject constructor(
    private val getHabitWithCompletionsUseCase: GetHabitWithCompletionsUseCase,
    private val toggleHabitCompletionOnDateUseCase: ToggleHabitCompletionOnDateUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val dateTimeProvider: DateTimeProvider,
    @Assisted("habitId") private val habitId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("habitId") habitId: Long): HabitDetailsViewModel
    }

    private val _state = MutableStateFlow<HabitDetailsState>(HabitDetailsState.Initial)
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _streakTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    private val _currentMonth = MutableStateFlow(YearMonth.now())

    init {
        observeStreak()
        loadHabitDetails()
    }

    fun processCommand(command: HabitDetailsCommand) {
        viewModelScope.launch {
            when (command) {
                is HabitDetailsCommand.ToggleHabitCompletionOnDate -> {
                    when (val currentState = _state.value) {
                        is HabitDetailsState.Content -> {
                            toggleHabitCompletionOnDateUseCase(
                                habitId = currentState.habit.id,
                                date = command.selectedDate
                            )
                            _streakTrigger.tryEmit(Unit)
                        }

                        HabitDetailsState.Initial -> {}
                    }
                }

                is HabitDetailsCommand.SetDisplayedMonth -> {
                    _currentMonth.value = command.selectedMonth
                }

                is HabitDetailsCommand.Back -> {
                    _navigationEvent.tryEmit(Unit)
                }
            }
        }
    }

    private fun loadHabitDetails() {
        viewModelScope.launch {
            getHabitWithCompletionsUseCase(habitId)
                .flatMapLatest { result ->
                    combine(
                        flowOf(result),
                        _currentMonth
                    ) { result, month ->
                        result to month
                    }
                }
                .collect { (result, month) ->
                    result.onSuccess { data ->
                        val startTimestamp = month.toStartOfMonthTimestamp()
                        val endTimestamp = month.toEndOfMonthTimestamp()

                        val filteredCompletions = data.completions.filter {
                            it.timestamp in startTimestamp..endTimestamp
                        }

                        _state.update { currentState ->
                            HabitDetailsState.Content(
                                habit = data.habit,
                                monthCompletions = filteredCompletions,
                                currentMonth = month,
                                currentStreak = (currentState as? HabitDetailsState.Content)?.currentStreak
                                    ?: 0,
                                bestStreak = (currentState as? HabitDetailsState.Content)?.bestStreak
                                    ?: 0,
                                totalCompletions = data.completions.size,
                                successRate = calculateSuccessRate(data.habit, data.completions)
                            )
                        }

                        _streakTrigger.emit(Unit)
                    }
                }
        }
    }

    private fun observeStreak() {
        viewModelScope.launch {
            Log.d("HabitDetailsViewModel", "observeStreak started")
            _streakTrigger
                .mapNotNull {
                    (_state.value as? HabitDetailsState.Content)?.habit
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
                                Log.d(
                                    "HabitDetailsViewModel",
                                    "observeStreak: updating streak to $streak"
                                )
                                currentState.copy(
                                    currentStreak = streak.current,
                                    bestStreak = streak.best
                                )
                            } else {
                                currentState
                            }
                        }
                    }
                }
        }
    }

    private fun calculateSuccessRate(habit: Habit, completions: List<HabitCompletion>): Float {
        if (completions.isEmpty()) {
            return 0f
        }

        val daysSinceStart = habit.createdAt.toLocalDate()
            .daysUntil(dateTimeProvider.nowLocalDate()).toLong()
            .coerceAtLeast(1)

        return (completions.size.toFloat() / daysSinceStart).coerceIn(0f, 1f)
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
        val monthCompletions: List<HabitCompletion>,
        val currentMonth: YearMonth,
        val currentStreak: Int = 0,
        val totalCompletions: Int = 0,
        val successRate: Float = 0f,
        val bestStreak: Int = 0
    ) : HabitDetailsState
}
