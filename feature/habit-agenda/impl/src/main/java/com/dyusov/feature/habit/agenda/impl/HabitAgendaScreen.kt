@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.agenda.impl

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.model.Habit
import com.dyusov.core.ui.utils.SwipeLaunchedEffect
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
                        text = "Your habits",
                        fontWeight = FontWeight.Bold,
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
                    text = "Swipe to mark done or reset",
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
                    }
                )
            }
        }
    }
}

@Composable
private fun HabitCard(
    modifier: Modifier = Modifier,
    habit: Habit,
    onHabitSwipe: () -> Unit
) {
    val swipeState = rememberSwipeAnchoredDraggableState()

    SwipeLaunchedEffect(state = swipeState, onSwipe = onHabitSwipe)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(habit.color.toPastel())
        ),
        border = if (habit.isCompletedToday) BorderStroke(2.dp, Color(habit.color)) else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = habit.name,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (habit.isCompletedToday) {
                Text(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    text = "Done",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }
        }
    }
}