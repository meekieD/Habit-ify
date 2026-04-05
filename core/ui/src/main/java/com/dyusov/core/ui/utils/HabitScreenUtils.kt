package com.dyusov.core.ui.utils

import com.dyusov.core.ui.R
import kotlinx.datetime.DayOfWeek

object HabitScreenUtils {
    val days = listOf(
        DayOfWeek.MONDAY    to R.string.day_monday,
        DayOfWeek.TUESDAY   to R.string.day_tuesday,
        DayOfWeek.WEDNESDAY to R.string.day_wednesday,
        DayOfWeek.THURSDAY  to R.string.day_thursday,
        DayOfWeek.FRIDAY    to R.string.day_friday,
        DayOfWeek.SATURDAY  to R.string.day_saturday,
        DayOfWeek.SUNDAY    to R.string.day_sunday,
    )
}