package com.dyusov.feature.habit.details.impl.utils

import android.content.Context
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.dyusov.core.model.HabitFrequency
import com.dyusov.feature.habit.details.impl.R
import kotlinx.datetime.DayOfWeek
import java.util.Locale

fun frequencyLabel(context: Context, frequency: HabitFrequency): String = when (frequency) {
    is HabitFrequency.Daily -> {
        context.getString(R.string.frequency_label_daily)
    }

    is HabitFrequency.Weekly -> {
        val days = frequency.daysOfWeek
            .sortedBy { it.ordinal }
            .joinToString(", ") {
                context.getString(it.labelRes)
            }
        context.getString(R.string.frequency_label_weekly, days)
    }

    is HabitFrequency.Custom -> {
        val days = frequency.daysOfMonth
            .sorted()
            .joinToString(", ") {
                ordinal(it)
            }
        context.getString(R.string.frequency_label_monthly, days)
    }
}

fun ordinal(n: Int, locale: Locale = Locale.getDefault()): String {
    return if (locale.language == "ru") {
        "$n-е"
    } else {
        val suffix = when {
            n in 11..13 -> "th"
            n % 10 == 1 -> "st"
            n % 10 == 2 -> "nd"
            n % 10 == 3 -> "rd"
            else -> "th"
        }
        "$n$suffix"
    }
}

val DayOfWeek.labelRes: Int
    get() = when (this) {
        DayOfWeek.MONDAY -> R.string.day_monday
        DayOfWeek.TUESDAY -> R.string.day_tuesday
        DayOfWeek.WEDNESDAY -> R.string.day_wednesday
        DayOfWeek.THURSDAY -> R.string.day_thursday
        DayOfWeek.FRIDAY -> R.string.day_friday
        DayOfWeek.SATURDAY -> R.string.day_saturday
        DayOfWeek.SUNDAY -> R.string.day_sunday
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