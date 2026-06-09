package com.dyusov.feature.habit.details.impl.utils

import android.content.Context
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dyusov.core.model.HabitFrequency
import com.dyusov.core.ui.utils.HabitScreenUtils
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

@Composable
fun streakDaysLabel(streak: Int): String {
    val lastTwo = streak % 100
    val textRes = when {
        lastTwo in 11..14 -> R.string.days_from_5_to_infinity
        lastTwo % 10 == 1 -> R.string.day
        lastTwo % 10 in 2..4 -> R.string.days_from_2_to_4
        else -> R.string.days_from_5_to_infinity
    }
    return stringResource(textRes)
}

val DayOfWeek.labelRes: Int
    get() = HabitScreenUtils.days.first { it.first == this }.second

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