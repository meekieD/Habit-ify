package com.dyusov.core.ui.utils

import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.filter

@Composable
fun rememberSwipeAnchoredDraggableState(
    initialValue: SwipeState = SwipeState.CENTER,
    swipeThreshold: Dp = 100.dp
): AnchoredDraggableState<SwipeState> {
    val swipeThresholdPx = with(LocalDensity.current) {
        swipeThreshold.toPx()
    }

    return remember(swipeThresholdPx) {
        AnchoredDraggableState(initialValue = initialValue).apply {
            updateAnchors(
                DraggableAnchors {
                    SwipeState.LEFT at -swipeThresholdPx
                    SwipeState.CENTER at 0f
                    SwipeState.RIGHT at swipeThresholdPx
                }
            )
        }
    }
}

@Composable
fun SwipeLaunchedEffect(
    state: AnchoredDraggableState<SwipeState>,
    onSwipe: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current

    LaunchedEffect(state) {
        snapshotFlow { state.settledValue }
            .filter { it != SwipeState.CENTER }
            .collect {
                haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                onSwipe()
                state.animateTo(SwipeState.CENTER)
            }
    }
}

@Composable
fun rememberSwipeFlingBehavior(
    state: AnchoredDraggableState<SwipeState>,
    positionalThreshold: Float = 0.5f,
) = AnchoredDraggableDefaults.flingBehavior(
    state = state,
    positionalThreshold = { distance ->
        distance * positionalThreshold
    },
    animationSpec = tween(durationMillis = 300),
)

@Composable
fun rememberDisplayedCompletionState(
    isHabitCompleted: Boolean,
    swipeState: AnchoredDraggableState<SwipeState>,
    key: Any
): Boolean {
    val displayedState = remember(key) {
        mutableStateOf(isHabitCompleted)
    }

    LaunchedEffect(key, isHabitCompleted, swipeState) {
        snapshotFlow { swipeState.offset }
            .collect { offset ->
                if (offset == 0f) {
                    displayedState.value = isHabitCompleted
                }
            }
    }

    return displayedState.value
}