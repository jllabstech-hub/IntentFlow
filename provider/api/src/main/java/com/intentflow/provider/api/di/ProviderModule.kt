package com.intentflow.provider.api.di

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.core.common.dispatcher.DispatcherProvider
import com.intentflow.provider.api.ClaudeProvider
import com.intentflow.provider.api.IntentExecutorProvider
import com.intentflow.provider.api.OpenAiProvider
import com.intentflow.provider.api.ProviderExecutionPipeline
import com.intentflow.provider.api.ProviderFactory
import com.intentflow.provider.api.ProviderManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideOpenAiProvider(): IntentExecutorProvider = OpenAiProvider()

    @Provides
    @IntoSet
    @Singleton
    fun provideClaudeProvider(): IntentExecutorProvider = ClaudeProvider()

    @Provides
    @Singleton
    fun provideProviderFactory(
        providers: Set<@JvmSuppressWildcards IntentExecutorProvider>
    ): ProviderFactory {
        val mock = providers.firstOrNull { it.providerId == "mock" } ?: OpenAiProvider()
        return ProviderFactory(mock, providers)
    }

    @Provides
    @Singleton
    fun provideProviderManager(factory: ProviderFactory): ProviderManager = ProviderManager(factory)

    @Provides
    @Singleton
    fun provideProviderExecutionPipeline(
        manager: ProviderManager
    ): ProviderExecutionPipeline = ProviderExecutionPipeline(manager, DefaultDispatcherProvider())
}
