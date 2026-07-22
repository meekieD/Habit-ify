package com.dyusov.habit_ify.presentation.startup

import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.widget.HabitWidgetUpdateWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppStartupManager @Inject constructor(
    private val workManager: WorkManager,
    private val dateTimeProvider: DateTimeProvider
) {
    private val scope = CoroutineScope(Dispatchers.IO)

    fun startDailyDataRefresh() {
        scope.launch {
            val timeDiff = dateTimeProvider.getMillisToNextMidnight()

            val dailyWorkRequest = PeriodicWorkRequestBuilder<HabitWidgetUpdateWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "WidgetDailyUpdate",
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWorkRequest
            )
        }
    }
}