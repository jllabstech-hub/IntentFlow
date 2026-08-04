package com.intentflow.catalog.generator.di

import com.intentflow.catalog.generator.CatalogDiffEngine
import com.intentflow.catalog.generator.CatalogGenerator
import com.intentflow.catalog.generator.DefaultCatalogGenerator
import com.intentflow.catalog.generator.UtteranceNormalizer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogGeneratorModule {

    @Binds
    @Singleton
    abstract fun bindCatalogGenerator(impl: DefaultCatalogGenerator): CatalogGenerator

    @Binds
    @Singleton
    abstract fun bindUtteranceNormalizer(impl: DefaultCatalogGenerator): UtteranceNormalizer

    @Binds
    @Singleton
    abstract fun bindCatalogDiffEngine(impl: DefaultCatalogGenerator): CatalogDiffEngine
}
