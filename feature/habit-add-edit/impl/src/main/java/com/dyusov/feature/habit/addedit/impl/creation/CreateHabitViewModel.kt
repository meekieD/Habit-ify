package com.dyusov.feature.habit.addedit.impl.creation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.domain.habit.CreateHabitUseCase
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.model.HabitFrequency
import com.dyusov.core.ui.habit.HabitCardDefaults
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import javax.inject.Inject


@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {
    private val _state = MutableStateFlow<CreateHabitState>(
        CreateHabitState.Creation()
    )

    val state = _state.asStateFlow()

    fun processCommand(command: CreateHabitCommand) {
        viewModelScope.launch {
            when (command) {
                is CreateHabitCommand.InputName -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            currentState.copy(name = command.name)
                        } else {
                            currentState
                        }
                    }
                }

                is CreateHabitCommand.InputDescription -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            currentState.copy(description = command.description)
                        } else {
                            currentState
                        }
                    }
                }

                is CreateHabitCommand.SelectFrequencyType -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            currentState.copy(frequencyType = command.type)
                        } else {
                            currentState
                        }
                    }
                }

                is CreateHabitCommand.ToggleDayOfWeek -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            val currentDays = currentState.selectedDaysOfTheWeek
                            currentState.copy(selectedDaysOfTheWeek = currentDays + command.day)
                        } else {
                            currentState
                        }
                    }
                }

                is CreateHabitCommand.ToggleDayOfMonth -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            val currentDays = currentState.selectedDaysOfMonth
                            currentState.copy(selectedDaysOfMonth = currentDays + command.day)
                        } else {
                            currentState
                        }
                    }
                }

                is CreateHabitCommand.SelectColor -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            currentState.copy(color = command.color)
                        } else {
                            currentState
                        }
                    }
                }

                CreateHabitCommand.Save -> {
                    _state.update { currentState ->
                        if (currentState is CreateHabitState.Creation) {
                            createHabitUseCase(
                                name = currentState.name,
                                description = currentState.description,
                                frequency = currentState.frequency,
                                color = currentState.color
                            )
                            CreateHabitState.Finished
                        } else {
                            currentState
                        }
                    }
                }

                CreateHabitCommand.Back -> {
                    _state.update { CreateHabitState.Finished }
                }
            }
        }
    }
}

sealed interface CreateHabitCommand {

    data class InputName(val name: String) : CreateHabitCommand

    data class InputDescription(val description: String) : CreateHabitCommand

    data class SelectFrequencyType(val type: FrequencyType) : CreateHabitCommand

    data class ToggleDayOfWeek(val day: DayOfWeek) : CreateHabitCommand

    data class ToggleDayOfMonth(val day: Int) : CreateHabitCommand

    data class SelectColor(val color: Int) : CreateHabitCommand

    data object Save : CreateHabitCommand

    data object Back : CreateHabitCommand
}

sealed interface CreateHabitState {

    data class Creation(
        val name: String = "",
        val description: String? = null,
        val frequencyType: FrequencyType = FrequencyType.DAILY,
        val selectedDaysOfTheWeek: Set<DayOfWeek> = setOf(LocalDate.nowClock().dayOfWeek),
        val selectedDaysOfMonth: Set<Int> = setOf(LocalDate.nowClock().day),
        val color: Int = HabitCardDefaults.DEFAULT_COLOR,
        val error: String? = null
    ) : CreateHabitState {

        val frequency: HabitFrequency
            get() = when (frequencyType) {
                FrequencyType.DAILY -> HabitFrequency.Daily
                FrequencyType.WEEKLY -> HabitFrequency.Weekly(selectedDaysOfTheWeek)
                FrequencyType.CUSTOM -> HabitFrequency.Custom(selectedDaysOfMonth)
            }

        val isSaveEnabled: Boolean
            get() = name.isNotBlank() && when (frequencyType) {
                FrequencyType.DAILY -> true
                FrequencyType.WEEKLY -> selectedDaysOfTheWeek.isNotEmpty()
                FrequencyType.CUSTOM -> selectedDaysOfMonth.isNotEmpty()
            }
    }

    data object Finished : CreateHabitState
}