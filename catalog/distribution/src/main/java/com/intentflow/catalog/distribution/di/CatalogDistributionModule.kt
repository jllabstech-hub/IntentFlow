package com.intentflow.catalog.distribution.di

import com.intentflow.catalog.distribution.CatalogManager
import com.intentflow.catalog.distribution.DefaultCatalogManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogDistributionModule {

    @Binds
    @Singleton
    abstract fun bindCatalogManager(impl: DefaultCatalogManager): CatalogManager
}
