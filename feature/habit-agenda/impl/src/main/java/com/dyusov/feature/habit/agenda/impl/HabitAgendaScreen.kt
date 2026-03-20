@file:OptIn(ExperimentalMaterial3Api::class)

package com.dyusov.feature.habit.agenda.impl

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.dyusov.core.designsystem.ThemeMode
import com.dyusov.core.designsystem.ThemeOption
import com.dyusov.core.designsystem.ThemeViewModel
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
    habitAgendaViewModel: HabitAgendaViewModel = hiltViewModel(),
    themeViewModel: ThemeViewModel = hiltViewModel(LocalActivity.current as ComponentActivity),
    onHabitClick: (Long) -> Unit,
    onAddHabitClick: () -> Unit
) {
    val state by habitAgendaViewModel.state.collectAsState()
    val haptic = LocalHapticFeedback.current

    val themeMode by themeViewModel.themeMode.collectAsState()
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    var showThemePicker by rememberSaveable { mutableStateOf(false) }

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
                },
                actions = {
                    IconButton(
                        onClick = {
                            showThemePicker = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Palette,
                            contentDescription = "Change theme",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
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
                        habitAgendaViewModel.processCommand(
                            command = HabitAgendaCommand.ToggleHabitCompletion(habitId)
                        )
                    },
                    onHabitClick = onHabitClick,
                    onLongHabitClick = { habitId ->
                        habitAgendaViewModel.processCommand(
                            command = HabitAgendaCommand.DeleteHabit(habitId)
                        )
                    },
                    isDark = isDark
                )
            }
        }
    }

    if (showThemePicker) {
        ThemePickerSheet(
            currentMode = themeMode,
            onModeSelected = { mode ->
                themeViewModel.setThemeMode(mode)
                showThemePicker = false
            },
            onDismiss = {
                showThemePicker = false
            }
        )
    }
}

@Composable
private fun ThemePickerSheet(
    modifier: Modifier = Modifier,
    currentMode: ThemeMode,
    onModeSelected: (ThemeMode) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    val options = listOf(
        ThemeMode.LIGHT to Icons.Outlined.LightMode,
        ThemeMode.DARK to Icons.Outlined.DarkMode,
        ThemeMode.SYSTEM to Icons.Outlined.SettingsBrightness,
    ).map { (mode, icon) ->
        ThemeOption(mode, stringResource(mode.labelRes), icon)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.appearance),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.choose_how_habitify_looks),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.selectableGroup()) {
                options.forEach { option ->
                    ThemeOptionRow(
                        option = option,
                        selected = currentMode == option.mode,
                        onSelect = {
                            scope.launch {
                                sheetState.hide()
                            }.invokeOnCompletion {
                                onModeSelected(option.mode)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ThemeOptionRow(
    modifier: Modifier = Modifier,
    option: ThemeOption,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = option.icon,
            contentDescription = null,
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            modifier = Modifier.weight(1f)
        )
        RadioButton(
            selected = selected,
            onClick = null
        )
    }
}

@Composable
fun SwipeableHabitItem(
    habit: Habit,
    onHabitSwipe: (Long) -> Unit,
    onHabitClick: (Long) -> Unit,
    onLongHabitClick: (Long) -> Unit,
    isDark: Boolean
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
                onLongHabitClick = onLongHabitClick,
                isDark = isDark
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
    onLongHabitClick: (Long) -> Unit,
    isDark: Boolean
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
            containerColor = Color(habit.color.toPastel(darkTheme = isDark))
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
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}