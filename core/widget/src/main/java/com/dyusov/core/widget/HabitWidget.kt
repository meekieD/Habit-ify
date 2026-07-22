package com.dyusov.core.widget


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.createBitmap
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.layout.wrapContentWidth
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.nowClock
import com.dyusov.core.common.datetime.toLocalDate
import com.dyusov.core.common.utils.MyResult
import com.dyusov.core.model.HabitWithCompletions
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

enum class DayState { COMPLETED, EMPTY }

class HabitWidget : GlanceAppWidget() {

    companion object {
        private val DOT_CONTAINER_SIZE = 20.dp
        private val MIN_WIDTH_FOR_FULL_WEEK = 200.dp
        val HABIT_ID_KEY = longPreferencesKey("habit_id")
        val FORCE_UPDATE_KEY = longPreferencesKey("force_update_trigger")
    }

    override val sizeMode: SizeMode = SizeMode.Exact

    // Explicitly define that the state of this widget is stored in Preferences
    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val entryPoint = EntryPointAccessors.fromApplication(
            context.applicationContext,
            WidgetEntryPoint::class.java
        )

        provideContent {
            val prefs = currentState<Preferences>()
            val habitId = prefs[HABIT_ID_KEY]

            val widgetData: HabitWithCompletions? = if (habitId != null) {
                runBlocking {
                    val selectedHabit = entryPoint.getHabitWithCompletions().invoke(habitId).firstOrNull()
                    when (selectedHabit) {
                        is MyResult.Success -> selectedHabit.data
                        is MyResult.Error, null -> null
                    }
                }
            } else {
                null
            }

            GlanceTheme(GlanceTheme.colors) {
                if (widgetData != null) {
                    HabitWidgetContent(widgetData, entryPoint.dateTimeProvider())
                } else {
                    EmptyWidgetContent()
                }
            }
        }
    }

    @Composable
    fun EmptyWidgetContent() {
        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Tap to configure tracking",
                style = TextStyle(fontSize = 12.sp, color = GlanceTheme.colors.onSurfaceVariant)
            )
        }
    }

    @Composable
    fun HabitWidgetContent(widgetData: HabitWithCompletions, dateTimeProvider: DateTimeProvider) {

        // Calculate dynamic weekly date bounds (Mon - Sun)
        val today = LocalDate.nowClock()
        val daysFromMonday = today.dayOfWeek.ordinal // 0 (Monday) through 6 (Sunday)
        val startOfWeek = today.minus(daysFromMonday, DateTimeUnit.DAY)
        val weekDates = (0..6).map { startOfWeek.plus(it, DateTimeUnit.DAY) }

        // Map completion timestamps to matching LocalDates
        val completionDates = widgetData.completions
            .map { it.timestamp.toLocalDate(dateTimeProvider.timeZone()) }
            .toSet()

        // Match each day of the week to COMPLETED or EMPTY status
        val weekStates = weekDates.map { date ->
            if (completionDates.contains(date)) {
                DayState.COMPLETED
            } else {
                DayState.EMPTY
            }
        }

        val widgetSize = LocalSize.current
        val canShowFullWeek = widgetSize.width >= MIN_WIDTH_FOR_FULL_WEEK
        val habitColor = Color(widgetData.habit.color)

        Row(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(GlanceTheme.colors.surface)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = GlanceModifier
                    .padding(start = 4.dp)
                    .height(DOT_CONTAINER_SIZE)
                    .wrapContentWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = widgetData.habit.name,
                    style = TextStyle(fontSize = 14.sp)
                )
            }

            Row(
                modifier = GlanceModifier
                    .padding(start = 8.dp)
                    .defaultWeight()
                    .height(DOT_CONTAINER_SIZE),
                horizontalAlignment = Alignment.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (canShowFullWeek) {
                    weekStates.forEachIndexed { index, state ->
                        val targetDate = weekDates[index]
                        HabitDot(
                            state = state,
                            habitColor = habitColor,
                            trailingSpace = index < weekStates.size - 1,
                            isToday = targetDate == today
                        )
                    }
                } else {
                    val currentDayIndex = weekDates.indexOf(today)
                    if (currentDayIndex != -1) {
                        HabitDot(
                            state = weekStates[currentDayIndex],
                            habitColor = habitColor,
                            trailingSpace = false,
                            isToday = true
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun HabitDot(
        state: DayState,
        habitColor: Color,
        isToday: Boolean = false,
        trailingSpace: Boolean = false
    ) {
        val density = LocalContext.current.resources.displayMetrics.density

        val sizePx = (32 * density).toInt()
        val strokePx = (1 * density)

        val dotRadiusPx = (8 * density)
        val haloRadiusPx = if (isToday) (16 * density) else 0f

        val bitmap = remember(habitColor, state, isToday) {
            val isCompleted = state == DayState.COMPLETED
            val isEmpty = state == DayState.EMPTY

            val fillColor = when {
                isCompleted -> habitColor
                isEmpty -> Color.Transparent
                else -> Color.Black
            }
            val strokeColor = if (isEmpty) habitColor else Color.Transparent
            val strokeWidth = if (isEmpty) strokePx else 0f

            createCircleBitmap(
                sizePx = sizePx,
                dotRadiusPx = dotRadiusPx,
                fillColor = fillColor,
                strokeColor = strokeColor,
                strokeWidthPx = strokeWidth,
                haloRadiusPx = haloRadiusPx,
                haloColor = habitColor.copy(alpha = 0.15f)
            )
        }

        Box(
            contentAlignment = Alignment.CenterStart,
            modifier = GlanceModifier
                .width(if (trailingSpace) 24.dp else 20.dp)
                .height(20.dp)
        ) {
            Box(
                modifier = GlanceModifier
                    .size(20.dp)
                    .background(ImageProvider(bitmap))
            ) {}
        }
    }

    fun createCircleBitmap(
        sizePx: Int,
        dotRadiusPx: Float,
        fillColor: Color,
        strokeColor: Color,
        strokeWidthPx: Float,
        haloRadiusPx: Float = 0f,
        haloColor: Color = Color.Transparent
    ): Bitmap {
        val bmp = createBitmap(sizePx, sizePx)
        val canvas = Canvas(bmp)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val cx = sizePx / 2f
        val cy = sizePx / 2f

        if (haloRadiusPx > 0f) {
            paint.style = Paint.Style.FILL
            paint.color = haloColor.toArgb()
            canvas.drawCircle(cx, cy, haloRadiusPx, paint)
        }

        paint.style = Paint.Style.FILL
        paint.color = fillColor.toArgb()
        canvas.drawCircle(cx, cy, dotRadiusPx, paint)

        if (strokeWidthPx > 0f) {
            paint.style = Paint.Style.STROKE
            paint.color = strokeColor.toArgb()
            paint.strokeWidth = strokeWidthPx
            canvas.drawCircle(cx, cy, dotRadiusPx - strokeWidthPx / 2f, paint)
        }

        return bmp
    }
}