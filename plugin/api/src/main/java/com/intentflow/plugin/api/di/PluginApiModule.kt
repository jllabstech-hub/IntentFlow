package com.intentflow.plugin.api.di

import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.api.CapabilityExecutor
import com.intentflow.plugin.api.CapabilityRegistry
import com.intentflow.plugin.api.DefaultCapabilityRegistry
import com.intentflow.plugin.api.MapsPlugin
import com.intentflow.plugin.api.PluginCapabilityRegistry
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PluginApiModule {

    @Binds
    @Singleton
    abstract fun bindCapabilityRegistry(impl: DefaultCapabilityRegistry): CapabilityRegistry

    companion object {
        @Provides
        @IntoSet
        @Singleton
        fun provideMapsPlugin(impl: MapsPlugin): AndroidPlugin = impl

        @Provides
        @Singleton
        fun providePluginCapabilityRegistry(
            plugins: Set<@JvmSuppressWildcards AndroidPlugin>
        ): PluginCapabilityRegistry {
            return PluginCapabilityRegistry(plugins)
        }
    }
}
