package com.dyusov.core.data.di

import com.dyusov.core.common.datetime.DateTimeProvider
import com.dyusov.core.common.datetime.SystemDateTimeProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DateTimeModule {

    @Binds
    @Singleton
    fun bindDateTimeProvider(
        impl: SystemDateTimeProvider
    ): DateTimeProvider
}