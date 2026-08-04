package com.intentflow.catalog.validation.di

import com.intentflow.catalog.validation.CatalogValidationPipeline
import com.intentflow.catalog.validation.DefaultCatalogValidationPipeline
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogValidationModule {

    @Binds
    @Singleton
    abstract fun bindCatalogValidationPipeline(impl: DefaultCatalogValidationPipeline): CatalogValidationPipeline
}
