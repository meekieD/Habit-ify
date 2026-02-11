package com.dyusov.core.data.di

import com.dyusov.core.data.repo.HabitCompletionRepository
import com.dyusov.core.data.repo.HabitCompletionRepositoryImpl
import com.dyusov.core.data.repo.HabitRepository
import com.dyusov.core.data.repo.HabitRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindHabitRepository(
        impl: HabitRepositoryImpl
    ): HabitRepository

    @Binds
    @Singleton
    fun bindHabitCompletionRepository(
        impl: HabitCompletionRepositoryImpl
    ): HabitCompletionRepository
}