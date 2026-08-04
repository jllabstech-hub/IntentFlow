package com.intentflow.engine.recorder.di

import com.intentflow.engine.recorder.DefaultIntentRecorder
import com.intentflow.engine.recorder.DefaultTraceReplayer
import com.intentflow.engine.recorder.IntentRecorder
import com.intentflow.engine.recorder.TraceReplayer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RecorderModule {

    @Binds
    @Singleton
    abstract fun bindIntentRecorder(impl: DefaultIntentRecorder): IntentRecorder

    @Binds
    @Singleton
    abstract fun bindTraceReplayer(impl: DefaultTraceReplayer): TraceReplayer
}
