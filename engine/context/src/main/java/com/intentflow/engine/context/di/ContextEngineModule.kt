package com.intentflow.engine.context.di

import com.intentflow.core.common.dispatcher.DefaultDispatcherProvider
import com.intentflow.engine.context.ContextEngine
import com.intentflow.engine.context.DefaultContextEngine
import com.intentflow.engine.context.provider.AppKnowledgeProvider
import com.intentflow.engine.context.provider.CalendarKnowledgeProvider
import com.intentflow.engine.context.provider.ClipboardKnowledgeProvider
import com.intentflow.engine.context.provider.ContactsKnowledgeProvider
import com.intentflow.engine.context.provider.DriveKnowledgeProvider
import com.intentflow.engine.context.provider.HealthKnowledgeProvider
import com.intentflow.engine.context.provider.HistoryKnowledgeProvider
import com.intentflow.engine.context.provider.LocationKnowledgeProvider
import com.intentflow.engine.context.provider.PhotosKnowledgeProvider
import com.intentflow.engine.context.provider.TimeKnowledgeProvider
import com.intentflow.engine.context.provider.WeatherKnowledgeProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ContextEngineModule {

    @Provides
    @Singleton
    fun provideTimeKnowledgeProvider(): TimeKnowledgeProvider = TimeKnowledgeProvider()

    @Provides
    @Singleton
    fun provideLocationKnowledgeProvider(): LocationKnowledgeProvider = LocationKnowledgeProvider()

    @Provides
    @Singleton
    fun provideContactsKnowledgeProvider(): ContactsKnowledgeProvider = ContactsKnowledgeProvider()

    @Provides
    @Singleton
    fun provideClipboardKnowledgeProvider(): ClipboardKnowledgeProvider = ClipboardKnowledgeProvider()

    @Provides
    @Singleton
    fun provideCalendarKnowledgeProvider(): CalendarKnowledgeProvider = CalendarKnowledgeProvider()

    @Provides
    @Singleton
    fun provideAppKnowledgeProvider(): AppKnowledgeProvider = AppKnowledgeProvider()

    @Provides
    @Singleton
    fun provideHistoryKnowledgeProvider(): HistoryKnowledgeProvider = HistoryKnowledgeProvider()

    @Provides
    @Singleton
    fun providePhotosKnowledgeProvider(): PhotosKnowledgeProvider = PhotosKnowledgeProvider()

    @Provides
    @Singleton
    fun provideWeatherKnowledgeProvider(): WeatherKnowledgeProvider = WeatherKnowledgeProvider()

    @Provides
    @Singleton
    fun provideHealthKnowledgeProvider(): HealthKnowledgeProvider = HealthKnowledgeProvider()

    @Provides
    @Singleton
    fun provideDriveKnowledgeProvider(): DriveKnowledgeProvider = DriveKnowledgeProvider()

    @Provides
    @Singleton
    fun provideContextEngine(
        timeProvider: TimeKnowledgeProvider,
        contactProvider: ContactsKnowledgeProvider,
        clipboardProvider: ClipboardKnowledgeProvider,
        locationProvider: LocationKnowledgeProvider,
        appProvider: AppKnowledgeProvider,
        historyProvider: HistoryKnowledgeProvider,
        calendarProvider: CalendarKnowledgeProvider,
        photosProvider: PhotosKnowledgeProvider,
        weatherProvider: WeatherKnowledgeProvider,
        healthProvider: HealthKnowledgeProvider,
        driveProvider: DriveKnowledgeProvider
    ): ContextEngine {
        return DefaultContextEngine(
            timeProvider, contactProvider, clipboardProvider,
            locationProvider, appProvider, historyProvider,
            calendarProvider, photosProvider, weatherProvider,
            healthProvider, driveProvider, DefaultDispatcherProvider()
        )
    }
}
