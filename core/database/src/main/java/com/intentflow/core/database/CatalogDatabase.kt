package com.intentflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.intentflow.core.database.dao.CatalogDao
import com.intentflow.core.database.entity.DomainEntity
import com.intentflow.core.database.entity.IntentEntity
import com.intentflow.core.database.entity.UtteranceFtsEntity

@Database(
    entities = [
        DomainEntity::class,
        IntentEntity::class,
        UtteranceFtsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CatalogDatabase : RoomDatabase() {
    abstract fun catalogDao(): CatalogDao
}
