package com.intentflow.engine.session.di

import com.intentflow.engine.session.DefaultIntentSessionManager
import com.intentflow.engine.session.IntentSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindIntentSessionManager(impl: DefaultIntentSessionManager): IntentSessionManager
}
