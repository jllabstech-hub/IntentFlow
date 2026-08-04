package com.intentflow.provider.evaluator.di

import com.intentflow.provider.evaluator.AiProviderEvaluator
import com.intentflow.provider.evaluator.DefaultAiProviderEvaluator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AiEvaluatorModule {

    @Binds
    @Singleton
    abstract fun bindAiProviderEvaluator(impl: DefaultAiProviderEvaluator): AiProviderEvaluator
}
