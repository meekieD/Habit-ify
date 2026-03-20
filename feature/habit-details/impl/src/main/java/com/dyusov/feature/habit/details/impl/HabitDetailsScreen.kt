@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.details.impl

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.common.datetime.localizedMonthName
import com.dyusov.core.common.datetime.minus
import com.dyusov.core.common.datetime.now
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.datetime.plus
import com.dyusov.core.common.datetime.toLocalDate
import com.dyusov.core.designsystem.ThemeMode
import com.dyusov.core.designsystem.ThemeViewModel
import com.dyusov.core.model.HabitFrequency
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.feature.habit.details.impl.utils.frequencyLabel
import com.dyusov.feature.habit.details.impl.utils.navButtonColors
import com.dyusov.feature.habit.details.impl.utils.surfaceIconColors
import kotlinx.datetime.DayOfWeek
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
    themeViewModel: ThemeViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onEditHabit: (Long) -> Unit,
    onFinished: () -> Unit
) {
    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

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
            onEditHabit = onEditHabit,
            isDark = isDark
        )
    }
}

@Composable
private fun HabitDetailsContentScreen(
    modifier: Modifier = Modifier,
    state: HabitDetailsState.Content,
    onCommand: (HabitDetailsCommand) -> Unit,
    onEditHabit: (Long) -> Unit,
    isDark: Boolean
) {
    val habitColor = remember(state.habit.color) {
        Color(state.habit.color)
    }
    val onHabitColor = remember(habitColor) {
        if (habitColor.luminance() > 0.5f) {
            Color.Black
        } else {
            Color.White
        }
    }

    val completedDates: Set<LocalDate> = remember(state.monthCompletions) {
        state.monthCompletions.mapNotNull { completion ->
            runCatching {
                completion.timestamp.toLocalDate()
            }.getOrNull()
        }.toSet()
    }
    val today = remember { LocalDate.nowClock() }

    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.details),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                            onCommand(HabitDetailsCommand.Back)
                        },
                        colors = surfaceIconColors
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    FilledTonalIconButton(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onEditHabit(state.habit.id)
                        },
                        colors = surfaceIconColors
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit_habit)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Habit header
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
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

            StreakCard(
                streak = state.currentStreak,
                habitColor = if (isDark) habitColor.copy(alpha = 0.5f) else habitColor,
                onHabitColor = onHabitColor,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            Spacer(Modifier.height(24.dp))

            CalendarCard(
                currentMonth = state.currentMonth,
                completedDates = completedDates,
                frequency = state.habit.frequency,
                habitColor = if (isDark) habitColor.copy(alpha = 0.5f) else habitColor,
                today = today,
                onPreviousMonth = {
                    onCommand(
                        HabitDetailsCommand.SetDisplayedMonth(
                            selectedMonth = state.currentMonth.minus(1)
                        )
                    )
                },
                onNextMonth = {
                    onCommand(
                        HabitDetailsCommand.SetDisplayedMonth(
                            selectedMonth = state.currentMonth.plus(1)
                        )
                    )
                },
                onDateToggle = { date ->
                    onCommand(HabitDetailsCommand.ToggleHabitCompletionOnDate(date))
                },
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(Modifier.height(16.dp))

            StatsCard(
                modifier = Modifier.padding(horizontal = 24.dp),
                totalCompletions = state.totalCompletions,
                successRate = state.successRate,
                bestStreak = state.bestStreak,
                habitColor = habitColor
            )
        }
    }
}


