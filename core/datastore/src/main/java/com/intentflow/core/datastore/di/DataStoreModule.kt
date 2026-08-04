package com.intentflow.core.datastore.di

import android.content.Context
import com.intentflow.core.datastore.DataStorePreferenceRepository
import com.intentflow.core.datastore.PreferenceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun providePreferenceRepository(
        @ApplicationContext context: Context
    ): PreferenceRepository {
        return DataStorePreferenceRepository(context)
    }
}
