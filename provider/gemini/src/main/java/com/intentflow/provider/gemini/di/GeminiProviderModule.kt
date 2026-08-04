package com.intentflow.provider.gemini.di

import com.intentflow.provider.api.IntentExecutorProvider
import com.intentflow.provider.gemini.GeminiProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GeminiProviderModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideGeminiProvider(impl: GeminiProvider): IntentExecutorProvider = impl
}
