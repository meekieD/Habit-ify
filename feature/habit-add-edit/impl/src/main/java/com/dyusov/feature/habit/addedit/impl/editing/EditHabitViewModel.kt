package com.dyusov.feature.habit.addedit.impl.editing

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.utils.onError
import com.dyusov.core.common.utils.onSuccess
import com.dyusov.core.domain.habit.DeleteHabitUseCase
import com.dyusov.core.domain.habit.GetHabitUseCase
import com.dyusov.core.domain.habit.UpdateHabitUseCase
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitFrequency
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

@HiltViewModel(assistedFactory = EditHabitViewModel.Factory::class)
class EditHabitViewModel @AssistedInject constructor(
    private val updateHabitUseCase: UpdateHabitUseCase,
    private val getHabitUseCase: GetHabitUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    @Assisted("habitId") private val habitId: Long
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(@Assisted("habitId") habitId: Long): EditHabitViewModel
    }

    private val _state = MutableStateFlow<EditHabitState>(
        EditHabitState.Initial
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getHabitUseCase(habitId)
                .onSuccess { habit ->
                    Log.d(
                        "EditHabitViewModel",
                        "Got habit with id=$habitId: $habit"
                    )
                    _state.update {
                        EditHabitState.Editing(habit = habit)
                    }
                }
                .onError { error ->
                    Log.d(
                        "EditHabitViewModel",
                        "Got error while getting habit with id=$habitId: $error"
                    )
                    _state.update {
                        EditHabitState.Initial
                    }
                }
        }
    }

    fun processCommand(command: EditHabitCommand) {
        viewModelScope.launch {
            when (command) {
                EditHabitCommand.Back -> {
                    _state.update {
                        EditHabitState.Finished
                    }
                }

                EditHabitCommand.Delete -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            deleteHabitUseCase(currentState.habit.id)
                            EditHabitState.Finished
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.InputDescription -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    description = command.description
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.InputName -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    name = command.name
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }

                EditHabitCommand.Save -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            updateHabitUseCase(habit = currentState.habit)
                            EditHabitState.Finished
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.SelectColor -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    color = command.color
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.SelectFrequencyType -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            val newFrequency = when (command.type) {
                                FrequencyType.DAILY -> HabitFrequency.Daily
                                FrequencyType.WEEKLY -> HabitFrequency.Weekly(
                                    daysOfWeek = currentState.selectedDaysOfTheWeek
                                )

                                FrequencyType.CUSTOM -> HabitFrequency.Custom(
                                    daysOfMonth = currentState.selectedDaysOfMonth
                                )
                            }
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    frequency = newFrequency
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.ToggleDayOfMonth -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            val selectedDays = currentState.selectedDaysOfMonth.let {
                                if (it.contains(command.day)) {
                                    it - command.day
                                } else {
                                    it + command.day
                                }
                            }
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    frequency = HabitFrequency.Custom(daysOfMonth = selectedDays)
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }

                is EditHabitCommand.ToggleDayOfWeek -> {
                    _state.update { currentState ->
                        if (currentState is EditHabitState.Editing) {
                            val selectedDays = currentState.selectedDaysOfTheWeek.let {
                                if (it.contains(command.day)) {
                                    it - command.day
                                } else {
                                    it + command.day
                                }
                            }
                            currentState.copy(
                                habit = currentState.habit.copy(
                                    frequency = HabitFrequency.Weekly(daysOfWeek = selectedDays)
                                )
                            )
                        } else {
                            currentState
                        }
                    }
                }
            }
        }
    }
}

sealed interface EditHabitCommand {

    data class InputName(val name: String) : EditHabitCommand

    data class InputDescription(val description: String) : EditHabitCommand

    data class SelectFrequencyType(val type: FrequencyType) : EditHabitCommand

    data class ToggleDayOfWeek(val day: DayOfWeek) : EditHabitCommand

    data class ToggleDayOfMonth(val day: Int) : EditHabitCommand

    data class SelectColor(val color: Int) : EditHabitCommand

    data object Save : EditHabitCommand

    data object Back : EditHabitCommand

    data object Delete : EditHabitCommand
}

sealed interface EditHabitState {
    data object Initial : EditHabitState

    data class Editing(
        val habit: Habit,
        val error: String? = null
    ) : EditHabitState {

        val frequencyType: FrequencyType
            get() = when (habit.frequency) {
                is HabitFrequency.Daily -> FrequencyType.DAILY
                is HabitFrequency.Weekly -> FrequencyType.WEEKLY
                is HabitFrequency.Custom -> FrequencyType.CUSTOM
            }

        val selectedDaysOfTheWeek: Set<DayOfWeek>
            get() = (habit.frequency as? HabitFrequency.Weekly)?.daysOfWeek
                ?: setOf(LocalDate.nowClock().dayOfWeek)

        val selectedDaysOfMonth: Set<Int>
            get() = (habit.frequency as? HabitFrequency.Custom)?.daysOfMonth
                ?: setOf(LocalDate.nowClock().day)

        val isSaveEnabled: Boolean
            get() = habit.name.isNotBlank() && when (frequencyType) {
                FrequencyType.DAILY -> true
                FrequencyType.WEEKLY -> selectedDaysOfTheWeek.isNotEmpty()
                FrequencyType.CUSTOM -> selectedDaysOfMonth.isNotEmpty()
            }
    }

    data object Finished : EditHabitState
}