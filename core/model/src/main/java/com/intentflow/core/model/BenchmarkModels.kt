package com.intentflow.core.model

import kotlinx.serialization.Serializable

@Serializable
enum class ExportFormat {
    JSON,
    CSV,
    MARKDOWN,
    HTML
}

@Serializable
enum class ChartType {
    BAR_CHART,
    LINE_CHART,
    LATENCY_HISTOGRAM,
    RADAR_ACCURACY
}

@Serializable
data class BenchmarkScenario(
    val scenarioId: String,
    val name: String,
    val description: String,
    val testUtterances: List<String>,
    val targetProviderId: String? = null,
    val targetCatalogVersion: Int? = null,
    val targetSearchAlgorithm: String = "FTS5_HYBRID",
    val iterations: Int = 100
)

@Serializable
data class LatencyMetric(
    val p50Ms: Long,
    val p95Ms: Long,
    val p99Ms: Long,
    val minMs: Long,
    val maxMs: Long
)

@Serializable
data class MemoryMetric(
    val heapAllocatedMb: Float,
    val peakMemoryMb: Float,
    val gcCount: Int
)

@Serializable
data class BenchmarkChartSpec(
    val chartId: String,
    val title: String,
    val chartType: ChartType,
    val seriesData: Map<String, List<Float>>
)

@Serializable
data class BenchmarkReport(
    val reportId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val scenarioId: String,
    val totalUtterances: Int,
    val intentAccuracyPercent: Float,
    val slotAccuracyPercent: Float,
    val coldStartMs: Long,
    val offlinePerformanceScore: Float,
    val latency: LatencyMetric,
    val memory: MemoryMetric,
    val charts: List<BenchmarkChartSpec> = emptyList()
)
