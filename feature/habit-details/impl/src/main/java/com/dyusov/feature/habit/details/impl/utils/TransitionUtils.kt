package com.dyusov.feature.habit.details.impl.utils

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

fun <S : Comparable<S>> transitionSpec(): AnimatedContentTransitionScope<S>.() -> ContentTransform =
    {
        val direction = if (targetState > initialState) -1 else 1

        slideInHorizontally(
            animationSpec = tween(300, easing = FastOutSlowInEasing),
            initialOffsetX = { it * direction }
        ) + fadeIn(tween(300)) togetherWith
                slideOutHorizontally(
                    animationSpec = tween(300, easing = FastOutSlowInEasing),
                    targetOffsetX = { -it * direction }
                ) + fadeOut(tween(300))
    }