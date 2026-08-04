package com.intentflow.engine.search.di

import com.intentflow.engine.search.DefaultIntentSearchRepository
import com.intentflow.engine.search.FtsSearchEngine
import com.intentflow.engine.search.IntentRanker
import com.intentflow.engine.search.IntentSearchRepository
import com.intentflow.engine.search.NoOpSemanticSearchProvider
import com.intentflow.engine.search.SearchEngine
import com.intentflow.engine.search.SemanticSearchProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchEngineModule {

    @Binds
    @Singleton
    abstract fun bindSearchEngine(
        impl: FtsSearchEngine
    ): SearchEngine

    @Binds
    @Singleton
    abstract fun bindIntentSearchRepository(
        impl: DefaultIntentSearchRepository
    ): IntentSearchRepository

    companion object {
        @Provides
        @Singleton
        fun provideSemanticSearchProvider(): SemanticSearchProvider {
            return NoOpSemanticSearchProvider()
        }

        @Provides
        @Singleton
        fun provideIntentRanker(): IntentRanker = IntentRanker()
    }
}
