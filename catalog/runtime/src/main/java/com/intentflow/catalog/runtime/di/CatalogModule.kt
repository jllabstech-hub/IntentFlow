package com.intentflow.catalog.runtime.di

import com.intentflow.catalog.api.CatalogCache
import com.intentflow.catalog.api.CatalogLoader
import com.intentflow.catalog.api.CatalogRepository
import com.intentflow.catalog.api.CatalogValidator
import com.intentflow.catalog.api.CatalogVersionManager
import com.intentflow.catalog.runtime.CatalogJsonParser
import com.intentflow.catalog.runtime.DefaultCatalogLoader
import com.intentflow.catalog.runtime.DefaultCatalogRepository
import com.intentflow.catalog.runtime.DefaultCatalogValidator
import com.intentflow.catalog.runtime.DefaultCatalogVersionManager
import com.intentflow.catalog.runtime.ThreadSafeCatalogCache
import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.core.common.dispatcher.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogModule {

    @Binds
    @Singleton
    abstract fun bindCatalogValidator(
        impl: DefaultCatalogValidator
    ): CatalogValidator

    @Binds
    @Singleton
    abstract fun bindCatalogCache(
        impl: ThreadSafeCatalogCache
    ): CatalogCache

    @Binds
    @Singleton
    abstract fun bindCatalogVersionManager(
        impl: DefaultCatalogVersionManager
    ): CatalogVersionManager

    @Binds
    @Singleton
    abstract fun bindCatalogLoader(
        impl: DefaultCatalogLoader
    ): CatalogLoader

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(
        impl: DefaultCatalogRepository
    ): CatalogRepository

    companion object {
        @Provides
        @Singleton
        fun provideJson(): Json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = false
        }

        @Provides
        @Singleton
        fun provideDispatcherProvider(): DispatcherProvider = DefaultDispatcherProvider()
    }
}
