package com.intentflow.engine.execution.di

import com.intentflow.engine.execution.DefaultExecutionEngine
import com.intentflow.engine.execution.ExecutionEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ExecutionModule {

    @Binds
    @Singleton
    abstract fun bindExecutionEngine(impl: DefaultExecutionEngine): ExecutionEngine
}
