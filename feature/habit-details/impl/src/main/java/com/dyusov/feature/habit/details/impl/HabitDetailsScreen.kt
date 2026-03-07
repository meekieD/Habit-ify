@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.details.impl

import android.util.Log
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.common.datetime.minus
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.datetime.plus
import com.dyusov.core.common.datetime.toLocalDate
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.feature.habit.details.impl.utils.frequencyLabel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth


@Composable
fun HabitDetailsScreen(
    modifier: Modifier = Modifier,
    habitId: Long,
    viewModel: HabitDetailsViewModel = hiltViewModel(
        creationCallback = { factory: HabitDetailsViewModel.Factory ->
            factory.create(habitId)
        },
    ),
    onEditHabit: (Long) -> Unit,
    onFinished: () -> Unit
) {
    Log.d("HabitDetailsScreen", "Composed with habitId=$habitId")
    val state by viewModel.state.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvent.collect {
            onFinished()
        }
    }

    when (state) {
        is HabitDetailsState.Initial -> {}
        is HabitDetailsState.Content -> HabitDetailsContentScreen(
            modifier = modifier,
            state = state as HabitDetailsState.Content,
            onCommand = viewModel::processCommand,
            habitId = habitId,
            onEditHabit = onEditHabit
        )
    }
}

@Composable
private fun HabitDetailsContentScreen(
    state: HabitDetailsState.Content,
    onCommand: (HabitDetailsCommand) -> Unit,
    habitId: Long,
    onEditHabit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val habitColor = Color(state.habit.color)
    val onHabitColor = if (habitColor.luminance() > 0.5f){
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.onBackground
    }

    val completedDates: Set<LocalDate> = remember(state.completions) {
        state.completions.mapNotNull { completion ->
            runCatching {
                completion.timestamp.toLocalDate()
            }.getOrNull()
        }.toSet()
    }

    val today = remember { LocalDate.nowClock() }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Details",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            onCommand(HabitDetailsCommand.Back)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onClick = {
                            onEditHabit(habitId)
                        },
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = "Edit habit"
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
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Habit Header
            Column(
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Spacer(Modifier.height(8.dp))
                // Frequency badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(habitColor, CircleShape)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = frequencyLabel(state.habit.frequency),
                        color = habitColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text = state.habit.name,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                state.habit.description?.takeIf { it.isNotBlank() }?.let { desc ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = desc,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Streak Card
            StreakCard(
                streak = state.currentStreak,
                habitColor = habitColor,
                onHabitColor = onHabitColor,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(24.dp))

            // Calendar
            CalendarCard(
                currentMonth = state.currentMonth,
                completedDates = completedDates,
                habitColor = habitColor,
                today = today,
                onPreviousMonth = {
                    val prev = state.currentMonth.minus(1)
                    onCommand(HabitDetailsCommand.SetDisplayedMonth(prev))
                },
                onNextMonth = {
                    val next = state.currentMonth.plus(1)
                    onCommand(HabitDetailsCommand.SetDisplayedMonth(next))
                },
                onDateToggle = { date ->
                    onCommand(HabitDetailsCommand.ToggleHabitCompletionOnDate(date))
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}


@Composable
private fun StreakCard(
    streak: Int,
    habitColor: Color,
    onHabitColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = habitColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    text = "Current Streak",
                    color = onHabitColor.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$streak",
                        color = onHabitColor,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (streak == 1) "day" else "days",
                        color = onHabitColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = "Streak fire icon",
                tint = onHabitColor.copy(alpha = if (streak > 0) 0.85f else 0.25f),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}


@Composable
private fun CalendarCard(
    currentMonth: YearMonth,
    completedDates: Set<LocalDate>,
    habitColor: Color,
    today: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateToggle: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledTonalIconButton(
                    modifier = Modifier.size(32.dp),
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    onClick = {
                        onPreviousMonth()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = "Previous month",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${
                        currentMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }
                    } ${currentMonth.year}",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                val canGoNext = currentMonth < YearMonth.now()
                FilledTonalIconButton(
                    modifier = Modifier.size(32.dp),
                    enabled = canGoNext,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    onClick = {
                        if (canGoNext) {
                            onNextMonth()
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = "Next month",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Day-of-week headers
            Row(modifier = Modifier.fillMaxWidth()) {
                listOf("M", "T", "W", "T", "F", "S", "S").forEach { label ->
                    Text(
                        text = label,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            CalendarGrid(
                month = currentMonth,
                completedDates = completedDates,
                habitColor = habitColor,
                today = today,
                onDateToggle = onDateToggle
            )
        }
    }
}

@Composable
private fun CalendarGrid(
    month: YearMonth,
    completedDates: Set<LocalDate>,
    habitColor: Color,
    today: LocalDate,
    onDateToggle: (LocalDate) -> Unit
) {
    val firstDay = LocalDate(month.year, month.month, 1)
    val startOffset = (firstDay.dayOfWeek.ordinal) % 7 // Mon=0 … Sun=6
    val daysInMonth = month.numberOfDays

    val cells = buildList {
        repeat(startOffset) { add(null) }
        for (day in 1..daysInMonth) {
            add(LocalDate(month.year, month.month, day))
        }
        while (size % 7 != 0) add(null)
    }

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        cells.chunked(7).forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f)) {
                        if (date != null) {
                            CalendarDayCell(
                                date = date,
                                isCompleted = date in completedDates,
                                isToday = date == today,
                                isFuture = date > today,
                                habitColor = habitColor,
                                onClick = { onDateToggle(date) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    date: LocalDate,
    isCompleted: Boolean,
    isToday: Boolean,
    isFuture: Boolean,
    habitColor: Color,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                color = when {
                    isCompleted -> habitColor
                    isToday -> habitColor.copy(alpha = 0.25f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isToday && !isCompleted) Modifier.border(
                    width = HabitCardDefaults.borderWidth,
                    color = habitColor.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(24.dp)
                ) else Modifier
            )
            .clickable(
                enabled = !isFuture,
                onClick = onClick
            )
            .alpha(if (isFuture) 0.5f else 1f)
    ) {
        Text(
            text = date.day.toString(),
            color = when {
                isCompleted -> if (habitColor.luminance() > 0.5f) MaterialTheme.colorScheme.onSurface else Color.White
                isToday -> habitColor
                isFuture -> MaterialTheme.colorScheme.onSurfaceVariant
                else ->  MaterialTheme.colorScheme.onBackground
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isToday || isCompleted) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}