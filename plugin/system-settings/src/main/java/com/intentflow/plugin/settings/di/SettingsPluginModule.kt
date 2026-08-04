package com.intentflow.plugin.settings.di

import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.settings.SettingsPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SettingsPluginModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideSettingsPlugin(impl: SettingsPlugin): AndroidPlugin = impl
}
