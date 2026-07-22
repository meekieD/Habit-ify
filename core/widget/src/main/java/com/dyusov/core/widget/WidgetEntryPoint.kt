package com.dyusov.core.widget

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.domain.tracking.GetHabitWithCompletionsUseCase
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun getHabitWithCompletions(): GetHabitWithCompletionsUseCase
    fun dateTimeProvider(): DateTimeProvider

}