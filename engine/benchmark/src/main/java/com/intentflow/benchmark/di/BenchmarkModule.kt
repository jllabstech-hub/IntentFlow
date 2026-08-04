package com.intentflow.engine.benchmark.di

import com.intentflow.engine.benchmark.BenchmarkRunner
import com.intentflow.engine.benchmark.DefaultBenchmarkRunner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BenchmarkModule {

    @Binds
    @Singleton
    abstract fun bindBenchmarkRunner(impl: DefaultBenchmarkRunner): BenchmarkRunner
}
