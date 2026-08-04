package com.intentflow.provider.mock.di

import com.intentflow.provider.api.IntentExecutorProvider
import com.intentflow.provider.mock.MockProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MockProviderModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideMockProviderIntoSet(impl: MockProvider): IntentExecutorProvider = impl
}
