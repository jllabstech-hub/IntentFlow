package com.intentflow.engine.reasoning.di

import com.intentflow.engine.reasoning.NoOpReasoningEngine
import com.intentflow.engine.reasoning.ReasoningEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReasoningModule {

    @Binds
    @Singleton
    abstract fun bindReasoningEngine(impl: NoOpReasoningEngine): ReasoningEngine
}
