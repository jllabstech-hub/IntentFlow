package com.intentflow.core.database.entity

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Entity(tableName = "domains")
data class DomainEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val description: String,
    val iconName: String?
)

@Entity(tableName = "intents")
data class IntentEntity(
    @PrimaryKey val intentId: String,
    val domainId: String,
    val displayName: String,
    val description: String,
    val deepLink: String?,
    val requiredPermissionsJson: String,
    val slotsJson: String,
    val exampleUtterancesJson: String,
    val metadataJson: String
)

@Fts4
@Entity(tableName = "utterances_fts")
data class UtteranceFtsEntity(
    val utterance: String,
    val intentId: String,
    val domainId: String
)
