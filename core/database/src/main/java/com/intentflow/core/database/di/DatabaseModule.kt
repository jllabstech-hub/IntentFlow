package com.intentflow.core.database.di

import android.content.Context
import androidx.room.Room
import com.intentflow.core.database.CatalogDatabase
import com.intentflow.core.database.dao.CatalogDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideCatalogDatabase(
        @ApplicationContext context: Context
    ): CatalogDatabase {
        return Room.databaseBuilder(
            context,
            CatalogDatabase::class.java,
            "intentflow_catalog.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCatalogDao(database: CatalogDatabase): CatalogDao {
        return database.catalogDao()
    }
}
