@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.addedit.impl.creation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.feature.habit.addedit.impl.R
import com.dyusov.feature.habit.addedit.impl.utils.HabitScreenUtils
import kotlinx.datetime.DayOfWeek

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
                            Icon(
                                modifier = Modifier
                                    .padding(start = 16.dp, end = 8.dp)
                                    .clickable {
                                        viewModel.processCommand(CreateHabitCommand.Back)
                                    },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_to_main_screen),
                            )
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

@Composable
fun SaveHabitButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(HabitCardDefaults.cornerRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(
            text = stringResource(R.string.save_habit),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.7.sp
        )
    }
}

@Composable
private fun SectionLabel(
    modifier: Modifier = Modifier,
    text: String
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.7.sp
    )
}

@Composable
private fun HabitField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionLabel(text = label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged {
                    isFocused = it.isFocused
                }
                .border(
                    width = 0.75.dp,
                    color = if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    shape = RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            ),
            minLines = minLines,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        )
    }
}

@Composable
private fun FrequencySelector(
    modifier: Modifier = Modifier,
    selected: FrequencyType,
    onSelect: (FrequencyType) -> Unit
) {
    SectionLabel(text = stringResource(R.string.frequency))
    SingleChoiceSegmentedButtonRow(
        modifier = modifier.fillMaxWidth()
    ) {
        HabitScreenUtils.frequencySelectorOptions.forEachIndexed { index, (type, label) ->
            val isSelected = selected == type
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = HabitScreenUtils.frequencySelectorOptions.size
                ),
                onClick = {
                    onSelect(type)
                },
                selected = isSelected,
                label = {
                    Text(
                        text = label,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            )
        }
    }
}

@Composable
private fun WeekdayPicker(
    modifier: Modifier = Modifier,
    selectedDays: Set<DayOfWeek>,
    onSelectToggle: (DayOfWeek) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionLabel(text = stringResource(R.string.days_of_week))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HabitScreenUtils.days.forEach { (day, label) ->
                val isSelected = day in selectedDays
                Box(
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                            },
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .border(
                            width = 0.75.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            },
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .clip(
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .clickable {
                            onSelectToggle(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun MonthDayPicker(
    modifier: Modifier = Modifier,
    selectedDays: Set<Int>,
    onSelectToggle: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionLabel(text = stringResource(R.string.days_of_month))
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 288.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            userScrollEnabled = false
        ) {
            items(31) { dayIndex ->
                val day = dayIndex + 1
                val isSelected = day in selectedDays
                Box(
                    Modifier
                        .aspectRatio(1f)
                        .background(
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                            },
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .border(
                            0.75.dp,
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            },
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .clip(
                            shape = if (isSelected) {
                                RoundedCornerShape(16.dp)
                            } else {
                                RoundedCornerShape(24.dp)
                            }
                        )
                        .clickable {
                            onSelectToggle(day)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "$day",
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun ColorPicker(
    modifier: Modifier = Modifier,
    selectedColor: Int,
    colors: List<Int>,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionLabel(text = stringResource(R.string.color))
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            colors.forEach { colorInt ->
                val color = Color(colorInt)
                val isSelected = colorInt == selectedColor
                Box(
                    Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .background(color.copy(alpha = 0.25f), CircleShape)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) color else color.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable {
                            onSelect(colorInt)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Box(modifier = Modifier
                            .size(16.dp)
                            .background(color, CircleShape))
                    }
                }
            }
        }
    }
}