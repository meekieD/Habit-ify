@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.agenda.impl

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.Habit
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.core.ui.utils.SwipeActionState
import com.dyusov.core.ui.utils.getActionText
import com.dyusov.core.ui.utils.toPastel
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

@Composable
fun HabitAgendaScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitAgendaViewModel = hiltViewModel(),
    onHabitClick: (Long) -> Unit,
    onAddHabitClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                title = {
                    Text(
                        text = stringResource(R.string.your_habits),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 32.sp,
                        textAlign = TextAlign.Center,
                    )
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.VirtualKey)
                    onAddHabitClick()
                },
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add habit"
                    )
                },
                text = {
                    Text(text = stringResource(R.string.new_habit))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = paddingValues,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(R.string.swipe_to_mark_done_or_reset),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 1.25.em,
                )
            }

            items(
                items = state.items,
                key = { it.id }
            ) { habit ->
                SwipeableHabitItem(
                    habit = habit,
                    onHabitSwipe = { habitId ->
                        viewModel.processCommand(
                            command = HabitAgendaCommand.ToggleHabitCompletion(habitId)
                        )
                    },
                    onHabitClick = onHabitClick,
                    onLongHabitClick = { habitId ->
                        viewModel.processCommand(
                            command = HabitAgendaCommand.DeleteHabit(habitId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun SwipeableHabitItem(
    habit: Habit,
    onHabitSwipe: (Long) -> Unit,
    onHabitClick: (Long) -> Unit,
    onLongHabitClick: (Long) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState(
        positionalThreshold = with(LocalDensity.current) {
            {
                32.dp.toPx()
            }
        }
    )
    val swipeActionState = remember(habit.isCompletedToday) {
        SwipeActionState(isCompleted = habit.isCompletedToday)
    }

    // add haptic feedback on swipe
    val haptic = LocalHapticFeedback.current
    LaunchedEffect(dismissState) {
        snapshotFlow { dismissState.currentValue }
            .filter { it != SwipeToDismissBoxValue.Settled }
            .collect {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
            }
    }

    SwipeToDismissBox(
        state = dismissState,
        onDismiss = {
            coroutineScope.launch {
                dismissState.reset()
                onHabitSwipe(habit.id)
            }
        },
        backgroundContent = {
            SwipeBackgroundLayer(
                actionState = swipeActionState,
                dismissDirection = dismissState.dismissDirection
            )
        },
        content = {
            HabitCardContent(
                habit = habit,
                onHabitClick = { habitId ->
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onHabitClick(habitId)
                },
                onLongHabitClick = onLongHabitClick
            )
        }
    )
}

@Composable
private fun SwipeBackgroundLayer(
    actionState: SwipeActionState,
    dismissDirection: SwipeToDismissBoxValue,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = actionState.backgroundColor,
                shape = RoundedCornerShape(HabitCardDefaults.cornerRadius)
            )
            .padding(
                horizontal = HabitCardDefaults.horizontalPadding,
                vertical = HabitCardDefaults.verticalPadding
            )
    ) {
        when (dismissDirection) {
            SwipeToDismissBoxValue.StartToEnd -> {
                ActionIcon(
                    modifier = Modifier.align(Alignment.CenterStart),
                    actionState = actionState
                )
            }

            SwipeToDismissBoxValue.EndToStart -> {
                ActionIcon(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    actionState = actionState
                )
            }

            SwipeToDismissBoxValue.Settled -> Unit
        }
    }
}

@Composable
private fun ActionIcon(
    modifier: Modifier = Modifier,
    actionState: SwipeActionState
) {
    Icon(
        modifier = modifier.size(HabitCardDefaults.iconSize),
        imageVector = actionState.actionIcon,
        contentDescription = actionState.getActionText(LocalContext.current),
        tint = actionState.actionColor
    )
}

@Composable
private fun HabitCardContent(
    modifier: Modifier = Modifier,
    habit: Habit,
    onHabitClick: (Long) -> Unit,
    onLongHabitClick: (Long) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    onHabitClick(habit.id)
                },
                onLongClick = {
                    onLongHabitClick(habit.id)
                }
            ),
        shape = RoundedCornerShape(HabitCardDefaults.cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = Color(habit.color.toPastel())
        ),
        border = if (habit.isCompletedToday) {
            BorderStroke(HabitCardDefaults.borderWidth, Color(habit.color))
        } else {
            null
        },
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = HabitCardDefaults.verticalPadding,
                    horizontal = HabitCardDefaults.horizontalPadding
                ),
            text = habit.name,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}