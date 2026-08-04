package com.intentflow.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.intentflow.core.database.entity.DomainEntity
import com.intentflow.core.database.entity.IntentEntity
import com.intentflow.core.database.entity.UtteranceFtsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CatalogDao {

    @Query("SELECT * FROM domains")
    fun getAllDomains(): Flow<List<DomainEntity>>

    @Query("SELECT * FROM intents WHERE domainId = :domainId")
    fun getIntentsForDomain(domainId: String): Flow<List<IntentEntity>>

    @Query("SELECT * FROM intents WHERE intentId = :intentId")
    suspend fun getIntentById(intentId: String): IntentEntity?

    @Query("SELECT * FROM utterances_fts WHERE utterance MATCH :query LIMIT :limit")
    suspend fun searchUtterancesFts(query: String, limit: Int = 20): List<UtteranceFtsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDomains(domains: List<DomainEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntents(intents: List<IntentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUtterancesFts(utterances: List<UtteranceFtsEntity>)

    @Query("DELETE FROM domains")
    suspend fun clearAll()
}
