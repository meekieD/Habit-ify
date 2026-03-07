package com.dyusov.feature.habit.details.impl.utils

import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dyusov.core.model.HabitFrequency

fun frequencyLabel(frequency: HabitFrequency): String = when (frequency) {
    is HabitFrequency.Daily -> "DAILY"
    is HabitFrequency.Weekly -> {
        val days = frequency.daysOfWeek
            .sortedBy { it.ordinal }
            .joinToString(", ") { it.name.take(3) }
        "WEEKLY · $days"
    }

    is HabitFrequency.Custom -> {
        val days = frequency.daysOfMonth.sorted().joinToString(", ") { ordinal(it) }
        "MONTHLY · $days"
    }
}

fun ordinal(n: Int): String {
    val suffix = when {
        n in 11..13 -> "th"
        n % 10 == 1 -> "st"
        n % 10 == 2 -> "nd"
        n % 10 == 3 -> "rd"
        else -> "th"
    }
    return "$n$suffix"
}

val surfaceIconColors: IconButtonColors
    @Composable get() = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

val navButtonColors: IconButtonColors
    @Composable get() = IconButtonDefaults.filledTonalIconButtonColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )