package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ToolingCommand {
    GENERATE_CATALOG,
    VALIDATE_CATALOG,
    BENCHMARK_SYSTEM,
    REPLAY_SESSIONS,
    RUN_REGRESSION_SUITE,
    GENERATE_DATASET,
    COMPILED_STATS,
    MIGRATE_CATALOG,
    DIFF_CATALOGS,
    PACKAGE_CATALOG_ZIP
}

@Serializable
data class ToolingExecutionResult(
    val command: ToolingCommand,
    val isSuccess: Boolean,
    val executionTimeMs: Long,
    val summaryMessage: String,
    val outputFilePath: String? = null,
    val errorDetails: List<String> = emptyList()
)
