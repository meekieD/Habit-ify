package com.dyusov.core.database.utils

import com.dyusov.core.database.entity.FrequencyType
import com.dyusov.core.database.entity.HabitDbModel

fun HabitDbModel.validate(): HabitDbModel {
    when (frequencyType) {
        FrequencyType.DAILY -> {
            check(weeklyDays == null && periodType == null && timesPerPeriod == null) {
                "DAILY habits should not have frequency-specific fields set"
            }
        }

        FrequencyType.WEEKLY -> {
            checkNotNull(weeklyDays) {
                "WEEKLY habits must have weeklyDays"
            }
            check(weeklyDays.isNotEmpty()) {
                "weeklyDays cannot be empty"
            }
            check(periodType == null && timesPerPeriod == null) {
                "WEEKLY habits should not have custom period fields"
            }
        }

        FrequencyType.CUSTOM -> {
            checkNotNull(periodType) {
                "CUSTOM habits must have periodType"
            }
            checkNotNull(timesPerPeriod) {
                "CUSTOM habits must have timesPerPeriod"
            }
            check(timesPerPeriod > 0) {
                "timesPerPeriod must be positive"
            }
            check(weeklyDays == null) {
                "CUSTOM habits should not have weeklyDays"
            }
        }
    }
    return this
}