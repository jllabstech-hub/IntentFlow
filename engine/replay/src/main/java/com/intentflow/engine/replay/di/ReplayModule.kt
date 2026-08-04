package com.intentflow.engine.replay.di

import com.intentflow.engine.replay.DefaultReplayTestSuiteRunner
import com.intentflow.engine.replay.ReplayTestSuiteRunner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReplayModule {

    @Binds
    @Singleton
    abstract fun bindReplayTestSuiteRunner(impl: DefaultReplayTestSuiteRunner): ReplayTestSuiteRunner
}
