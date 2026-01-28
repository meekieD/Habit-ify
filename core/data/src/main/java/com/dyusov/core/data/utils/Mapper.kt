package com.dyusov.core.data.utils

import com.dyusov.core.database.entity.FrequencyType
import com.dyusov.core.database.entity.HabitCompletionDbModel
import com.dyusov.core.database.entity.HabitDbModel
import com.dyusov.core.database.entity.HabitWithCompletionsDbModel
import com.dyusov.core.model.Habit
import com.dyusov.core.model.HabitCompletion
import com.dyusov.core.model.HabitFrequency
import com.dyusov.core.model.HabitWithCompletions
import com.dyusov.core.model.PeriodType
import java.time.DayOfWeek

object HabitColors {
    const val DEFAULT = 0xFFFFFFFF.toInt() // if no color, use white
}

fun HabitDbModel.toEntity(): Habit {
    return Habit(
        id = id,
        name = name,
        description = description,
        frequency = frequencyType.toHabitFrequency(weeklyDays, periodType, timesPerPeriod),
        color = color?.let {
            try {
                it.hexToInt()
            } catch (_: Exception) {
                HabitColors.DEFAULT
            }
        } ?: HabitColors.DEFAULT,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Habit.toDbModel(): HabitDbModel {
    return HabitDbModel(
        id = id,
        name = name,
        description = description,
        frequencyType = frequency.toFrequencyType(),
        weeklyDays = frequency.getWeeklyDays(),
        periodType = frequency.getPeriodType(),
        timesPerPeriod = frequency.getTimesPerPeriod(),
        createdAt = createdAt,
        updatedAt = updatedAt,
        color = color.let {
            try {
                it.toHexString()
            } catch (_: Exception) {
                HabitColors.DEFAULT.toHexString()
            }
        }
    )
}

fun HabitCompletionDbModel.toEntity(): HabitCompletion {
    return HabitCompletion(
        id = id,
        habitId = habitId,
        timestamp = timestamp
    )
}

fun HabitCompletion.toDbModel(): HabitCompletionDbModel {
    return HabitCompletionDbModel(
        id = id,
        habitId = habitId,
        timestamp = timestamp
    )
}

fun HabitWithCompletionsDbModel.toEntity(): HabitWithCompletions {
    return HabitWithCompletions(
        habit = habit.toEntity(),
        completions = completions.toEntities()
    )
}

fun HabitWithCompletions.toDbModel(): HabitWithCompletionsDbModel {
    return HabitWithCompletionsDbModel(
        habit = habit.toDbModel(),
        completions = completions.toDbModels()
    )
}

fun List<HabitDbModel>.toEntities(): List<Habit> {
    return map { it.toEntity() }
}

fun List<Habit>.toDbModels(): List<HabitDbModel> {
    return map { it.toDbModel() }
}

fun List<HabitCompletionDbModel>.toEntities(): List<HabitCompletion> {
    return map { it.toEntity() }
}

fun List<HabitCompletion>.toDbModels(): List<HabitCompletionDbModel> {
    return map { it.toDbModel() }
}

private fun FrequencyType.toHabitFrequency(
    weeklyDays: Set<DayOfWeek>?,
    periodType: PeriodType?,
    timesPerPeriod: Int?
): HabitFrequency {
    return when (this) {
        FrequencyType.DAILY -> HabitFrequency.Daily

        FrequencyType.WEEKLY -> HabitFrequency.Weekly(
            daysOfWeek = weeklyDays ?: emptySet()
        )

        FrequencyType.CUSTOM -> HabitFrequency.Custom(
            period = periodType ?: PeriodType.WEEK,
            timesPerPeriod = timesPerPeriod ?: 1
        )
    }
}

private fun HabitFrequency.toFrequencyType(): FrequencyType {
    return when (this) {
        is HabitFrequency.Daily -> FrequencyType.DAILY
        is HabitFrequency.Weekly -> FrequencyType.WEEKLY
        is HabitFrequency.Custom -> FrequencyType.CUSTOM
    }
}

private fun HabitFrequency.getWeeklyDays(): Set<DayOfWeek>? {
    return when (this) {
        is HabitFrequency.Weekly -> this.daysOfWeek
        else -> null
    }
}

private fun HabitFrequency.getPeriodType(): PeriodType? {
    return when (this) {
        is HabitFrequency.Custom -> this.period
        else -> null
    }
}

private fun HabitFrequency.getTimesPerPeriod(): Int? {
    return when (this) {
        is HabitFrequency.Custom -> this.timesPerPeriod
        else -> null
    }
}