@Composable
private fun StreakCard(
    modifier: Modifier = Modifier,
    streak: Int,
    habitColor: Color,
    onHabitColor: Color
) {
    var previousStreak by remember { mutableIntStateOf(streak) }
    var isIncreasing by remember { mutableStateOf(true) }

    LaunchedEffect(streak) {
        isIncreasing = streak >= previousStreak
    }
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
                    text = stringResource(R.string.current_streak),
                    color = onHabitColor.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 0.8.sp
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.Bottom) {
                    AnimatedContent(
                        targetState = streak,
                        transitionSpec = {
                            val slideIn = if (isIncreasing) 1 else -1
                            slideInVertically(
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                initialOffsetY = { it * slideIn }
                            ) + fadeIn(tween(200)) togetherWith
                                    slideOutVertically(
                                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                                        targetOffsetY = { -it * slideIn }
                                    ) + fadeOut(tween(200))
                        }
                    ) { displayedStreakValue ->
                        Text(
                            text = "$displayedStreakValue",
                            color = onHabitColor,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (streak == 1) {
                            stringResource(R.string.day)
                        } else if (streak in (2..4)) {
                            stringResource(R.string.days_from_2_to_4)
                        } else {
                            stringResource(R.string.days_from_5_to_infinity)
                        },
                        color = onHabitColor.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = stringResource(R.string.streak_fire_icon),
                tint = onHabitColor.copy(alpha = if (streak > 0) 0.85f else 0.25f),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}


@Composable
private fun CalendarCard(
    modifier: Modifier = Modifier,
    currentMonth: YearMonth,
    completedDates: Set<LocalDate>,
    frequency: HabitFrequency,
    habitColor: Color,
    today: LocalDate,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateToggle: (LocalDate) -> Unit
) {
    val canGoNext = currentMonth < YearMonth.now()

    var slideDirection by remember { mutableIntStateOf(1) }
    var previousMonth by remember { mutableStateOf(currentMonth) }

    LaunchedEffect(currentMonth) {
        slideDirection = if (currentMonth > previousMonth) 1 else -1
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledTonalIconButton(
                    modifier = Modifier.size(32.dp),
                    colors = navButtonColors,
                    onClick = onPreviousMonth
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.ChevronLeft,
                        contentDescription = stringResource(R.string.previous_month)
                    )
                }

                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = {
                        val direction = slideDirection
                        slideInHorizontally(
                            animationSpec = tween(300, easing = FastOutSlowInEasing),
                            initialOffsetX = { it * direction }
                        ) + fadeIn(tween(300)) togetherWith
                                slideOutHorizontally(
                                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                                    targetOffsetX = { -it * direction }
                                ) + fadeOut(tween(300))
                    }
                ) { month ->
                    Text(
                        text = stringResource(
                            R.string.month_label,
                            month.localizedMonthName().lowercase().replaceFirstChar(Char::uppercaseChar),
                            month.year
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                FilledTonalIconButton(
                    modifier = Modifier.size(32.dp),
                    enabled = canGoNext,
                    colors = navButtonColors,
                    onClick = onNextMonth
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = Icons.Rounded.ChevronRight,
                        contentDescription = stringResource(R.string.next_month)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Day-of-week headers
            Row(modifier = Modifier.fillMaxWidth()) {
                DayOfWeek.entries.map { it.name[0].uppercase() }.forEach { label ->
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

            AnimatedContent(
                targetState = currentMonth,
                transitionSpec = {
                    val direction = slideDirection
                    slideInHorizontally(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        initialOffsetX = { it * direction }
                    ) + fadeIn(tween(300)) togetherWith
                            slideOutHorizontally(
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                targetOffsetX = { -it * direction }
                            ) + fadeOut(tween(300))
                }
            ) { month ->
                CalendarGrid(
                    month = month,
                    completedDates = completedDates,
                    frequency = frequency,
                    habitColor = habitColor,
                    today = today,
                    onDateToggle = onDateToggle
                )
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    modifier: Modifier = Modifier,
    month: YearMonth,
    completedDates: Set<LocalDate>,
    frequency: HabitFrequency,
    habitColor: Color,
    today: LocalDate,
    onDateToggle: (LocalDate) -> Unit
) {
    val cells = remember(month) {
        val startOffset = LocalDate(month.year, month.month, 1).dayOfWeek.ordinal % 7
        buildList {
            repeat(startOffset) { add(null) }
            for (day in 1..month.numberOfDays) {
                add(LocalDate(month.year, month.month, day))
            }
        }
    }

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(7),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        userScrollEnabled = false
    ) {
        items(cells) { date ->
            if (date != null) {
                CalendarDayCell(
                    date = date,
                    isCompleted = date in completedDates,
                    isScheduledMissed = when (frequency) {
                        is HabitFrequency.Weekly -> {
                            date.dayOfWeek in frequency.daysOfWeek &&
                                    date <= today &&
                                    date !in completedDates
                        }

                        is HabitFrequency.Custom -> {
                            date.day in frequency.daysOfMonth &&
                                    date <= today &&
                                    date !in completedDates
                        }

                        HabitFrequency.Daily -> {
                            date <= today && date !in completedDates
                        }
                    },
                    isToday = date == today,
                    isFuture = date > today,
                    habitColor = habitColor,
                    onClick = {
                        onDateToggle(date)
                    }
                )
            } else {
                Box(Modifier.aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    modifier: Modifier = Modifier,
    date: LocalDate,
    isCompleted: Boolean,
    isScheduledMissed: Boolean,
    isToday: Boolean,
    isFuture: Boolean,
    habitColor: Color,
    onClick: () -> Unit
) {
    val cellShape = RoundedCornerShape(24.dp)
    val haptic = LocalHapticFeedback.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .aspectRatio(1f)
            .clip(cellShape)
            .background(
                when {
                    isCompleted -> habitColor
                    isToday -> habitColor.copy(alpha = 0.25f)
                    else -> Color.Transparent
                }
            )
            .then(
                if (isCompleted || isScheduledMissed) {
                    Modifier.border(
                        HabitCardDefaults.borderWidth,
                        habitColor.copy(alpha = 0.5f),
                        cellShape
                    )
                } else {
                    Modifier
                }
            )
            .clickable(enabled = !isFuture) {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                onClick()
            }
            .alpha(if (isFuture) 0.5f else 1f)
    ) {
        Text(
            text = date.day.toString(),
            color = when {
                isCompleted -> if (habitColor.luminance() > 0.5f) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    Color.White
                }

                isScheduledMissed -> habitColor
                isToday -> habitColor
                isFuture -> MaterialTheme.colorScheme.onSurfaceVariant
                else -> MaterialTheme.colorScheme.onBackground
            },
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isToday || isCompleted || isScheduledMissed) {
                FontWeight.Bold
            } else {
                FontWeight.Normal
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun StatsCard(
    modifier: Modifier = Modifier,
    totalCompletions: Int,
    successRate: Float,
    bestStreak: Int,
    habitColor: Color
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.total),
                    value = totalCompletions.toString(),
                    habitColor = habitColor
                )

                VerticalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                StatItem(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.success),
                    value = "${(successRate * 100).toInt()}%",
                    habitColor = habitColor
                )

                VerticalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )

                StatItem(
                    modifier = Modifier.weight(1f),
                    label = stringResource(R.string.best),
                    value = bestStreak.toString(),
                    habitColor = habitColor
                )
            }
        }
    }
}

@Composable
fun StatItem(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    habitColor: Color
) {
    var previousValue by remember { mutableStateOf(value) }
    var isIncreasing by remember { mutableStateOf(true) }

    LaunchedEffect(value) {
        val previous = previousValue.filter { it.isDigit() }.toIntOrNull() ?: 0
        val current = value.filter { it.isDigit() }.toIntOrNull() ?: 0
        isIncreasing = current >= previous
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.clip(RoundedCornerShape(4.dp))
        ) {
            AnimatedContent(
                targetState = value,
                transitionSpec = {
                    val slideIn = if (isIncreasing) 1 else -1
                    slideInVertically(
                        animationSpec = tween(300, easing = FastOutSlowInEasing),
                        initialOffsetY = { it * slideIn }
                    ) + fadeIn(tween(200)) togetherWith
                            slideOutVertically(
                                animationSpec = tween(300, easing = FastOutSlowInEasing),
                                targetOffsetY = { -it * slideIn }
                            ) + fadeOut(tween(200))
                }
            ) { displayValue ->
                Text(
                    text = displayValue,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = habitColor
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 1.2.sp
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
        )
    }
}