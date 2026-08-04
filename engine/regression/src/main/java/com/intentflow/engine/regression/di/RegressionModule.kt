package com.intentflow.engine.regression.di

import com.intentflow.engine.regression.DefaultRegressionRunner
import com.intentflow.engine.regression.RegressionRunner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RegressionModule {

    @Binds
    @Singleton
    abstract fun bindRegressionRunner(impl: DefaultRegressionRunner): RegressionRunner
}
