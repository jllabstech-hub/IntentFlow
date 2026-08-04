package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ConsoleTab {
    CATALOG_VIEWER,
    CATALOG_SEARCH,
    INTENT_REPLAY,
    BENCHMARK_RUNNER,
    DATASET_GENERATOR,
    CATALOG_VALIDATOR,
    STATISTICS_DASHBOARD,
    PROVIDER_COMPARISON,
    CATALOG_VERSION_COMPARE,
    REPORT_EXPORTER
}

@Serializable
data class ConsoleState(
    val activeTab: ConsoleTab = ConsoleTab.STATISTICS_DASHBOARD,
    val selectedCatalogVersion: Int = 1,
    val isTaskRunning: Boolean = false,
    val statusMessage: String = "Platform Console Ready",
    val activeReportId: String? = null
)
