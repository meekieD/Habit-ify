package com.dyusov.core.designsystem.di

import com.dyusov.core.designsystem.repo.ThemeRepository
import com.dyusov.core.designsystem.repo.ThemeRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface ThemeModule {

    @Binds
    @Singleton
    fun bindThemeRepository(
        impl: ThemeRepositoryImpl
    ): ThemeRepository
}