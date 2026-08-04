package com.intentflow.plugin.calendar.di

import com.intentflow.plugin.api.AndroidPlugin
import com.intentflow.plugin.calendar.CalendarPlugin
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CalendarPluginModule {

    @Provides
    @IntoSet
    @Singleton
    fun provideCalendarPlugin(impl: CalendarPlugin): AndroidPlugin = impl
}
