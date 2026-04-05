@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.addedit.impl.editing

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
import androidx.compose.material.icons.rounded.Delete
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.ui.utils.HabitActionBottomSheet
import com.dyusov.feature.habit.addedit.impl.R
import com.dyusov.feature.habit.addedit.impl.common.ColorPicker
import com.dyusov.feature.habit.addedit.impl.common.FrequencySelector
import com.dyusov.feature.habit.addedit.impl.common.HabitField
import com.dyusov.feature.habit.addedit.impl.common.MonthDayPicker
import com.dyusov.feature.habit.addedit.impl.common.SaveHabitButton
import com.dyusov.feature.habit.addedit.impl.common.WeekdayPicker
import com.dyusov.feature.habit.addedit.impl.utils.AddEditHabitScreenUtils

@Composable
fun EditHabitScreen(
    modifier: Modifier = Modifier,
    habitId: Long,
    viewModel: EditHabitViewModel = hiltViewModel(
        creationCallback = { factory: EditHabitViewModel.Factory ->
            factory.create(habitId)
        },
    ),
    onFinished: () -> Unit
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvent.collect {
            onFinished()
        }
    }

    when (val currentState = state) {
        EditHabitState.Initial -> {}
        is EditHabitState.Editing -> {
            val haptic = LocalHapticFeedback.current

            val habitColor = remember(currentState.habit.color) {
                Color(currentState.habit.color)
            }
            val onHabitColor = remember(habitColor) {
                if (habitColor.luminance() > 0.5f) {
                    Color.Black
                } else {
                    Color.White
                }
            }

            var showDeleteSheet by remember { mutableStateOf(false) }

            if (showDeleteSheet) {
                HabitActionBottomSheet(
                    habitColor = habitColor,
                    onHabitColor = onHabitColor,
                    onConfirm = {
                        showDeleteSheet = false
                        viewModel.processCommand(EditHabitCommand.Delete)
                    },
                    onDismiss = {
                        showDeleteSheet = false
                    },
                    label = stringResource(R.string.delete_habit_title),
                    description = stringResource(R.string.delete_habit_description),
                    confirmLabel = stringResource(R.string.delete_habit_confirm),
                    cancelLabel = stringResource(R.string.cancel)
                )
            }
            Scaffold(
                modifier = modifier,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(R.string.edit_habit),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        navigationIcon = {
                            FilledTonalIconButton(
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                                    viewModel.processCommand(EditHabitCommand.Back)
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
                        actions = {
                            FilledTonalIconButton(
                                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                                onClick = {
                                    showDeleteSheet = true
                                },
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Delete,
                                    contentDescription = stringResource(R.string.delete_habit)
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
                            value = currentState.habit.name,
                            placeholder = stringResource(R.string.e_g_morning_run),
                            onValueChange = {
                                viewModel.processCommand(
                                    command = EditHabitCommand.InputName(it)
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Description
                        HabitField(
                            label = stringResource(R.string.description),
                            value = currentState.habit.description ?: "",
                            placeholder = stringResource(R.string.optional_details),
                            onValueChange = {
                                viewModel.processCommand(
                                    command = EditHabitCommand.InputDescription(it)
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
                                    command = EditHabitCommand.SelectFrequencyType(it)
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
                                        command = EditHabitCommand.ToggleDayOfWeek(it)
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
                                        command = EditHabitCommand.ToggleDayOfMonth(it)
                                    )
                                }
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Color
                        ColorPicker(
                            selectedColor = currentState.habit.color,
                            colors = AddEditHabitScreenUtils.habitColors,
                            onSelect = {
                                viewModel.processCommand(
                                    command = EditHabitCommand.SelectColor(it)
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
                                    command = EditHabitCommand.Save
                                )
                            },
                            enabled = currentState.isSaveEnabled
                        )
                    }
                }
            }
        }
    }
}