package com.intentflow.provider.gemma.di

import android.content.Context
import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.provider.api.IntentExecutorProvider
import com.intentflow.provider.gemma.GemmaModelManager
import com.intentflow.provider.gemma.GemmaProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GemmaProviderModule {

    @Provides
    @Singleton
    fun provideGemmaModelManager(
        @ApplicationContext context: Context
    ): GemmaModelManager {
        return GemmaModelManager(context, DefaultDispatcherProvider())
    }

    @Provides
    @IntoSet
    @Singleton
    fun provideGemmaProvider(
        modelManager: GemmaModelManager
    ): IntentExecutorProvider {
        return GemmaProvider(modelManager)
    }
}
