package com.intentflow.engine.graph.di

import com.intentflow.engine.graph.DefaultIntentGraphEngine
import com.intentflow.engine.graph.GraphValidator
import com.intentflow.engine.graph.IntentGraphEngine
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class GraphModule {

    @Binds
    @Singleton
    abstract fun bindIntentGraphEngine(impl: DefaultIntentGraphEngine): IntentGraphEngine

    companion object {
        @Provides
        @Singleton
        fun provideGraphValidator(): GraphValidator = GraphValidator()
    }
}
