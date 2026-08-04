package com.intentflow.engine.memory.di

import com.intentflow.engine.memory.DefaultIntentMemoryEngine
import com.intentflow.engine.memory.IntentMemoryEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MemoryModule {

    @Binds
    @Singleton
    abstract fun bindIntentMemoryEngine(impl: DefaultIntentMemoryEngine): IntentMemoryEngine
}
