package com.intentflow.presentation.console.di

import com.intentflow.presentation.console.DefaultPlatformConsoleController
import com.intentflow.presentation.console.PlatformConsoleController
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ConsoleModule {

    @Binds
    @Singleton
    abstract fun bindPlatformConsoleController(impl: DefaultPlatformConsoleController): PlatformConsoleController
}
