@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.addedit.impl.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.FrequencyType
import com.dyusov.feature.habit.addedit.impl.R
import com.dyusov.feature.habit.addedit.impl.common.ColorPicker
import com.dyusov.feature.habit.addedit.impl.common.FrequencySelector
import com.dyusov.feature.habit.addedit.impl.common.HabitField
import com.dyusov.feature.habit.addedit.impl.common.MonthDayPicker
import com.dyusov.feature.habit.addedit.impl.common.SaveHabitButton
import com.dyusov.feature.habit.addedit.impl.common.WeekdayPicker
import com.dyusov.feature.habit.addedit.impl.utils.HabitScreenUtils

@Composable
fun CreateHabitScreen(
    modifier: Modifier = Modifier,
    viewModel: CreateHabitViewModel = hiltViewModel(),
    onFinished: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is CreateHabitState.Creation -> {
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.new_habit),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            FilledTonalIconButton(
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                onClick = {
                                    viewModel.processCommand(CreateHabitCommand.Back)
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                    contentDescription = stringResource(R.string.back_to_main_screen)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                            actionIconContentColor = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Name
                        HabitField(
                            label = stringResource(R.string.habit_name),
                            value = currentState.name,
                            placeholder = "e.g. Morning run",
                            onValueChange = {
                                viewModel.processCommand(
                                    command = CreateHabitCommand.InputName(it)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        HabitField(
                            label = stringResource(R.string.description),
                            value = currentState.description ?: "",
                            placeholder = "Optional details…",
                            onValueChange = {
                                viewModel.processCommand(
                                    command = CreateHabitCommand.InputDescription(it)
                                )
                            },
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Frequency type
                        FrequencySelector(
                            selected = currentState.frequencyType,
                            onSelect = {
                                viewModel.processCommand(
                                    command = CreateHabitCommand.SelectFrequencyType(it)
                                )
                            }
                        )

                        // Conditional day pickers
                        AnimatedVisibility(
                            visible = currentState.frequencyType == FrequencyType.WEEKLY,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            WeekdayPicker(
                                selectedDays = currentState.selectedDaysOfTheWeek,
                                onSelectToggle = {
                                    viewModel.processCommand(
                                        command = CreateHabitCommand.ToggleDayOfWeek(it)
                                    )
                                }
                            )
                        }

                        AnimatedVisibility(
                            visible = currentState.frequencyType == FrequencyType.CUSTOM,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            MonthDayPicker(
                                selectedDays = currentState.selectedDaysOfMonth,
                                onSelectToggle = {
                                    viewModel.processCommand(
                                        command = CreateHabitCommand.ToggleDayOfMonth(it)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Color
                        ColorPicker(
                            selectedColor = currentState.color,
                            colors = HabitScreenUtils.habitColors,
                            onSelect = {
                                viewModel.processCommand(
                                    command = CreateHabitCommand.SelectColor(it)
                                )
                            }
                        )
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(top = 24.dp, start = 16.dp, end = 16.dp)
                    ) {
                        SaveHabitButton(
                            onClick = {
                                viewModel.processCommand(
                                    command = CreateHabitCommand.Save
                                )
                            },
                            enabled = currentState.isSaveEnabled
                        )
                    }
                }
            }
        }

        CreateHabitState.Finished -> {
            LaunchedEffect(key1 = Unit) {
                onFinished()
            }
        }
    }
}
