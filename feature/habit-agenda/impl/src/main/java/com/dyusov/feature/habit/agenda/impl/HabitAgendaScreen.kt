@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.agenda.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.Habit
import com.dyusov.core.ui.habit.HabitCardDefaults
import com.dyusov.core.ui.utils.SwipeActionState
import com.dyusov.core.ui.utils.SwipeLaunchedEffect
import com.dyusov.core.ui.utils.SwipeState
import com.dyusov.core.ui.utils.getActionText
import com.dyusov.core.ui.utils.rememberDisplayedCompletionState
import com.dyusov.core.ui.utils.rememberSwipeAnchoredDraggableState
import com.dyusov.core.ui.utils.rememberSwipeFlingBehavior
import com.dyusov.core.ui.utils.toPastel
import kotlin.math.roundToInt

@Composable
fun HabitAgendaScreen(
    modifier: Modifier = Modifier,
    viewModel: HabitAgendaViewModel = hiltViewModel(),
    onFirstScreenButtonClick: () -> Unit,
    onSecondScreenButtonClick: () -> Unit
) {
    val state by viewModel.state.collectAsState()

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
                HabitCard(
                    habit = habit,
                    onHabitSwipe = {
                        viewModel.processCommand(
                            HabitAgendaCommand.ToggleHabitCompletion(habit.id)
                        )
                    },
                    onHabitClick = onFirstScreenButtonClick
                )
            }
        }
    }
}

@Composable
private fun HabitCard(
    modifier: Modifier = Modifier,
    habit: Habit,
    onHabitSwipe: () -> Unit,
    onHabitClick: () -> Unit
) {
    val swipeState = rememberSwipeAnchoredDraggableState()

    val displayedAsCompleted = rememberDisplayedCompletionState(
        isHabitCompleted = habit.isCompletedToday,
        swipeState = swipeState,
        key = habit.id
    )

    SwipeLaunchedEffect(state = swipeState, onSwipe = onHabitSwipe)

    val swipeActionState = remember(displayedAsCompleted) {
        SwipeActionState(isCompleted = displayedAsCompleted)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = HabitCardDefaults.cardHorizontalMargin)
            .clip(RoundedCornerShape(HabitCardDefaults.cornerRadius))
    ) {
        SwipeBackgroundLayer(actionState = swipeActionState)

        HabitCardContent(
            habit = habit,
            swipeState = swipeState,
            onHabitClick = onHabitClick
        )
    }
}

@Composable
private fun SwipeBackgroundLayer(
    actionState: SwipeActionState,
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SwipeActionContent(
                actionState = actionState,
                iconFirst = false
            )
            SwipeActionContent(
                actionState = actionState,
                iconFirst = true
            )
        }
    }
}

@Composable
private fun SwipeActionContent(
    actionState: SwipeActionState,
    iconFirst: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconFirst) {
            ActionIcon(actionState)
            Spacer(modifier = Modifier.width(HabitCardDefaults.iconSpacing))
            ActionText(actionState)
        } else {
            ActionText(actionState)
            Spacer(modifier = Modifier.width(HabitCardDefaults.iconSpacing))
            ActionIcon(actionState)
        }
    }
}

@Composable
private fun ActionIcon(actionState: SwipeActionState) {
    Icon(
        modifier = Modifier.size(HabitCardDefaults.iconSize),
        imageVector = actionState.actionIcon,
        contentDescription = actionState.getActionText(LocalContext.current),
        tint = actionState.actionColor
    )
}

@Composable
private fun ActionText(actionState: SwipeActionState) {
    Text(
        text = actionState.getActionText(LocalContext.current),
        color = actionState.actionColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun HabitCardContent(
    habit: Habit,
    swipeState: AnchoredDraggableState<SwipeState>,
    onHabitClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onHabitClick)
            .offset {
                IntOffset(
                    x = swipeState.requireOffset().roundToInt(),
                    y = 0
                )
            }
            .anchoredDraggable(
                state = swipeState,
                orientation = Orientation.Horizontal,
                flingBehavior = rememberSwipeFlingBehavior(state = swipeState),
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = HabitCardDefaults.verticalPadding,
                    horizontal = HabitCardDefaults.horizontalPadding
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = habit.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}