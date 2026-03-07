package com.dyusov.feature.habit.addedit.impl.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dyusov.core.model.FrequencyType
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.feature.habit.addedit.impl.R
import com.dyusov.feature.habit.addedit.impl.utils.HabitScreenUtils
import kotlinx.datetime.DayOfWeek

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
fun SectionLabel(
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
fun HabitField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    minLines: Int = 1
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        SectionLabel(text = label)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            textStyle = TextStyle(
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = MaterialTheme.colorScheme.onSurface
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                focusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            ),
            minLines = minLines,
            placeholder = {
                Text(
                    text = placeholder,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )
            }
        )
    }
}

@Composable
fun FrequencySelector(
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
                colors = SegmentedButtonDefaults.colors().copy(
                    activeBorderColor = Color.Transparent,
                    inactiveBorderColor = Color.Transparent,
                    inactiveContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
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
fun WeekdayPicker(
    modifier: Modifier = Modifier,
    selectedDays: Set<DayOfWeek>,
    onSelectToggle: (DayOfWeek) -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.height(1.dp))
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
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            width = 0.75.dp,
                            color = if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(24.dp)
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
fun MonthDayPicker(
    modifier: Modifier = Modifier,
    selectedDays: Set<Int>,
    onSelectToggle: (Int) -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Spacer(modifier = Modifier.height(1.dp))
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
                            shape = RoundedCornerShape(24.dp)
                        )
                        .border(
                            0.75.dp,
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.Transparent
                            },
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(
                            shape = RoundedCornerShape(24.dp)
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
fun ColorPicker(
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
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(color, CircleShape)
                        )
                    }
                }
            }
        }
    }
}